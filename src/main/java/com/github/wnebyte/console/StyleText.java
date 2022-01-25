package com.github.wnebyte.console;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StyleText {

    private final List<StyleSegment> styleSegments;

    private final String[] segments;

    public StyleText(List<StyleSegment> styleSegments) {
        this.styleSegments = styleSegments;
        this.segments = join().split("\n");
    }

    public List<StyleSegment> getStyleSegments() {
        return Collections.unmodifiableList(styleSegments);
    }

    private StyleSegment getFirstStyleSegment() {
        return styleSegments.get(0);
    }

    private StyleSegment getLastStyleSegment() {
        return styleSegments.get(styleSegments.size() - 1);
    }

    private StyleSegment getStyleSegment(int index) {
        return styleSegments.get(index);
    }

    private String join() {
        return styleSegments.stream().map(StyleSegment::getText).collect(Collectors.joining());
    }

    public String getFirstSegment() {
        return segments[0];
    }

    public String getLastSegment() {
        return segments[segments.length - 1];
    }

    public String getSegment(int index) {
        return segments[index];
    }
}
