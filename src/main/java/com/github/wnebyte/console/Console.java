package com.github.wnebyte.console;

import com.github.wnebyte.console.util.GUIUtils;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.TwoDimensional;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;
import java.util.*;
import java.util.function.Consumer;
import static com.github.wnebyte.console.util.StringUtils.*;
import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyCode.DOWN;
import static org.fxmisc.wellbehaved.event.EventPattern.*;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;

/**
 * This class is a css-styleable FX Console.
 */
public class Console extends BorderPane {

    /**
     * This enum represents predefined css resources.
     */
    public enum Style {
        WIN,
        LINUX
    }

    public static final String ERROR_STYLE = "error";

    public static final String PREFIX_STYLE = "prefix";

    public static final List<String> ERROR_STYLE_CLASSES = Collections.singletonList(ERROR_STYLE);

    private final StyleClassedTextArea area = new StyleClassedTextArea();

    private final VirtualizedScrollPane<StyleClassedTextArea> scrollPane =
            new VirtualizedScrollPane<>(area);

    private final List<String> history = new ArrayList<String>();

    private int historyPointer = 0;

    private Consumer<String> callback;

    private String prefix;

    private List<String> prefixStyleClasses = new ArrayList<>(){ { add(PREFIX_STYLE); } };

    public Console() {
        setCenter(scrollPane);
        area.setWrapText(true);
        area.setEditable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        style(Style.WIN);
        build();
    }

    public Console(final Style style) {
        this();
        style(style);
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
    }

    private void style(final Style style) {
        String resource = (style == Style.WIN) ? "/css/win.css" : "/css/linux.css";
        getStylesheets().clear();
        getStylesheets().add(getClass().getResource(resource).toExternalForm());
    }

    private Consumer<? super KeyEvent> onEnter() {
        return new Consumer<>() {
            @Override
            public void accept(KeyEvent keyEvent) {
                String text = area.getText(area.getCurrentParagraph());
                text = (prefix != null && text.startsWith(prefix)) ? text.substring(prefix.length()) : text;
                ln();

                if (nonEmpty(text)) {
                    history.add(text);
                    history.remove("");
                    history.add("");
                    historyPointer = history.size() - 1;

                    if (callback != null) {
                        callback.accept(text);
                    }
                }
                ready();
            }
        };
    }

    private Consumer<? super KeyEvent> onBackSpace() {
        return new Consumer<>() {
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
        return new Consumer<>() {
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
        return new Consumer<>() {
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
        return new Consumer<>() {
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
        return new Consumer<>() {
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
        return new Consumer<>() {
            @Override
            public void accept(MouseEvent e) {
                if (area.getContextMenu() != null) {
                    GUIUtils.runSafe(() -> area.getContextMenu().hide());
                }
            }
        };
    }

    private Consumer<? super MouseEvent> onSecondary() {
        return new Consumer<>() {
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
     * @return this Console.
     */
    public final Console print(final String text) {
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
        return this;
    }

    /**
     * Prints the specified <code>text</code> to the console at the current caret position
     * with the specified <code>styleClasses</code>.
     * @param text to be print.
     * @param styleClasses to be applied to the text.
     * @return this Console.
     */
    public final Console print(final String text, final String... styleClasses) {
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
        return this;
    }

    /**
     * Prints the specified <code>text</code> and a new line to the console at the current caret position
     * with default <code>styleClasses</code>.
     * @param text to be print.
     * @return this Console.
     */
    public final Console println(final String text) {
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
            ln();
        });
        return this;
    }

    /**
     * Prints the specified <code>text</code> and a new line to the console at the current caret position
     * with the specified <code>styleClasses</code>.
     * @param text to be print.
     * @param styleClasses to be applied to the text.
     * @return this Console.
     */
    public final Console println(final String text, final String... styleClasses) {
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
            ln();
        });
        return this;
    }

    /**
     * Prints the specified <code>text</code> and a new line to the console
     * with {@linkplain Console#ERROR_STYLE_CLASSES} <code>styleClasses</code>.
     * @param text to be print.
     * @return this Console.
     */
    public final Console printerr(final String text) {
        GUIUtils.runSafe(() -> {
            toParagraphs(replaceLineSeparator(text)).forEach(out -> {
                if (out.equals("\n")) {
                    ln();
                }
                else {
                    area.appendText(out);
                    int to = area.getText(area.getCurrentParagraph()).length();
                    int from = to - out.length();
                    area.setStyle(area.getCurrentParagraph(), from, to, ERROR_STYLE_CLASSES);
                }
            });
            ln();
        });
        return this;
    }

    /**
     * Prints a new line to the console.
     * @return this Console.
     */
    public final Console ln() {
        GUIUtils.runSafe(() -> {
            area.appendText(System.lineSeparator());
            area.clearStyle(area.getCurrentParagraph());
            scrollToBottom();
        });
        return this;
    }

    /**
     * Prints this console's <code>prefix</code> to the console at the beginning of the current
     * paragraph if one has been specified.
     */
    public final void ready() {
        GUIUtils.runSafe(() -> {
            String text = (prefix != null) ? prefix : "";
            area.insertText(area.getCurrentParagraph(), 0, text);
            area.setStyle(area.getCurrentParagraph(), 0, text.length() - 1, prefixStyleClasses);
            scrollToBottom();
        });
    }

    /**
     * Clears the console of text.
     */
    public final void clear() {
        GUIUtils.runSafe(area::clear);
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

    public final void setPrefix(String prefix) {
        if (isNullOrEmpty(prefix)) {
            throw new IllegalArgumentException(
                    "Prefix may to be null or empty"
            );
        }
        prefix = removeLineSeparators(prefix);
        this.prefix = (prefix.endsWith("\\s")) ? prefix : prefix.concat(" ");
    }

    public final void setPrefixStyleClasses(final List<String> prefixStyleClasses) {
        if ((prefixStyleClasses != null) && (prefixStyleClasses.size() != 0)) {
            this.prefixStyleClasses = prefixStyleClasses;
        }
    }

    /**
     * @return the exclusive min column position of the current paragraph.
     */
    private int minMinor() {
        return (prefix != null && area.getText(area.getCurrentParagraph())
                .startsWith(prefix)) ? prefix.length() : 0;
    }

    /**
     * Scrolls the <code>area</code> vertically to the bottom.
     */
    private void scrollToBottom() {
        area.scrollYBy(Double.MAX_VALUE);
    }
}