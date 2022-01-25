package com.github.wnebyte.console;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class StyleTextBuilder {

    private final List<StyleSegment> styleSegments;

    public StyleTextBuilder() {
        this.styleSegments = new ArrayList<>();
    }

    public StyleTextBuilder(StyleText styleText) {
        this.styleSegments = new ArrayList<>(styleText.getStyleSegments());
    }

    public StyleTextBuilder append(String text, String... styleClasses) {
        List<String> styleList = (styleClasses == null) ? Collections.emptyList() : Arrays.asList(styleClasses);
        StyleSegment styleSegment = new StyleSegment(text, styleList);
        return append(styleSegment);
    }

    public StyleTextBuilder append(StyleSegment styleSegment) {
        styleSegments.add(styleSegment);
        return this;
    }

    public StyleTextBuilder ln() {
        StyleSegment styleSegment = new StyleSegment("\n", Collections.emptyList());
        styleSegments.add(styleSegment);
        return this;
    }

    public StyleTextBuilder whitespace() {
        StyleSegment styleSegment = new StyleSegment(" ", Collections.emptyList());
        styleSegments.add(styleSegment);
        return this;
    }

    public StyleText build() {
        return new StyleText(styleSegments);
    }
}
