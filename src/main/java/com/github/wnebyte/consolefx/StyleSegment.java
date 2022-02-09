package com.github.wnebyte.consolefx;

import java.util.Collections;
import java.util.List;
import static com.github.wnebyte.consolefx.util.StringUtils.normalizeString;

public class StyleSegment {

    private final String text;

    private final List<String> styleClasses;

    public StyleSegment(String text, List<String> styleClasses) {
        this.text = normalizeString(text);
        this.styleClasses = styleClasses;
    }

    public String getText() {
        return text;
    }

    public List<String> getStyleClasses() {
        return Collections.unmodifiableList(styleClasses);
    }
}
