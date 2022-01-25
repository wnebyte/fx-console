package com.github.wnebyte.console;

import java.util.*;
import java.util.function.Consumer;
import java.time.Duration;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.fxmisc.wellbehaved.event.Nodes;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.PlainTextChange;
import org.fxmisc.richtext.model.TwoDimensional;
import org.fxmisc.flowless.VirtualizedScrollPane;
import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyCode.DOWN;
import static org.fxmisc.wellbehaved.event.EventPattern.*;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;
import static com.github.wnebyte.console.util.StringUtils.*;
import static com.github.wnebyte.console.util.CollectionUtils.toCharArray;
import static com.github.wnebyte.console.util.GUIUtils.runSafe;

/**
 * This class is a css-styleable FX Console.
 */
public class Console extends BorderPane {

    /*
    ###########################
    #      STATIC METHODS     #
    ###########################
    */

    public static <T extends javafx.event.Event, U extends T> void addConsumableInputMap(
            final Node node,
            final EventPattern<? super T, ? extends U> eventPattern,
            final Consumer<? super U> action
    ) {
        if (node == null) { return; }
        Nodes.addInputMap(node, InputMap.consume(eventPattern, action));
    }

    public static <T extends javafx.event.Event, U extends T> void addIgnorableInputMap(
            final Node node,
            final EventPattern<? super T, ? extends U> eventPattern
    ) {
        if (node == null) { return; }
        Nodes.addInputMap(node, InputMap.ignore(eventPattern));
    }

    /*
    ###########################
    #      STATIC FIELDS      #
    ###########################
    */

    public static final String ERROR_STYLE_CLASS = "error";

    public static final String[] ERROR_STYLE_CLASSES = { ERROR_STYLE_CLASS };

    private static final char MASK = '*';

    private static final String INIT_MASK_SEQUENCE = ":pw";

    /*
    ###########################
    #          FIELDS         #
    ###########################
    */

    private final Object lock;

    private final StyleClassedTextArea area;

    private final VirtualizedScrollPane<StyleClassedTextArea> scrollPane;

    private final BooleanProperty noMask;

    private final LinkedList<Character> buffer;

    private final List<String> history;

    private int historyPointer;

    private Consumer<String> callback;

    private StyleText prefix;

    public Console() {
        this.lock = new Object();
        this.area = new StyleClassedTextArea();
        this.scrollPane = new VirtualizedScrollPane<>(this.area);
        this.noMask = new SimpleBooleanProperty(true);
        this.buffer = new LinkedList<>();
        this.history = new LinkedList<>();
        this.historyPointer = 0;
        setCenter(this.scrollPane);
        this.area.setWrapText(true);
        this.area.setEditable(true);
        this.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        build();
    }

    private void build() {
        Console.addConsumableInputMap(this.area, keyPressed(ENTER), this::onEnterPressed);
        Console.addConsumableInputMap(this.area, keyPressed(BACK_SPACE), this::onBackSpacePressed);
        Console.addConsumableInputMap(this.area, keyPressed(LEFT), this::onLeftPressed);
        Console.addConsumableInputMap(this.area, keyPressed(RIGHT), this::onRightPressed);
        Console.addConsumableInputMap(this.area, keyPressed(UP), this::onUpPressed);
        Console.addConsumableInputMap(this.area, keyPressed(DOWN), this::onDownPressed);
        Console.addConsumableInputMap(this.area, mousePressed(MouseButton.PRIMARY), this::onPrimaryClicked);
        Console.addConsumableInputMap(this.area, mousePressed(MouseButton.SECONDARY), this::onSecondaryClicked);
        Console.addConsumableInputMap(this.area, keyPressed("V", KeyCodeCombination.CONTROL_DOWN), this::paste);
        Console.addIgnorableInputMap (this.area, mouseClicked());
        Console.addIgnorableInputMap (this.area, mouseReleased());
        Console.addIgnorableInputMap (this.area, mouseDragged());
        Console.addIgnorableInputMap (this.area, keyPressed("A", KeyCodeCombination.CONTROL_DOWN));
        Console.addIgnorableInputMap (this.area, keyPressed("Z", KeyCodeCombination.CONTROL_DOWN));
        this.area.getUndoManager().close();
        this.area.multiPlainChanges()
                .successionEnds(Duration.ofMillis(10))
                .subscribe(this::scan);
        this.area.multiPlainChanges()
                .suppressWhen(this.noMask)
                .subscribe(this::mask);
    }

    private String removePrefix(String text) {
        if (prefix != null) {
            String seg = prefix.getLastStyleSegment().getText();
            if (text.startsWith(seg)) {
                return text.substring(seg.length());
            } else {
                return text;
            }
        } else {
            return text;
        }
    }

