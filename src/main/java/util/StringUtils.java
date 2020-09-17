package util;

import java.nio.charset.Charset;

public class StringUtils {

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.equals("");
    }

    public static String encode(String s, Charset charset) {
        return new String(s.getBytes(), charset);
    }

    public static boolean containsLineSeparator(String s) {
        return s.contains("\n");
    }
}
