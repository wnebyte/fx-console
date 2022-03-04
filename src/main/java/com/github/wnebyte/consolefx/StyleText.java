package com.github.wnebyte.consolefx;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StyleText {

    private final List<StyleSegment> styleSegments;

    private final String[] lines;

    public StyleText(List<StyleSegment> styleSegments) {
        this.styleSegments = styleSegments;
        this.lines = join().split("\n");
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

    public String getFirstLine() {
        return lines[0];
    }

    public String getLastLine() {
        return lines[lines.length - 1];
    }

    public String getLine(int index) {
        return lines[index];
    }
}
