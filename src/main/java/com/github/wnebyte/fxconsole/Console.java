package com.github.wnebyte.fxconsole;

import com.github.wnebyte.fxconsole.util.CollectionUtils;
import com.github.wnebyte.fxconsole.util.GUIUtils;
import com.github.wnebyte.fxconsole.util.StyledTextBuilder;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.PlainTextChange;
import org.fxmisc.richtext.model.TwoDimensional;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import static com.github.wnebyte.fxconsole.util.StringUtils.*;
import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyCode.DOWN;
import static org.fxmisc.wellbehaved.event.EventPattern.*;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;

/**
 * This class is a css-styleable FX Console.
 */
public class Console extends BorderPane {

    /**
     * This enum represent predefined css resources.
     */
    public enum Style {
        WIN,
        LINUX
    }

    public static final String ERROR_STYLE = "error";

    public static final List<String> ERROR_STYLE_CLASSES = Collections.singletonList(ERROR_STYLE);

    private static final char MASK = '*';

    private static final String MASK_CMD = ":mask";

    private final Object lock = new Object();

    private final StyleClassedTextArea area = new StyleClassedTextArea();

    private final VirtualizedScrollPane<StyleClassedTextArea> scrollPane =
            new VirtualizedScrollPane<>(area);

    private final List<String> history = new ArrayList<String>();

    private int historyPointer = 0;

    private Consumer<String> callback;

    private StyledText prefix;

    private final BooleanProperty ignoreMask = new SimpleBooleanProperty(true);

    private final LinkedList<Character> buffer = new LinkedList<>();

    public Console() {
        this(Style.WIN);
    }

    public Console(final Style style) {
        setCenter(scrollPane);
        area.setWrapText(true);
        area.setEditable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        style(style);
        build();
    }

    private void build() {
        Nodes.addInputMap(area, InputMap.consume(keyPressed(ENTER), onEnter()));
        Nodes.addInputMap(area, InputMap.consume(keyPressed(BACK_SPACE), onBackSpace()));
        Nodes.addInputMap(area, InputMap.consume(keyPressed(LEFT), onLeft()));
        Nodes.addInputMap(area, InputMap.consume(keyPressed(RIGHT), onRight()));
        Nodes.addInputMap(area, InputMap.consume(keyPressed(UP), onUp()));
        Nodes.addInputMap(area, InputMap.consume(keyPressed(DOWN), onDown()));
        Nodes.addInputMap(area, InputMap.consume(mousePressed(MouseButton.PRIMARY), onPrimary()));
        Nodes.addInputMap(area, InputMap.consume(mousePressed(MouseButton.SECONDARY), onSecondary()));
        Nodes.addInputMap(area, InputMap.ignore(mouseClicked()));
        Nodes.addInputMap(area, InputMap.ignore(mouseReleased()));
        Nodes.addInputMap(area, InputMap.ignore(mouseDragged()));
        Nodes.addInputMap(area, InputMap.ignore(keyPressed("A", KeyCodeCombination.CONTROL_DOWN)));
        Nodes.addInputMap(area, InputMap.ignore(keyPressed("Z", KeyCodeCombination.CONTROL_DOWN)));
        Nodes.addInputMap(area, InputMap.ignore(keyPressed("V", KeyCodeCombination.CONTROL_DOWN)));
        area.getUndoManager().close();
        area.multiPlainChanges()
                .successionEnds(Duration.ofMillis(10))
                .subscribe(del());
        area.multiPlainChanges()
                .suppressWhen(ignoreMask)
                .subscribe(mask());

    }

    private void style(final Style style) {
        String resource = (style == Style.WIN) ? "/css/win.css" : "/css/linux.css";
        getStylesheets().add(getClass().getResource(resource).toExternalForm());
    }

    /**
     * Scans the tail of the text from the current paragraph for {@link Console#MASK_CMD} occurrence.
     */
    private Consumer<List<PlainTextChange>> del() {
        return new Consumer<List<PlainTextChange>>() {
            @Override
            public void accept(List<PlainTextChange> plainTextChanges) {
                String text = area.getText(area.getCurrentParagraph());
                int len = text.length();
                if (text.endsWith(MASK_CMD)) {
                    ignoreMask.setValue(false);
                    area.deleteText(area.getCurrentParagraph(), Math.max(minMinor(), len - MASK_CMD.length()),
                            area.getCurrentParagraph(), len);
                }
            }
        };
    }

