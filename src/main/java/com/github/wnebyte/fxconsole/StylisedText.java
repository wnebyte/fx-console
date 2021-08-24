package com.github.wnebyte.fxconsole;

import javafx.util.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents a sequence of text that are to be stylised.
 */
public final class StylisedText {

    private final List<Pair<String, List<String>>> list;

    @SafeVarargs
    public StylisedText(final Pair<String, List<String>>... list) {
        if ((list == null) || (list.length == 0)) {
            throw new IllegalArgumentException(
                    "List must not be null or empty"
            );
        }
        this.list = Arrays.asList(list);
    }

    public StylisedText(final List<Pair<String, List<String>>> list) {
        if ((list == null) || (list.isEmpty())) {
            throw new IllegalArgumentException(
                    "List must not be null or empty"
            );
        }
        this.list = list;
    }

    public final List<Pair<String, List<String>>> getList() {
        return list;
    }

    public final Pair<String, List<String>> getLast() {
        if (list.isEmpty()) {
            throw new IllegalArgumentException(
                    "List is empty"
            );
        }
        return list.get(list.size() - 1);
    }

    public final String joinText() {
        if (list.isEmpty()) {
            return "";
        }
        return list.stream().map(Pair::getKey).collect(Collectors.joining());
    }
}
