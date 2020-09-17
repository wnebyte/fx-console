package data;

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
import static util.StringUtils.encode;
import static util.StringUtils.containsLineSeparator;
import static util.StringUtils.isNullOrEmpty;

public class ConsolePane extends BorderPane {
    private final StyleClassedTextArea area = new StyleClassedTextArea();
    private final List<String> history = new ArrayList<>();
    private int historyPointer = 0;
    private Consumer<String> onMessageReceivedHandler;
    private Charset charset = StandardCharsets.UTF_8;

    public ConsolePane() {
        VirtualizedScrollPane<StyleClassedTextArea> scrollPane = new VirtualizedScrollPane<>(area);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        area.setEditable(true);
        area.setWrapText(false);
        this.getStylesheets().add(getClass().getResource("/css/prompt.css").toExternalForm());
        setCenter(scrollPane);

        Nodes.addInputMap(area, InputMap.consume(keyPressed(ENTER), e -> {
            String text = area.getText(area.getCurrentParagraph());
            if (!text.equals("")) {
                history.add(text);
                historyPointer = history.size();
            }
            area.appendText(System.lineSeparator());
            snapY();

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
    }

    public void setOnMessageReceivedHandler(final Consumer<String> onMessageReceivedHandler) {
        this.onMessageReceivedHandler = onMessageReceivedHandler;
    }

    public void setEncoding(Charset charset) {
        this.charset = charset;
    }

    public void println(String text, String style) {
        GUIUtils.runSafe(() -> {
            if (containsLineSeparator(text)) {
                throw new IllegalArgumentException(
                        "text may not contain any line separators."
                );
            }
            area.appendText(encode(text, charset));
            if (!isNullOrEmpty(style)) {
                StyleFactory.create(text, style).forEach(s -> {
                    area.setStyle(area.getCurrentParagraph(), s.getIndex().getStart(),
                            s.getIndex().getEnd() + 1, s.getStyleClasses());
                });
            }
            area.appendText(System.lineSeparator());
            area.clearStyle(area.getCurrentParagraph());
            snapY();
        });
    }

    public void println(String text, Collection<String> styleClasses) {
        GUIUtils.runSafe(() -> {
            area.setStyle(area.getCurrentParagraph(), styleClasses);
            area.appendText(encode(text, charset) + System.lineSeparator());
            area.clearStyle(area.getCurrentParagraph());
            snapY();
        });
    }

    public void println(String text) {
        GUIUtils.runSafe(() -> {
            area.appendText(encode(text, charset) + System.lineSeparator());
            area.clearStyle(area.getCurrentParagraph());
            snapY();
        });
    }

    public void printerr(String text) {
        GUIUtils.runSafe(() -> {
            area.setStyle(area.getCurrentParagraph(), Collections.singletonList("error"));
            area.appendText(encode(text, charset) + System.lineSeparator());
            area.clearStyle(area.getCurrentParagraph());
            snapY();
        });
    }

    public void clear() {
        GUIUtils.runSafe(area::clear);
    }

    public void clearHistory() {
        history.clear();
        historyPointer = 0;
    }

    @Override
    public void requestFocus() {
        area.requestFocus();
    }

    private void snapY() {
        area.scrollYBy(Double.MAX_VALUE);
    }
}