    /**
     * Replaces any appended text with the {@linkplain Console#MASK} char.
     */
    private Consumer<List<PlainTextChange>> mask() {
        return new Consumer<List<PlainTextChange>>() {
            @Override
            public void accept(List<PlainTextChange> plainTextChanges) {
                for (PlainTextChange plainTextChange : plainTextChanges) {
                    String inserted = plainTextChange.getInserted();
                    String removed = plainTextChange.getRemoved();

                    if ((inserted.length() != 0) && (removed.length() != 0)) {
                        continue;
                    }
                    if (0 < inserted.length()) {
                        char[] arr = inserted.toCharArray();
                        for (char c : arr) {
                            buffer.add(c);
                        }
                        area.replaceText(
                                plainTextChange.getPosition(),
                                plainTextChange.getInsertionEnd(),
                                String.valueOf(MASK)
                        );
                    }
                    if (0 < removed.length()) {
                        if (!(buffer.isEmpty())) {
                            for (int i = 0; i < removed.length(); i++) {
                                buffer.removeLast();
                            }
                        }
                    }
                }
            }
        };
    }

    private Consumer<? super KeyEvent> onEnter() {
        return new Consumer<KeyEvent>() {
            @Override
            public void accept(KeyEvent keyEvent) {
                boolean buffered;
                // get ln of text
                String text = area.getText(area.getCurrentParagraph());
                // remove potential prefix
                text = (prefix != null && text.startsWith(prefix.getLast().getKey())) ?
                        text.substring(prefix.getLast().getKey().length()) :
                        text;
                // if mask buffer is populated
                if (buffered = (0 < buffer.size())) {
                    String buff = new String(CollectionUtils.toCharArray(buffer));
                    // replace masked chars with contents of buffer
                    text = replaceSequence(text, buff, MASK);
                    // clear buffer
                    buffer.clear();
                }
                ignoreMask.setValue(true);
                ln();

                if (nonEmpty(text)) {
                    if (!buffered) {
                        history.add(text);
                        history.remove("");
                        history.add("");
                        historyPointer = history.size() - 1;
                    }
                    if (callback != null) {
                        callback.accept(text);
                    }
                }
                ready();
            }
        };
    }

    private Consumer<? super KeyEvent> onBackSpace() {
        return new Consumer<KeyEvent>() {
            @Override
            public void accept(KeyEvent keyEvent) {
                scrollToBottom();
                int minor = area.offsetToPosition(area.getCaretPosition(), TwoDimensional.Bias.Backward)
                        .getMinor();
                if (minMinor() < minor) {
                    area.deletePreviousChar();
                }
            }
        };
    }

    private Consumer<? super KeyEvent> onLeft() {
        return new Consumer<KeyEvent>() {
            @Override
            public void accept(KeyEvent keyEvent) {
                scrollToBottom();
                int major = area.offsetToPosition(area.getCaretPosition(), TwoDimensional.Bias.Backward)
                        .getMajor();
                int minor = area.offsetToPosition(area.getCaretPosition(), TwoDimensional.Bias.Backward)
                        .getMinor();
                if (minMinor() < minor) {
                    area.moveTo(major, minor - 1);
                }
            }
        };
    }

    private Consumer<? super KeyEvent> onRight() {
        return new Consumer<KeyEvent>() {
            @Override
            public void accept(KeyEvent keyEvent) {
                scrollToBottom();
                int major = area.offsetToPosition(area.getCaretPosition(), TwoDimensional.Bias.Forward)
                        .getMajor();
                int minor = area.offsetToPosition(area.getCaretPosition(), TwoDimensional.Bias.Forward)
                        .getMinor();
                if (minor != area.getParagraphLength(major)) {
                    area.moveTo(major, minor + 1);
                }
            }
        };
    }

