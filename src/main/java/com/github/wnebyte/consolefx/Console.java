package com.github.wnebyte.consolefx;

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
import static com.github.wnebyte.consolefx.util.StringUtils.*;
import static com.github.wnebyte.consolefx.util.GUIUtils.runSafe;
import static com.github.wnebyte.consolefx.util.CollectionUtils.toCharArray;

/**
 * This class represents a Java-FX Console that is styleable using CSS.
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
        if (node == null) {
            return;
        }
        Nodes.addInputMap(node, InputMap.consume(eventPattern, action));
    }

    public static <T extends javafx.event.Event, U extends T> void addIgnorableInputMap(
            final Node node,
            final EventPattern<? super T, ? extends U> eventPattern
    ) {
        if (node == null) {
            return;
        }
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

    private static final String MASK_SEQUENCE = ":msk";

    private static final ScrollPane.ScrollBarPolicy DEFAULT_VERT_SCROLL_BAR_POLICY
            = ScrollPane.ScrollBarPolicy.ALWAYS;

    /*
    ###########################
    #          FIELDS         #
    ###########################
    */

    private final Object lock;

    private final StyleClassedTextArea area;

    private final VirtualizedScrollPane<StyleClassedTextArea> scrollPane;

    private final BooleanProperty suppressMask;

    private final LinkedList<Character> buffer;

    private final List<String> history;

    private int historyPointer;

    private Consumer<String> callback;

    private StyleText prefix;

    public final Printer out;

    public final Printer err;

    /*
    ###########################
    #       CONSTRUCTORS      #
    ###########################
    */

    public Console() {
        this.lock = new Object();
        this.area = new StyleClassedTextArea();
        this.scrollPane = new VirtualizedScrollPane<>(this.area);
        this.suppressMask = new SimpleBooleanProperty(true);
        this.buffer = new LinkedList<>();
        this.history = new ArrayList<>();
        this.historyPointer = 0;
        this.out = new StandardPrinter();
        this.err = new ErrorPrinter();
        this.area.setWrapText(true);
        this.area.setEditable(true);
        this.scrollPane.setVbarPolicy(DEFAULT_VERT_SCROLL_BAR_POLICY);
        super.setCenter(this.scrollPane);
        build();
    }

    /*
    ###########################
    #          METHODS        #
    ###########################
    */

    private void build() {
        addConsumableInputMap(this.area, keyPressed(ENTER), this::onEnterPressed);
        addConsumableInputMap(this.area, keyPressed(BACK_SPACE), this::onBackSpacePressed);
        addConsumableInputMap(this.area, keyPressed(LEFT), this::onLeftPressed);
        addConsumableInputMap(this.area, keyPressed(RIGHT), this::onRightPressed);
        addConsumableInputMap(this.area, keyPressed(UP), this::onUpPressed);
        addConsumableInputMap(this.area, keyPressed(DOWN), this::onDownPressed);
        addConsumableInputMap(this.area, mousePressed(MouseButton.PRIMARY), this::onPrimaryClicked);
        addConsumableInputMap(this.area, mousePressed(MouseButton.SECONDARY), this::onSecondaryClicked);
        addConsumableInputMap(this.area, keyPressed("V", KeyCodeCombination.CONTROL_DOWN), this::paste);
        addIgnorableInputMap(this.area, mouseClicked());
        addIgnorableInputMap(this.area, mouseReleased());
        addIgnorableInputMap(this.area, mouseDragged());
        addIgnorableInputMap(this.area, keyPressed("A", KeyCodeCombination.CONTROL_DOWN));
        addIgnorableInputMap(this.area, keyPressed("Z", KeyCodeCombination.CONTROL_DOWN));
        this.area.getUndoManager().close();
        this.area.multiPlainChanges()
                .successionEnds(Duration.ofMillis(10))
                .subscribe(this::scan);
        this.area.multiPlainChanges()
                .suppressWhen(this.suppressMask)
                .subscribe(this::mask);
    }

    private String removePrefix(String text) {
        if (prefix != null) {
            String seg = prefix.getLastSegment();
            if (text.startsWith(seg)) {
                return text.substring(seg.length());
            }
        }
        return text;
    }

    private void onEnterPressed(KeyEvent e) {
        boolean bufferNonEmpty;
        // get text
        String text = area.getText(area.getCurrentParagraph());
        // remove potential prefix
        text = removePrefix(text);

        // if buffer is not empty
        if (bufferNonEmpty = (buffer.size() > 0)) {
            String bufferText = new String(toCharArray(buffer));
            // replace mask sequence with contents of buffer
            text = replaceSequence(text, bufferText, MASK);
            // clear buffer
            buffer.clear();
        }

        suppressMask.setValue(true);
        println();

        if (!isNullOrEmpty(text)) {
            // if text is non-null and non-empty
            if (!bufferNonEmpty) {
                // if buffer was empty add text to history
                history.add(text);
                history.remove("");
                history.add("");
                historyPointer = history.size() - 1;
            }
            if (callback != null) {
                // callback with the text
                callback.accept(text);
            }
        } else {
            // print prefix & unlock console
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
            area.replaceText(
                    area.getCurrentParagraph(),
                    getMinMinor(),
                    area.getCurrentParagraph(),
                    area.getParagraphLength(area.getCurrentParagraph()),
                    history.get(historyPointer)
            );
        });
    }

    private void onDownPressed(KeyEvent e) {
        scrollToBottom();
        if (historyPointer >= history.size() - 1) {
            return;
        }
        historyPointer++;
        runSafe(() -> {
            area.replaceText(
                    area.getCurrentParagraph(),
                    getMinMinor(),
                    area.getCurrentParagraph(),
                    area.getParagraphLength(area.getCurrentParagraph()),
                    history.get(historyPointer)
            );
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
     * Scans the tail of the text from the current paragraph for {@link Console#MASK_SEQUENCE} occurrence,
     * if found deletes the occurrence and sets the class scoped <code>noMask</code> property to <code>false</code>
     * to init masking.
     */
    private void scan(List<PlainTextChange> plainTextChanges) {
        String text = area.getText(area.getCurrentParagraph());
        int len = text.length();
        if (text.endsWith(MASK_SEQUENCE)) {
            suppressMask.setValue(false);
            area.deleteText(
                    area.getCurrentParagraph(),
                    Math.max(getMinMinor(), len - MASK_SEQUENCE.length()),
                    area.getCurrentParagraph(),
                    len
            );
        }
    }

    /**
     * Replaces any appended text with the {@linkplain Console#MASK} char,
     * and pushes the appended text onto the class scoped buffer.
     */
    private void mask(List<PlainTextChange> plainTextChanges) {
        for (PlainTextChange ptc : plainTextChanges) {
            String inserted = ptc.getInserted();
            String removed = ptc.getRemoved();

            if ((inserted.length() != 0) && (removed.length() != 0)) {
                continue;
            }
            if (inserted.length() > 0) {
                char[] arr = inserted.toCharArray();
                for (char c : arr) {
                    buffer.add(c);
                }
                area.replaceText(
                        ptc.getPosition(),
                        ptc.getInsertionEnd(),
                        String.valueOf(MASK)
                );
            }
            if (removed.length() > 0) {
                if (!(buffer.isEmpty())) {
                    for (int i = 0; i < removed.length(); i++) {
                        buffer.removeLast();
                    }
                }
            }
        }
    }

    /**
     * Prints the specified <code>text</code> at the current caret position.
     * @param text to be print.
     */
    public void print(final String text) {
        synchronized (lock) {
            runSafe(() -> {
                split(text).forEach(out -> {
                    if (out.equals("\n")) {
                        println();
                    } else {
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
     * Prints the specified <code>text</code> with the specified <code>styleClasses</code>
     * at the current caret position.
     *
     * @param text         to be print.
     * @param styleClasses to be applied to the text.
     */
    public void print(final String text, final String... styleClasses) {
        synchronized (lock) {
            runSafe(() -> {
                split(text).forEach(out -> {
                    if (out.equals("\n")) {
                        println();
                    } else {
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
     * Prints the specified <code>text</code> and a new line at the current caret position.
     * @param text to be print.
     */
    public void println(final String text) {
        synchronized (lock) {
            runSafe(() -> {
                print(text);
                println();
            });
        }
    }

    /**
     * Prints the specified <code>text</code> with the specified <code>styleClasses</code>
     * and a new line at the current caret position.
     * @param text         to be print.
     * @param styleClasses to be applied to the text.
     */
    public void println(final String text, final String... styleClasses) {
        synchronized (lock) {
            runSafe(() -> {
                print(text, styleClasses);
                println();
            });
        }
    }

    /**
     * Prints the specified <code>StyleText</code> and a new line at the current caret position.
     * @param styleText to be print.
     */
    public void println(final StyleText styleText) {
        synchronized (lock) {
            runSafe(() -> {
                print(styleText);
                println();
            });
        }
    }

    /**
     * Prints the specified <code>text</code> using the default error styleClass and a new line
     * at the current caret position.
     * @param text to be print.
     */
    public void printerr(final String text) {
        synchronized (lock) {
            runSafe(() -> {
                print(text, Console.ERROR_STYLE_CLASSES);
                println();
            });
        }
    }

    /**
     * Prints a new line.
     */
    public void println() {
        synchronized (lock) {
            runSafe(() -> {
                area.appendText(System.lineSeparator());
                area.clearStyle(area.getCurrentParagraph());
                scrollToBottom();
            });
        }
    }

    /**
     * Prints the <code>Prefix</code> if one has been specified and unlocks this <code>Console</code>.
     */
    public void ready() {
        synchronized (lock) {
            runSafe(() -> {
                if (prefix != null) {
                    int minor = area.offsetToPosition(area.getCaretPosition(), TwoDimensional.Bias.Backward)
                            .getMinor();
                    if (getMinMinor() < minor) {
                        println();
                    }
                    print(prefix);
                }
                unlock();
            });
        }
    }

    public String getText() {
        return removePrefix(area.getText(area.getCurrentParagraph()));
    }

    /**
     * Clears any text from this <code>Console</code>.
     */
    public final void clear() {
        synchronized (lock) {
            runSafe(area::clear);
        }
    }

    /**
     * Clears the contents of the history.
     */
    public final void clearHistory() {
        history.clear();
        historyPointer = 0;
    }

    /**
     * Locks this <code>Console</code>.
     */
    public final void lock() {
        runSafe(() -> area.setEditable(false));
    }

    /**
     * Unlocks this <code>Console</code>.
     */
    public final void unlock() {
        runSafe(() -> area.setEditable(true));
    }

    /**
     * Returns whether this <code>Console</code> is locked.
     * @return <code>true</code> if it is locked,
     * otherwise <code>false</code>.
     */
    public final boolean isLocked() {
        return area.isEditable();
    }

    /**
     * Set whether the displayed text should wrap or not.
     * @param value whether the text should wrap.
     */
    public final void setWrapText(final boolean value) {
        runSafe(() -> area.setWrapText(value));
    }

    /**
     * Returns whether the displayed text is set to wrap or not,
     * @return <code>true</code> if the text is set to wrap,
     * otherwise <code>false</code>.
     */
    public final boolean isWrapText() {
        return area.isWrapText();
    }

    /**
     * Set a <code>vBarPolicy</code> for the vertical <code>ScrollPane</code>.
     * @param vBarPolicy to be used.
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
     *
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
     * Returns the <code>ContextMenu</code> of this <code>Console</code>.
     * @return the <code>ContextMenu</code> if one exists,
     * otherwise <code>null</code>.
     */
    public final ContextMenu getContextMenu() {
        return area.getContextMenu();
    }

    /**
     * Set a <code>ContextMenu</code> to be used by this <code>Console</code>.
     * @param contextMenu to be used.
     */
    public final void setContextMenu(final ContextMenu contextMenu) {
        area.setContextMenu(contextMenu);
    }

    /**
     * Set a callback to be invoked when this <code>Console</code> has new text appended to it.
     * @param callback to be used.
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
        this.prefix = (prefix.getLastSegment().endsWith(" ")) ?
                prefix : new StyleTextBuilder(prefix).whitespace().build();
    }

    /**
     * Returns the minimum column position for the current paragraph.
     * @return the minimum column position for the current paragraph (inclusive).
     */
    private int getMinMinor() {
        if (prefix == null) {
            return 0;
        } else {
            String text = area.getText(area.getCurrentParagraph());
            String seg = prefix.getLastSegment();
            return text.startsWith(seg) ? seg.length() : 0;
        }
    }

    /**
     * Scrolls the <code>area</code> vertically to the bottom.
     */
    private void scrollToBottom() {
        area.scrollYBy(Double.MAX_VALUE);
    }

    /*
    has to override all print methods.
     */
    public class StandardPrinter extends Printer {

        @Override
        public void print(boolean b) {
            Console.this.print(String.valueOf(b));
        }

        @Override
        public void print(char c) {
            Console.this.print(String.valueOf(c));
        }

        @Override
        public void print(char[] s) {
            Console.this.print(String.valueOf(s));
        }

        @Override
        public void print(double d) {
            Console.this.print(String.valueOf(d));
        }

        @Override
        public void print(float f) {
            Console.this.print(String.valueOf(f));
        }

        @Override
        public void print(int i) {
            Console.this.print(String.valueOf(i));
        }

        @Override
        public void print(long l) {
            Console.this.print(String.valueOf(l));
        }

        @Override
        public void print(String s) {
            Console.this.print(s);
        }

        @Override
        public void print(Object o) {
            Console.this.print(String.valueOf(o));
        }

        public void print(boolean b, String... styleClasses) {
            Console.this.print(String.valueOf(b), styleClasses);
        }

        public void print(char c, String... styleClasses) {
            Console.this.print(String.valueOf(c));
        }

        public void print(char[] s, String... styleClasses) {
            Console.this.print(String.valueOf(s));
        }

        public void print(double d, String... styleClasses) {
            Console.this.print(String.valueOf(d));
        }

        public void print(float f, String... styleClasses) {
            Console.this.print(String.valueOf(f));
        }

        public void print(int i, String... styleClasses) {
            Console.this.print(String.valueOf(i));
        }

        public void print(long l, String... styleClasses) {
            Console.this.print(String.valueOf(l));
        }

        public void print(String s, String... styleClasses) {
            Console.this.print(s);
        }

        public void print(Object o, String... styleClasses) {
            Console.this.print(String.valueOf(o));
        }

        @Override
        public void println(boolean x) {
            Console.this.println(String.valueOf(x));
        }

        @Override
        public void println(char x) {
            Console.this.println(String.valueOf(x));
        }

        @Override
        public void println(char[] x) {
            Console.this.println(String.valueOf(x));
        }

        @Override
        public void println(double x) {
            Console.this.println(String.valueOf(x));
        }

        @Override
        public void println(float x) {
            Console.this.println(String.valueOf(x));
        }

        @Override
        public void println(int x) {
            Console.this.println(String.valueOf(x));
        }

        @Override
        public void println(long x) {
            Console.this.println(String.valueOf(x));
        }

        @Override
        public void println(String x) {
            Console.this.println(x);
        }

        @Override
        public void println(Object x) {
            Console.this.println(String.valueOf(x));
        }

        public void println(boolean x, String... styleClasses) {
            Console.this.println(String.valueOf(x), styleClasses);
        }

        public void println(char x, String... styleClasses) {
            Console.this.println(String.valueOf(x), styleClasses);
        }

        public void println(char[] x, String... styleClasses) {
            Console.this.println(String.valueOf(x), styleClasses);
        }

        public void println(double x, String... styleClasses) {
            Console.this.println(String.valueOf(x), styleClasses);
        }

        public void println(float x, String... styleClasses) {
            Console.this.println(String.valueOf(x), styleClasses);
        }

        public void println(int x, String... styleClasses) {
            Console.this.println(String.valueOf(x), styleClasses);
        }

        public void println(long x, String... styleClasses) {
            Console.this.println(String.valueOf(x), styleClasses);
        }

        public void println(String x, String... styleClasses) {
            Console.this.println(x, styleClasses);
        }

        public void println(Object x, String... styleClasses) {
            Console.this.println(String.valueOf(x), styleClasses);
        }

        @Override
        public void println() {
            Console.this.println();
        }
    }

    public class ErrorPrinter extends Printer {

        @Override
        public void print(boolean b) {
            Console.this.print(String.valueOf(b), ERROR_STYLE_CLASSES);
        }

        @Override
        public void print(char c) {
            Console.this.print(String.valueOf(c), ERROR_STYLE_CLASSES);
        }

        @Override
        public void print(char[] s) {
            Console.this.print(String.valueOf(s), ERROR_STYLE_CLASSES);
        }

        @Override
        public void print(double d) {
            Console.this.print(String.valueOf(d), ERROR_STYLE_CLASSES);
        }

        @Override
        public void print(float f) {
            Console.this.print(String.valueOf(f), ERROR_STYLE_CLASSES);
        }

        @Override
        public void print(int i) {
            Console.this.print(String.valueOf(i), ERROR_STYLE_CLASSES);
        }

        @Override
        public void print(long l) {
            Console.this.print(String.valueOf(l), ERROR_STYLE_CLASSES);
        }

        @Override
        public void print(String s) {
            Console.this.print(s, ERROR_STYLE_CLASSES);
        }

        @Override
        public void print(Object o) {
            Console.this.print(String.valueOf(o), ERROR_STYLE_CLASSES);
        }

        @Override
        public void println(boolean b) {
            Console.this.println(String.valueOf(b), ERROR_STYLE_CLASSES);
        }

        @Override
        public void println(char c) {
            Console.this.println(String.valueOf(c), ERROR_STYLE_CLASSES);
        }

        @Override
        public void println(char[] s) {
            Console.this.println(String.valueOf(s), ERROR_STYLE_CLASSES);
        }

        @Override
        public void println(double d) {
            Console.this.println(String.valueOf(d), ERROR_STYLE_CLASSES);
        }

        @Override
        public void println(float f) {
            Console.this.println(String.valueOf(f), ERROR_STYLE_CLASSES);
        }

        @Override
        public void println(int i) {
            Console.this.println(String.valueOf(i), ERROR_STYLE_CLASSES);
        }

        @Override
        public void println(long l) {
            Console.this.println(String.valueOf(l), ERROR_STYLE_CLASSES);
        }

        @Override
        public void println(String s) {
            Console.this.println(s, ERROR_STYLE_CLASSES);
        }

        @Override
        public void println(Object o) {
            Console.this.println(String.valueOf(o), ERROR_STYLE_CLASSES);
        }

        @Override
        public void println() {
            Console.this.println();
        }
    }
}