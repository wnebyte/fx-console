package com.github.wnebyte.fxconsole.util;

import com.github.wnebyte.fxconsole.StylisedText;
import javafx.util.Pair;
import java.util.*;

/**
 * This class is a builder class for instances of {@link StylisedText}.
 */
public final class StylisedTextBuilder {

    private final List<Pair<String, List<String>>> stylisedTextList;

    public StylisedTextBuilder() {
        this.stylisedTextList = new ArrayList<>();
    }

    public StylisedTextBuilder(final StylisedText stylisedText) {
        this.stylisedTextList = stylisedText.getList();
    }

    public final StylisedTextBuilder append(final String text, final String... styleClasses) {
        if (text == null) {
            throw new IllegalArgumentException(
                    "Text may not be null"
            );
        }
        List<String> styles = (styleClasses != null) ? Arrays.asList(styleClasses) : new ArrayList<>();
        Pair<String, List<String>> pair = new Pair<>(text, styles);
        stylisedTextList.add(pair);
        return this;
    }

    public final StylisedTextBuilder ln() {
        if (stylisedTextList.isEmpty()) {
            Pair<String, List<String>> pair = new Pair<>("\n", new ArrayList<>());
            stylisedTextList.add(pair);
        }
        else {
            Pair<String, List<String>> last = stylisedTextList.get(stylisedTextList.size() - 1);
            String key = last.getKey().concat("\n");
            List<String> value = new ArrayList<>(last.getValue());
            Pair<String, List<String>> pair = new Pair<>(key, value);
            stylisedTextList.set(stylisedTextList.size() - 1, pair);
        }
        return this;
    }

    public final StylisedTextBuilder whitespace() {
        if (stylisedTextList.isEmpty()) {
            Pair<String, List<String>> pair = new Pair<>(" ", new ArrayList<>());
            stylisedTextList.add(pair);
        }
        else {
            Pair<String, List<String>> last = stylisedTextList.get(stylisedTextList.size() - 1);
            String key = last.getKey().concat(" ");
            List<String> value = new ArrayList<>(last.getValue());
            Pair<String, List<String>> pair = new Pair<>(key, value);
            stylisedTextList.set(stylisedTextList.size() - 1, pair);
        }
        return this;
    }

    public final StylisedText build() {
        return new StylisedText(stylisedTextList);
    }
}
