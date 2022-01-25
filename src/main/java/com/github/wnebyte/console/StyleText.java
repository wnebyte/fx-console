package com.github.wnebyte.console;

import java.util.List;
import java.util.ArrayList;

public class StyleText {

    private final List<StyleSegment> styleSegments;

    public StyleText() {
        this.styleSegments = new ArrayList<>();
    }

    public StyleText(List<StyleSegment> styleSegments) {
        this.styleSegments = styleSegments;
    }

    public List<StyleSegment> getStyleSegments() {
        return styleSegments;
    }

    public StyleSegment getFirstStyleSegment() {
        return styleSegments.get(0);
    }

    public StyleSegment getLastStyleSegment() {
        return styleSegments.get(styleSegments.size() - 1);
    }
}
