package data;

import java.util.*;
import java.util.regex.Pattern;

class StyleFactory {

    static List<Style> create(String text, String from) {
        return create(text, parse(from));
    }

    private final static class IntermediateStyleObject {
        private final List<Substring> substrings;
        private final List<String> styleClasses;

        private IntermediateStyleObject(List<Substring> substrings, List<String> styleClasses) {
            this.substrings = substrings;
            this.styleClasses = styleClasses;
        }

        private List<Substring> getSubstrings() {
            return substrings;
        }

        private List<String> getStyleClasses() {
            return styleClasses;
        }
    }

    private final static class Substring {
        private final String content;
        private final Integer index;

        private Substring(String content) {
            this.content = content;
            index = null;
        }

        private Substring(String content, int index) {
            this.content = content;
            this.index = index;
        }

        private String getContent() {
            return content;
        }

        private Integer getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return String.format("[data.styling.Substring, content=%s, index=%d]", getContent(), getIndex());
        }
    }


    private static List<IntermediateStyleObject> parse(String from) {
        // styles = not{0},test{1}:error,exception;this:purple;
        if (!from.contains(";")) {
            throw new IllegalArgumentException(
                    "from does not contain the end of sequence character ';'"
            );
        }

        List<IntermediateStyleObject> styleList = new ArrayList<>();
        // ss = not{0},test{1}:error,exception
        for (String ss : from.split(";")) {
            if (!ss.contains(":")) {
                throw new IllegalArgumentException("" +
                        "from does not contain the character ':'");
            }

            List<Substring> substrings = new ArrayList<>();
            // content = not{0},test{1}
            for (String content : ss.split(":", 2)[0].split(",")) {
                if (Pattern.compile("[{]\\d[}]").matcher(content).find()) {
                    int k = Integer.parseInt(content.substring(content.indexOf("{") + 1, content.indexOf("}")));
                    substrings.add(new Substring(content.replace("{" + k + "}", ""), k));
                }
                else {
                    substrings.add(new Substring(content));
                }
            }
            styleList.add(new IntermediateStyleObject(substrings,
                    Collections.singletonList(ss.split(":", 2)[1])));
        }
        return styleList;
    }

    private static List<Style> create(String text, List<IntermediateStyleObject> styles) {
        List<Style> list = new ArrayList<>();

        for (IntermediateStyleObject style : styles) {
            for (Substring substring : style.getSubstrings()) {
                // text does not contain substring ->
                if (!text.contains(substring.getContent())) {
                    throw new IllegalArgumentException(
                            "text does not contain substring."
                    );
                }

                // indices(start, end) of all of the occurrence(s) of substring in text ->
                List<Index> indices = getIndices(text, substring.getContent());

                // at least 1 occurrence of substring in text ->
                if (indices.size() >= 1) {

                    // k in substring{k} has been specified ->
                    if (substring.getIndex() != null) {

                        // occurrence(s) of substring < k ->
                        if (indices.size() - 1 < substring.getIndex()) {
                            throw new IllegalArgumentException(
                                    "occurrence(s) of substring must be >= than {" + substring.getIndex() + "}."
                            );
                        }
                        Index index = getIndices(text, substring.getContent()).get(substring.getIndex());
                        list.add(new Style(index, style.getStyleClasses()));
                        continue;
                    }

                    for (Index index : indices) {
                        list.add(new Style(index, style.getStyleClasses()));
                    }
                }
            }
        }
        return list;
    }

    private static List<Index> getIndices(String text, String substring) {
        int noIndexVal = -1;
        Set<Integer> startIndexes = new HashSet<>();
        startIndexes.add(noIndexVal);
        List<Index> indices = new ArrayList<>();

        for (int i = 0; i < text.length() - 1; i++) {
            int startIndex = text.indexOf(substring, i);
            if (startIndexes.add(startIndex)) {
                indices.add(new Index(startIndex, startIndex + substring.length() - 1));
            }
        }
        return indices;
    }
}