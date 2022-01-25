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
        List<String> styleList = (styleClasses != null) ? Arrays.asList(styleClasses) : new ArrayList<>();
        StyleSegment styleSegment = new StyleSegment(text, styleList);
        styleSegments.add(styleSegment);
        return this;
    }

    public StyleTextBuilder append(StyleSegment styleSegment) {
        styleSegments.add(styleSegment);
        return this;
    }

    public StyleTextBuilder ln() {
        if (styleSegments.isEmpty()) {
            StyleSegment styleSegment = new StyleSegment("\n", Collections.emptyList());
            styleSegments.add(styleSegment);
        }
        else {
            int index = styleSegments.size() - 1;
            StyleSegment styleSegment = styleSegments.get(index);
            String text = styleSegment.getText();
            List<String> styleClasses = styleSegment.getStyleClasses();
            styleSegment = new StyleSegment(text.concat("\n"), styleClasses);
            styleSegments.set(index, styleSegment);
        }

        return this;
    }

    public StyleTextBuilder whitespace() {
        if (styleSegments.isEmpty()) {
            StyleSegment styleSegment = new StyleSegment(" ", Collections.emptyList());
            styleSegments.add(styleSegment);
        }
        else {
            int index = styleSegments.size() - 1;
            StyleSegment styleSegment = styleSegments.get(index);
            String text = styleSegment.getText();
            List<String> styleClasses = styleSegment.getStyleClasses();
            styleSegment = new StyleSegment(text.concat(" "), styleClasses);
            styleSegments.set(index, styleSegment);
        }

        return this;
    }

    public StyleText build() {
        return new StyleText(styleSegments);
    }
}
