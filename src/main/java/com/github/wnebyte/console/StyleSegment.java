package com.github.wnebyte.console;

import java.util.List;

public class StyleSegment {

    private final String text;

    private final List<String> styleClasses;

    public StyleSegment(String text, List<String> styleClasses) {
        this.text = text;
        this.styleClasses = styleClasses;
    }

    public String getText() {
        return text;
    }

    public List<String> getStyleClasses() {
        return styleClasses;
    }
}
