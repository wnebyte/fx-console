package model;

import java.util.List;

/**
 * Each Style Needs: Index, List<String> styleClasses
 */
public class Style {

    private final Index index;
    private final List<String> styleClasses;

    public Style(Index index, List<String> styleClasses) {
        this.index = index;
        this.styleClasses = styleClasses;
    }

    public List<String> getStyleClasses() {
        return styleClasses;
    }

    public Index getIndex() {
        return index;
    }
}