    private void onEnterPressed(KeyEvent e) {
        boolean hasBuffer;
        // get text
        String text = area.getText(area.getCurrentParagraph());
        // remove potential prefix
        text = removePrefix(text);

        // if buffer has content
        if (hasBuffer = (buffer.size() > 0)) {
            String bufferText = new String(toCharArray(buffer));
            // replace masked chars with contents of buffer
            text = replaceSequence(text, bufferText, MASK);
            // clear buffer
            buffer.clear();
        }

        noMask.setValue(true);
        ln();

        if (!isNullOrEmpty(text)) {
            if (!hasBuffer) {
                // add text to history if buffer was empty
                history.add(text);
                history.remove("");
                history.add("");
                historyPointer = history.size() - 1;
            }
            if (callback != null) {
                // callback with text
                callback.accept(text);
            }
        } else {
            ready();
        }
    }

    private void onBackSpacePressed(KeyEvent e) {
        scrollToBottom();
        int minor = area.offsetToPosition(area.getCaretPosition(), TwoDimensional.Bias.Backward)
                .getMinor();
        if (getMinMinor() < minor) {
            area.deletePreviousChar();
        }
    }

    private void onLeftPressed(KeyEvent e) {
        scrollToBottom();
        int major = area.offsetToPosition(area.getCaretPosition(), TwoDimensional.Bias.Backward)
                .getMajor();
        int minor = area.offsetToPosition(area.getCaretPosition(), TwoDimensional.Bias.Backward)
                .getMinor();
        if (getMinMinor() < minor) {
            area.moveTo(major, minor - 1);
        }
    }

    private void onRightPressed(KeyEvent k) {
        scrollToBottom();
        int major = area.offsetToPosition(area.getCaretPosition(), TwoDimensional.Bias.Forward)
                .getMajor();
        int minor = area.offsetToPosition(area.getCaretPosition(), TwoDimensional.Bias.Forward)
                .getMinor();
        if (minor != area.getParagraphLength(major)) {
            area.moveTo(major, minor + 1);
        }
    }


    private void onUpPressed(KeyEvent e) {
        scrollToBottom();
        if (historyPointer == 0) {
            return;
        }
        historyPointer--;
        runSafe(() -> {
            area.replaceText(area.getCurrentParagraph(), getMinMinor(), area.getCurrentParagraph(),
                    area.getParagraphLength(area.getCurrentParagraph()), history.get(historyPointer));
        });
    }

    private void onDownPressed(KeyEvent e) {
        scrollToBottom();
        if (historyPointer >= history.size() - 1) {
            return;
        }
        historyPointer++;
        runSafe(() -> {
            area.replaceText(area.getCurrentParagraph(), getMinMinor(), area.getCurrentParagraph(),
                    area.getParagraphLength(area.getCurrentParagraph()), history.get(historyPointer));
        });
    }

    private void onPrimaryClicked(MouseEvent e) {
        if (area.getContextMenu() != null) {
            runSafe(() -> area.getContextMenu().hide());
        }
    }

    private void onSecondaryClicked(MouseEvent e) {
        if (area.getContextMenu() != null) {
            runSafe(() -> area.getContextMenu().show(area, e.getScreenX(), e.getScreenY()));
        }
    }

