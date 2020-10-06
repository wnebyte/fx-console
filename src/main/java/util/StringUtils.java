package util;

public class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.equals("");
    }

    public static boolean containsLineSeparator(String s) {
        return s.contains(System.lineSeparator());
    }
}
