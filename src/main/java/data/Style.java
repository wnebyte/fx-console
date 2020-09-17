package data;

import java.util.List;

class Style {

    private final Index index;
    private final List<String> styleClasses;

    Style(Index index, List<String> styleClasses) {
        this.index = index;
        this.styleClasses = styleClasses;
    }

    List<String> getStyleClasses() {
        return styleClasses;
    }

    Index getIndex() {
        return index;
    }
}
