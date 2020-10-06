package model;

import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ScrollPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.TwoDimensional.Bias;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;
import util.GUIUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

import static javafx.scene.input.KeyCode.*;
import static org.fxmisc.wellbehaved.event.EventPattern.*;

/**
 * <summary>Class is designed to look and, behave as a ConsolePane.</summary>
 */
public class ConsolePane extends BorderPane {

    /**
     * <summary>StyleClassedTextArea field.</summary>
     */
    private final StyleClassedTextArea area = new StyleClassedTextArea();

    /**
     * <summary>History field -- saves the commands that gets inputted into the console.</summary>
     */
    private final List<String> history = new ArrayList<>();

    /**
     * <summary>HistoryPointer field -- a pointer to keep track of which History element is the next one
     * to be displayed. </summary>
     */
    private int historyPointer = 0;

    /**
     * <summary>The consumer to be accepted, following a key press of ENTER.</summary>
     */
    private Consumer<String> onMessageReceivedHandler;

    /**
     * Default Constructor.
     * Initializes the relevant properties of this class and, overrides the proper keyEventListeners,
     * and removes the undesired ones, to make it behave as a console.
     */
    public ConsolePane() {
        VirtualizedScrollPane<StyleClassedTextArea> scrollPane = new VirtualizedScrollPane<>(area);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        setCenter(scrollPane);
        area.setEditable(true);
        area.setWrapText(true);
        this.getStylesheets().add(getClass().getResource("/css/default.css").toExternalForm());

        Nodes.addInputMap(area, InputMap.consume(keyPressed(ENTER), e -> {
            final String text = new String(area.getText(area.getCurrentParagraph())
                    .getBytes(StandardCharsets.UTF_8));

            if (!text.equals("")) {
                history.add(area.getText(area.getCurrentParagraph()));
                historyPointer = history.size();
            }
            area.appendText(System.lineSeparator());
            scrollDown();

            if (onMessageReceivedHandler != null) {
                onMessageReceivedHandler.accept(text);
            }
        }));

        Nodes.addInputMap(area, InputMap.consume(keyPressed(BACK_SPACE), e -> {
            int minor = area.offsetToPosition(area.getCaretPosition(), Bias.Backward).getMinor();

            if (minor != 0) {
                area.deletePreviousChar();
            }
        }));

        Nodes.addInputMap(area, InputMap.consume(keyPressed(LEFT), e -> {
            int major = area.offsetToPosition(area.getCaretPosition(), Bias.Backward).getMajor();
            int minor = area.offsetToPosition(area.getCaretPosition(), Bias.Backward).getMinor();

            if (minor != 0) {
                area.moveTo(major, minor - 1);
            }
        }));

        Nodes.addInputMap(area, InputMap.consume(keyPressed(RIGHT), e -> {
            int major = area.offsetToPosition(area.getCaretPosition(), Bias.Forward).getMajor();
            int minor = area.offsetToPosition(area.getCaretPosition(), Bias.Forward).getMinor();

            if (minor != area.getParagraphLength(major)) {
                area.moveTo(major, minor + 1);
            }
        }));

        Nodes.addInputMap(area, InputMap.consume(keyPressed(UP), e -> {
            if (historyPointer == 0) {
                return;
            }
            historyPointer--;

            GUIUtils.runSafe(() -> area.replaceText(area.getCurrentParagraph(), 0, area.getCurrentParagraph(),
                    area.getParagraphLength(area.getCurrentParagraph()), history.get(historyPointer)));
        }));

        Nodes.addInputMap(area, InputMap.consume(keyPressed(DOWN), e -> {
            if (historyPointer >= history.size() - 1) {
                return;
            }
            historyPointer++;

            GUIUtils.runSafe(() -> area.replaceText(area.getCurrentParagraph(), 0, area.getCurrentParagraph(),
                    area.getParagraphLength(area.getCurrentParagraph()), history.get(historyPointer)));
        }));

        Nodes.addInputMap(area, InputMap.ignore(mouseClicked()));
        Nodes.addInputMap(area, InputMap.ignore(mouseReleased()));
        Nodes.addInputMap(area, InputMap.ignore(mousePressed()));
        Nodes.addInputMap(area, InputMap.ignore(mouseDragged()));
        Nodes.addInputMap(area, InputMap.ignore(keyPressed("A",
                KeyCodeCombination.CONTROL_DOWN)));
        Nodes.addInputMap(area, InputMap.ignore(keyPressed("Z",
                KeyCodeCombination.CONTROL_DOWN)));
        area.getUndoManager().close();
    }

    /**
     * <summary>Default println method.</summary>
     * @param text a text to be print to the console.
     */
    public void println(String text) {
        GUIUtils.runSafe(() -> {
            area.appendText(new String(text.getBytes(), StandardCharsets.UTF_8) + System.lineSeparator());
            area.clearStyle(area.getCurrentParagraph());
            scrollDown();
        });
    }

    /**
     * <summary>Styled println method.</summary>
     * @param text a text to be print to the console.
     * @param styleClasses a collection of css-classes, to be applied to the text.
     */
    public void println(String text, Collection<String> styleClasses) {
        GUIUtils.runSafe(() -> {
            area.setStyle(area.getCurrentParagraph(), styleClasses);
            area.appendText(new String(text.getBytes(), Charset.defaultCharset()) + System.lineSeparator());
            area.clearStyle(area.getCurrentParagraph());
            scrollDown();
        });
    }

    /*
    <note>Will be implemented in the future.</note>
   public void println(String text, List<Style> styles) {
        GUIUtils.runSafe(() -> {
            int paragraphs = text.concat("\\s").split("\n").length;

            area.appendText(new String(text.getBytes(), Charset.defaultCharset()));
            for (Style style : styles) {
                area.setStyle(area.getCurrentParagraph() - paragraphs + style.getIndex().getParagraph(),
                        style.getIndex().getStart(),
                        style.getIndex().getEnd(),
                        style.getStyleClasses());
            }
            area.appendText(System.lineSeparator());
            area.clearStyle(area.getCurrentParagraph());
            scrollDown();
        });
    }
    */

    /**
     * <summary>Default printerr method.</summary>
     * @param text a text to be print to the console.
     */
    public void printerr(String text) {
        GUIUtils.runSafe(() -> {
            area.setStyle(area.getCurrentParagraph(), Collections.singletonList("error"));
            area.appendText(new String(text.getBytes(), Charset.defaultCharset()) + System.lineSeparator());
            area.clearStyle(area.getCurrentParagraph());
            scrollDown();
        });
    }

    /**
     * <summary>Method for clearing the text-content of the console.</summary>
     */
    public void clear() {
        GUIUtils.runSafe(area::clear);
    }

    /**
     * <summary>Method for clearing the underlying history list (of commands) of its content.</summary>
     */
    public void clearHistory() {
        history.clear();
        historyPointer = 0;
    }

    /**
     * <summary>SET method for this class's consumer.</summary>
     * @param onMessageReceivedHandler a consumer to be assigned to this class's consumer field.
     */
    public void setOnMessageReceivedHandler(final Consumer<String> onMessageReceivedHandler) {
        this.onMessageReceivedHandler = onMessageReceivedHandler;
    }

    // internal calls to this method could be replaced by placing a listener on the area.textProperty().
    /**
     * <summary>Method for scrolling down the scrollPane, as much as possible</summary>
     */
    private void scrollDown() {
        area.scrollYBy(Double.MAX_VALUE);
    }

}