    private Consumer<? super KeyEvent> onUp() {
        return new Consumer<KeyEvent>() {
            @Override
            public void accept(KeyEvent keyEvent) {
                scrollToBottom();
                if (historyPointer == 0) {
                    return;
                }
                historyPointer--;
                GUIUtils.runSafe(() -> {
                    area.replaceText(area.getCurrentParagraph(), minMinor(), area.getCurrentParagraph(),
                            area.getParagraphLength(area.getCurrentParagraph()), history.get(historyPointer));
                });
            }
        };
    }

    private Consumer<? super KeyEvent> onDown() {
        return new Consumer<KeyEvent>() {
            @Override
            public void accept(KeyEvent keyEvent) {
                scrollToBottom();
                if (historyPointer >= history.size() - 1) {
                    return;
                }
                historyPointer++;
                GUIUtils.runSafe(() -> {
                    area.replaceText(area.getCurrentParagraph(), minMinor(), area.getCurrentParagraph(),
                            area.getParagraphLength(area.getCurrentParagraph()), history.get(historyPointer));
                });
            }
        };
    }

    private Consumer<? super MouseEvent> onPrimary() {
        return new Consumer<MouseEvent>() {
            @Override
            public void accept(MouseEvent e) {
                if (area.getContextMenu() != null) {
                    GUIUtils.runSafe(() -> area.getContextMenu().hide());
                }
            }
        };
    }

    private Consumer<? super MouseEvent> onSecondary() {
        return new Consumer<MouseEvent>() {
            @Override
            public void accept(MouseEvent e) {
                if (area.getContextMenu() != null) {
                    GUIUtils.runSafe(() -> area.getContextMenu().show(area, e.getScreenX(), e.getScreenY()));
                }
            }
        };
    }

    /**
     * Prints the specified <code>text</code> to the console at the current caret position
     * with default <code>styleClasses</code>.
     * @param text to be print.
     */
    public void print(final String text) {
        synchronized (lock) {
            GUIUtils.runSafe(() -> {
                toParagraphs(replaceLineSeparator(text)).forEach(out -> {
                    if (out.equals("\n")) {
                        ln();
                    }
                    else {
                        area.appendText(out);
                        int to = area.getText(area.getCurrentParagraph()).length();
                        int from = to - out.length();
                        area.clearStyle(area.getCurrentParagraph(), from, to);
                    }
                });
            });
        }
    }

    /**
     * Prints the specified <code>text</code> to the console at the current caret position
     * with the specified <code>styleClasses</code>.
     * @param text to be print.
     * @param styleClasses to be applied to the text.
     */
    public void print(final String text, final String... styleClasses) {
        synchronized (lock) {
            GUIUtils.runSafe(() -> {
                toParagraphs(replaceLineSeparator(text)).forEach(out -> {
                    if (out.equals("\n")) {
                        ln();
                    }
                    else {
                        area.appendText(out);
                        int to = area.getText(area.getCurrentParagraph()).length();
                        int from = to - out.length();
                        area.setStyle(area.getCurrentParagraph(), from, to, Arrays.asList(styleClasses));
                    }
                });
            });
        }
    }

    /**
     * Prints the specified <code>styledText</code> to the console at the current position.
     * @param styledText the text and style to be print.
     */
    public void print(final StyledText styledText) {
        synchronized (lock) {
            GUIUtils.runSafe(() -> {
                styledText.getTextSequences().forEach(out -> {
                    print(out.getKey(), out.getValue().toArray(new String[0]));
                });
            });
        }
    }

    /**
     * Prints the specified <code>text</code> and a new line to the console at the current caret position
     * with default <code>styleClasses</code>.
     * @param text to be print.
     */
    public void println(final String text) {
        synchronized (lock) {
            GUIUtils.runSafe(() -> {
                print(text);
                ln();
            });
        }
    }

    /**
     * Prints the specified <code>text</code> and a new line to the console at the current caret position
     * with the specified <code>styleClasses</code>.
     * @param text to be print.
     * @param styleClasses to be applied to the text.
     */
    public void println(final String text, final String... styleClasses) {
        synchronized (lock) {
            GUIUtils.runSafe(() -> {
                print(text, styleClasses);
                ln();
            });
        }
    }

    public void println(final StyledText styledText) {
        synchronized (lock) {
            GUIUtils.runSafe(() -> {
                print(styledText);
                ln();
            });
        }
    }