    private void paste(KeyEvent e) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasString()) {
            String s = clipboard.getString()
                    .replace("\r\n", "")
                    .replace("\n", "");
            print(s);
        }
    }

    /**
     * Scans the tail of the text from the current paragraph for {@link Console#INIT_MASK_SEQUENCE} occurrence,
     * if found deletes the occurrence and sets the class scoped <code>noMask</code> property to <code>false</code>
     * to init masking.
     */
    private void scan(List<PlainTextChange> plainTextChanges) {
        String text = area.getText(area.getCurrentParagraph());
        int len = text.length();
        if (text.endsWith(INIT_MASK_SEQUENCE)) {
            noMask.setValue(false);
            area.deleteText(area.getCurrentParagraph(), Math.max(getMinMinor(), len - INIT_MASK_SEQUENCE.length()),
                    area.getCurrentParagraph(), len);
        }
    }

    /**
     * Replaces any appended text with the {@linkplain Console#MASK} char,
     * and pushes the appended text onto the class scoped buffer.
     */
    private void mask(List<PlainTextChange> plainTextChanges) {
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

    /**
     * Prints the specified <code>String</code> at the current caret position.
     * @param text to be print.
     */
    public void print(final String text) {
        synchronized (lock) {
            runSafe(() -> {
                split(text).forEach(out -> {
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
     * Prints the specified <code>String</code> with the specified <code>styleClasses</code>
     * at the current caret position.
     * @param text to be print.
     * @param styleClasses to be applied to the text.
     */
    public void print(final String text, final String... styleClasses) {
        synchronized (lock) {
            runSafe(() -> {
                split(text).forEach(out -> {
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
     * Prints the specified <code>StyleText</code> at the current caret position.
     * @param styleText to be print.
     */
    public void print(final StyleText styleText) {
        synchronized (lock) {
            runSafe(() -> {
                styleText.getStyleSegments().forEach(out -> {
                    print(out.getText(), out.getStyleClasses().toArray(new String[0]));
                });
            });
        }
    }

    /**
     * Prints the specified <code>String</code> and appends a new line at the current caret position.
     * @param text to be print.
     */
    public void println(final String text) {
        synchronized (lock) {
            runSafe(() -> {
                print(text);
                ln();
            });
        }
    }

    /**
     * Prints the specified <code>String</code> with the specified <code>styleClasses</code>
     * and appends a new line at the current caret position.
     * @param text to be print.
     * @param styleClasses to be applied to the text.
     */
    public void println(final String text, final String... styleClasses) {
        synchronized (lock) {
            runSafe(() -> {
                print(text, styleClasses);
                ln();
            });
        }
    }

    /**
     * Prints the specified <code>StyleText</code> and appends a new line at the current caret position.
     * @param styleText to be print.
     */
    public void println(final StyleText styleText) {
        synchronized (lock) {
            runSafe(() -> {
                print(styleText);
                ln();
            });
        }
    }

    public void printerr(final String text) {
        synchronized (lock) {
            runSafe(() -> {
                print(text, Console.ERROR_STYLE_CLASSES);
                ln();
            });
        }
    }

    /**
     * Prints a new line.
     */
    public void ln() {
        synchronized (lock) {
            runSafe(() -> {
                area.appendText(System.lineSeparator());
                area.clearStyle(area.getCurrentParagraph());
                scrollToBottom();
            });
        }
    }

    /**
     * Prints the <code>Prefix</code> at the current caret position and unlocks this <code>Console</code>.
     */
    // Todo: replace with insert
    public void ready() {
        synchronized (lock) {
            runSafe(() -> {
                if (prefix != null) {
                    print(prefix);
                }
                unlock();
            });
        }
    }

    /**
     * Clears any text.
     */
    public final void clear() {
        synchronized (lock) {
            runSafe(area::clear);
        }
    }

    /**
     * Locks the editable area.
     */
    public final void lock() {
        runSafe(() -> area.setEditable(false));
    }

    /**
     * Unlocks the editable area.
     */
    public final void unlock() {
        runSafe(() -> area.setEditable(true));
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
        runSafe(() -> area.setWrapText(value));
    }

    /**
     * @return <code>true</code> if the <code>wrapText</code> property is set to <code>true</code>,
     * otherwise <code>false</code>.
     */
    public final boolean isWrapText() {
        return area.isWrapText();
    }

    /**
     * Sets the vBarPolicy for this console's vertical ScrollPane.
     * @param vBarPolicy to be set.
     */
    public final void setVbarPolicy(final ScrollPane.ScrollBarPolicy vBarPolicy) {
        runSafe(() -> scrollPane.setVbarPolicy(vBarPolicy));
    }

    /**
     * @return the vBarPolicy for this console's vertical ScrollPane.
     */
    public final ScrollPane.ScrollBarPolicy getVbarPolicy() {
        return scrollPane.getVbarPolicy();
    }

    /**
     * Sets the hBarPolicy for this console's horizontal ScrollPane.
     * @param hBarPolicy to be set.
     */
    public final void setHBarPolicy(final ScrollPane.ScrollBarPolicy hBarPolicy) {
        runSafe(() -> scrollPane.setHbarPolicy(hBarPolicy));
    }

    /**
     * @return returns the hBarPolicy for this console's ScrollPane.
     */
    public final ScrollPane.ScrollBarPolicy getHbarPolicy() {
        return scrollPane.getHbarPolicy();
    }

    /**
     * Clears this console's <code>history</code>.
     */
    public final void clearHistory() {
        history.clear();
        historyPointer = 0;
    }

    /**
     * Returns this console's ContextMenu.
     * @return the ContextMenu, or <code>null</code> if not set.
     */
    public final ContextMenu getContextMenu() {
        return area.getContextMenu();
    }

    /**
     * Sets the ContextMenu for this console
     * @param contextMenu to be set.
     */
    public final void setContextMenu(final ContextMenu contextMenu) {
        area.setContextMenu(contextMenu);
    }

    /**
     * Specify the Handler to be called when new input has been appended to the console.
     * @param callback to be set.
     */
    public final void setCallback(final Consumer<String> callback) {
        this.callback = callback;
    }

    public final void setPrefix(final StyleText prefix) {
        if ((prefix == null) || (prefix.getStyleSegments().isEmpty())) {
            throw new IllegalArgumentException(
                    "The prefix if specified must consist of at least one segment."
            );
        }
        // make sure that the prefix ends with a whitespace character
        this.prefix = (prefix.getLastStyleSegment().getText().endsWith("\\s")) ?
                prefix : new StyleTextBuilder(prefix).whitespace().build();
    }

    /**
     * @return the exclusive min column position of the current paragraph.
     */
    private int getMinMinor() {
        /*
        return (prefix != null && area.getText(area.getCurrentParagraph())
                .startsWith(prefix.getLast().getKey())) ? prefix.getLast().getKey().length() : 0;
         */
        if (prefix == null) {
            return 0;
        } else {
            String text = area.getText(area.getCurrentParagraph());
            String seg = prefix.getLastStyleSegment().getText();
            return text.startsWith(seg) ? seg.length() : 0;
        }
    }

    /**
     * Scrolls the <code>area</code> vertically to the bottom.
     */
    private void scrollToBottom() {
        area.scrollYBy(Double.MAX_VALUE);
    }
}