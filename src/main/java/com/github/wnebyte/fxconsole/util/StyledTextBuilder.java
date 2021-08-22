package com.github.wnebyte.fxconsole.util;

import com.github.wnebyte.fxconsole.StyledText;
import javafx.util.Pair;
import java.util.*;

public final class StyledTextBuilder {

    private final List<Pair<String, List<String>>> textSequences;

    public StyledTextBuilder() {
        this.textSequences = new ArrayList<>();
    }

    public StyledTextBuilder(final StyledText styledText) {
        this.textSequences = styledText.getTextSequences();
    }

    public final StyledTextBuilder append(final String text, final String... styleClasses) {
        if (text == null) {
            throw new IllegalArgumentException(
                    "Text may not be null"
            );
        }
        List<String> styles = (styleClasses != null) ? Arrays.asList(styleClasses) : new ArrayList<>();
        Pair<String, List<String>> pair = new Pair<>(text, styles);
        textSequences.add(pair);
        return this;
    }

    public final StyledTextBuilder ln() {
        if (textSequences.isEmpty()) {
            Pair<String, List<String>> pair = new Pair<>("\n", new ArrayList<>());
            textSequences.add(pair);
        }
        else {
            Pair<String, List<String>> last = textSequences.get(textSequences.size() - 1);
            String key = last.getKey().concat("\n");
            List<String> value = new ArrayList<>(last.getValue());
            Pair<String, List<String>> pair = new Pair<>(key, value);
            textSequences.set(textSequences.size() - 1, pair);
        }
        return this;
    }

    public final StyledTextBuilder whitespace() {
        if (textSequences.isEmpty()) {
            Pair<String, List<String>> pair = new Pair<>(" ", new ArrayList<>());
            textSequences.add(pair);
        }
        else {
            Pair<String, List<String>> last = textSequences.get(textSequences.size() - 1);
            String key = last.getKey().concat(" ");
            List<String> value = new ArrayList<>(last.getValue());
            Pair<String, List<String>> pair = new Pair<>(key, value);
            textSequences.set(textSequences.size() - 1, pair);
        }
        return this;
    }

    public final StyledText build() {
        return new StyledText(textSequences);
    }
}
