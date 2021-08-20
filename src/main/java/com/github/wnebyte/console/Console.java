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
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import static com.github.wnebyte.console.util.StringUtils.isNullOrEmpty;
import static com.github.wnebyte.console.util.StringUtils.nonEmpty;
import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyCode.DOWN;
import static org.fxmisc.wellbehaved.event.EventPattern.*;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;

public class Console extends BorderPane {

    public static final List<String> ERROR_STYLE = Collections.singletonList("error");

    private final StyleClassedTextArea area = new StyleClassedTextArea();

    private final VirtualizedScrollPane<StyleClassedTextArea> scrollPane =
            new VirtualizedScrollPane<>(area);

    private final List<String> history = new ArrayList<String>();

    private int historyPointer = 0;

    private Consumer<String> callback;

    private String prefix;

    public Console() {
        setCenter(scrollPane);
        area.setWrapText(true);
        area.setEditable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        getStylesheets().add(getClass().getResource("/css/win.css").toExternalForm());
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
    }

    private Consumer<? super KeyEvent> onEnter() {
        return new Consumer<>() {
            @Override
            public void accept(KeyEvent keyEvent) {
                String text = area.getText(area.getCurrentParagraph());
                text = (prefix != null && text.startsWith(prefix)) ? text.substring(prefix.length()) : text;
                area.appendText(System.lineSeparator());
                printprefix();
                scrollToBottom();

                if (nonEmpty(text)) {
                    history.add(text);
                    history.remove("");
                    history.add("");
                    historyPointer = history.size() - 1;

                    if (callback != null) {
                        callback.accept(text);
                    }
                }
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
            area.appendText(text);
            int to = area.getText(area.getCurrentParagraph()).length();
            int from = to - text.length();
            area.clearStyle(area.getCurrentParagraph(), from, to);
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
            area.appendText(text);
            int to = area.getText(area.getCurrentParagraph()).length();
            int from = to - text.length();
            area.setStyle(area.getCurrentParagraph(), from, to, Arrays.asList(styleClasses));
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
     * Prints the <code>prefix</code> to the console if one exists, otherwise an empty string.
     */
    public final void printprefix() {
        GUIUtils.runSafe(() -> {
            area.appendText(prefix != null ? prefix : "");
            area.clearStyle(area.getCurrentParagraph());
            scrollToBottom();
        });
    }

    /**
     * Prints the specified <code>text</code> and a new line to the console at the current caret position
     * with default <code>styleClasses</code>.
     * @param text to be print.
     * @return this Console.
     */
    public final Console println(final String text) {
        GUIUtils.runSafe(() -> {
            area.appendText(text + System.lineSeparator());
            area.clearStyle(area.getCurrentParagraph());
            scrollToBottom();
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
            area.setStyle(area.getCurrentParagraph(), Arrays.asList(styleClasses));
            area.appendText(text + System.lineSeparator());
            area.clearStyle(area.getCurrentParagraph());
            scrollToBottom();
        });
        return this;
    }

    /**
     * Prints the specified <code>text</code> and a new line to the console
     * with {@linkplain Console#ERROR_STYLE} <code>styleClasses</code>.
     * @param text to be print.
     * @return this Console.
     */
    public final Console printerr(final String text) {
        GUIUtils.runSafe(() -> {
            area.setStyle(area.getCurrentParagraph(), ERROR_STYLE);
            area.appendText(text + System.lineSeparator());
            area.clearStyle(area.getCurrentParagraph());
            scrollToBottom();
        });
        return this;
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

    public final void setCallback(final Consumer<String> callback) {
        this.callback = callback;
    }

    public final void setPrefix(final String prefix) {
        if (isNullOrEmpty(prefix)) {
            throw new IllegalArgumentException(
                    "Prefix may to be null or empty"
            );
        }
        try {
            Pattern pattern = Pattern.compile(prefix);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException(
                 e.getMessage()
            );
        }
        this.prefix = prefix;
    }

    private int minMinor() {
        return (prefix != null) ? prefix.length() : 0;
    }

    /**
     * Scrolls the <code>area</code> vertically to the bottom.
     */
    private void scrollToBottom() {
        area.scrollYBy(Double.MAX_VALUE);
    }
}
