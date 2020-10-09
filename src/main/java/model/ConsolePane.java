package model;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.Caret;
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
 * Class is designed to look and, behave as a console.
 * The contents of this class is centered in its parent BorderPane.
 * By default the /css/default.css file defines the visual appearance of this class.
 */
public class ConsolePane extends BorderPane {

    /**
     * The StyleClassedTextArea field.
     */
    private final StyleClassedTextArea area = new StyleClassedTextArea();

    /**
     * The VirtualizedScrollPane field.
     */
    private final VirtualizedScrollPane<StyleClassedTextArea> scrollPane =
            new VirtualizedScrollPane<>(area);

    /**
     * The history field.
     */
    private final List<String> history = new ArrayList<>();

    /**
     * The historyPointer field.
     */
    private int historyPointer = 0;

    /**
     * The consumer field.
     */
    private Consumer<String> onMessageReceivedHandler;

    /**
     * Initializes the relevant properties of this class and, overrides the proper keyEventListeners,
     * and removes the undesired ones, to make it look, and behave as a console.
     */
    public ConsolePane() {
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
                history.remove("");
                history.add("");
                historyPointer = history.size() - 1;
            }
            area.appendText(System.lineSeparator());
            scrollDown();

            if (!text.equals("") && onMessageReceivedHandler != null) {
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

        Nodes.addInputMap(area, InputMap.consume(mousePressed(MouseButton.PRIMARY), e -> {
            if (area.getContextMenu() != null) {
                GUIUtils.runSafe(() -> area.getContextMenu().hide());
            }
        }));

        Nodes.addInputMap(area, InputMap.consume(mousePressed(MouseButton.SECONDARY), e -> {
            if (area.getContextMenu() != null) {
                GUIUtils.runSafe(() -> area.getContextMenu().show(this, e.getScreenX(), e.getScreenY()));
            }
        }));

        Nodes.addInputMap(area, InputMap.ignore(mouseClicked()));
        Nodes.addInputMap(area, InputMap.ignore(mouseReleased()));
        Nodes.addInputMap(area, InputMap.ignore(mouseDragged()));
        Nodes.addInputMap(area, InputMap.ignore(keyPressed("A",
                KeyCodeCombination.CONTROL_DOWN)));
        Nodes.addInputMap(area, InputMap.ignore(keyPressed("Z",
                KeyCodeCombination.CONTROL_DOWN)));
        area.getUndoManager().close();

    }

    /**
     * Sets this class's consumer field.
     * @param onMessageReceivedHandler a consumer to be assigned to this class's consumer field.
     */
    public void setOnMessageReceivedHandler(final Consumer<String> onMessageReceivedHandler) {
        this.onMessageReceivedHandler = onMessageReceivedHandler;
    }

    /**
     * Appends a text with default style to this class's text-editing area.
     * @param text a text to be appended to this class's text-editing area.
     */
    public void println(String text) {
        GUIUtils.runSafe(() -> {
            area.appendText(new String(text.getBytes(), StandardCharsets.UTF_8) + System.lineSeparator());
            area.clearStyle(area.getCurrentParagraph());
            scrollDown();
        });
    }

    /**
     * Appends a text with the specified styles to this class's text-editing area.
     * @param text a text to be appended to this class's text-editing area.
     * @param styleClasses a collection of styles to be applied to the text.
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
     * Appends a text with default error-style to this class's text-editing area.
     * @param text a text to be appended to this class's text-editing area.
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
     * Clears all of the text from this class's text-editing area.
     */
    public void clear() {
        GUIUtils.runSafe(area::clear);
    }

    /**
     * Sets the editable property for this class's text-editing area.
     * @param value new boolean value for this class's text-editing area's editable property.
     */
    public void setEditable(boolean value) {
        GUIUtils.runSafe(() -> area.setEditable(value));
    }

    /**
     * Sets the wrap text property for this class's text-editing area.
     * @param value new boolean value for this class's text-editing area's wrap text property.
     */
    public void setWrapText(boolean value) {
        GUIUtils.runSafe(() -> area.setWrapText(value));
    }

    /**
     * Inserts the content of the clipboard into this class's text-editing area.
     */
    public void paste() {
        GUIUtils.runSafe(area::paste);
    }

    /**
     * Sets the v-scrollbar policy for this class's text-editing area.
     * @param vBarPolicy a scrollbar policy to apply to this class's text-editing area's vertical scrollbar.
     */
    public void setVbarPolicy(ScrollPane.ScrollBarPolicy vBarPolicy) {
        GUIUtils.runSafe(() -> scrollPane.setVbarPolicy(vBarPolicy));

    }

    /**
     * Gets this class's text-editing area's v-scrollbar policy.
     * @return this class's text-editing area's v-scrollbar policy.
     */
    public ScrollPane.ScrollBarPolicy getVbarPolicy() {
        return scrollPane.getVbarPolicy();
    }

    /**
     * Clears this class's history-list of its content.
     */
    public void clearHistory() {
        history.clear();
        historyPointer = 0;
    }

    /**
     * Gets this class's context menu.
     * @return this class's context menu. NULL by default.
     */
    public ContextMenu getContextMenu() {
        return area.getContextMenu();
    }

    /**
     * Sets this class's context menu.
     * @param contextMenu a context menu to be set to this class's context menu field.
     */
    public void setContextMenu(ContextMenu contextMenu) {
        area.setContextMenu(contextMenu);
    }

    /**
     * Scrolls the v-scrollbar all the way down.
     */
    private void scrollDown() {
        area.scrollYBy(Double.MAX_VALUE);
    }

}