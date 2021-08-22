package com.github.wnebyte.fxconsole;

import javafx.util.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class StyledText {

    private final List<Pair<String, List<String>>> textSequences;

    @SafeVarargs
    public StyledText(final Pair<String, List<String>>... textSequences) {
        if ((textSequences == null) || (textSequences.length == 0)) {
            throw new IllegalArgumentException(
                    "TextSequences must not be null or empty"
            );
        }
        this.textSequences = Arrays.asList(textSequences);
    }

    public StyledText(final List<Pair<String, List<String>>> textSequences) {
        if ((textSequences == null) || (textSequences.isEmpty())) {
            throw new IllegalArgumentException(
                    "TextSequences must not be null or empty"
            );
        }
        this.textSequences = textSequences;
    }

    public final List<Pair<String, List<String>>> getTextSequences() {
        return textSequences;
    }

    public final Pair<String, List<String>> getLast() {
        if (textSequences.isEmpty()) {
            throw new IllegalArgumentException(
                    "TextSequences is empty"
            );
        }
        return textSequences.get(textSequences.size() - 1);
    }

    public final String join() {
        if (textSequences.isEmpty()) {
            return "";
        }
        return textSequences.stream().map(Pair::getKey).collect(Collectors.joining());
    }
}