    /**
     * Prints the specified <code>text</code> and a new line to the console
     * with {@linkplain Console#ERROR_STYLE_CLASSES} <code>styleClasses</code>.
     * @param text to be print.
     */
    public void printerr(final String text) {
        synchronized (lock) {
            GUIUtils.runSafe(() -> {
                print(text, Console.ERROR_STYLE_CLASSES.toArray(new String[0]));
                ln();
            });
        }
    }

    /**
     * Prints a new line to the console.
     */
    public void ln() {
        synchronized (lock) {
            GUIUtils.runSafe(() -> {
                area.appendText(System.lineSeparator());
                area.clearStyle(area.getCurrentParagraph());
                scrollToBottom();
            });
        }
    }

    /**
     * Prints this console's <code>prefix</code> to the console at the beginning of the current
     * paragraph if one has been specified.
     */
    // Todo: write an insert method
    public void ready() {
        synchronized (lock) {
            GUIUtils.runSafe(() -> {
                if (prefix != null) {
                    print(prefix);
                }
            });
        }
    }

    /**
     * Clears the console of text.
     */
    public final void clear() {
        synchronized (lock) {
            GUIUtils.runSafe(area::clear);
        }
    }

    /**
     * Locks the console.
     */
    public final void lock() {
        GUIUtils.runSafe(() -> area.setEditable(false));
    }

    /**
     * Unlocks the console.
     */
    public final void unlock() {
        GUIUtils.runSafe(() -> area.setEditable(true));
    }

    /**
     * @return <code>true</code> if the console is locked,
     * otherwise <code>false</code>.
     */
    public final boolean isLocked() {
        return area.isEditable();
    }

    /**
     * Sets the <code>wrapText</code> property of the console to the specified <code>value</code>.
     * @param value to use.
     */
    public final void setWrapText(final boolean value) {
        GUIUtils.runSafe(() -> area.setWrapText(value));
    }

    /**
     * @return <code>true</code> if the <code>wrapText</code> property is set to <code>true</code>,
     * otherwise <code>false</code>.
     */
    public final boolean isWrapText() {
        return area.isWrapText();
    }

    public final void setVbarPolicy(final ScrollPane.ScrollBarPolicy vBarPolicy) {
        GUIUtils.runSafe(() -> scrollPane.setVbarPolicy(vBarPolicy));

    }

    public final ScrollPane.ScrollBarPolicy getVbarPolicy() {
        return scrollPane.getVbarPolicy();
    }

    public final void setHBarPolicy(final ScrollPane.ScrollBarPolicy hBarPolicy) {
        GUIUtils.runSafe(() -> scrollPane.setHbarPolicy(hBarPolicy));
    }

    public final ScrollPane.ScrollBarPolicy getHbarPolicy() {
        return scrollPane.getHbarPolicy();
    }

    /**
     * Clears the console's <code>history</code>.
     */
    public final void clearHistory() {
        history.clear();
        historyPointer = 0;
    }

    public final ContextMenu getContextMenu() {
        return area.getContextMenu();
    }

    public final void setContextMenu(final ContextMenu contextMenu) {
        area.setContextMenu(contextMenu);
    }

    /**
     * Specify the <code>Consumer{@literal <}String{@literal >}<String></code> to be called when
     * new input has been appended to the console.
     * @param callback to be set.
     */
    public final void setCallback(final Consumer<String> callback) {
        this.callback = callback;
    }

    public final void setPrefix(final StyledText prefix) {
        if ((prefix == null) || (prefix.getTextSequences().isEmpty())) {
            throw new IllegalArgumentException(
                    "Prefix if specified may not be null"
            );
        }
        this.prefix = (prefix.getLast().getKey().endsWith("\\s")) ?
                prefix : new StyledTextBuilder(prefix).whitespace().build();
    }

    /**
     * @return the exclusive min column position of the current paragraph.
     */
    private int minMinor() {
        return (prefix != null && area.getText(area.getCurrentParagraph())
                .startsWith(prefix.getLast().getKey())) ? prefix.getLast().getKey().length() : 0;
    }

    /**
     * Scrolls the <code>area</code> vertically to the bottom.
     */
    private void scrollToBottom() {
        area.scrollYBy(Double.MAX_VALUE);
    }
}