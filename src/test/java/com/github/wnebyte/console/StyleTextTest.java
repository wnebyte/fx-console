package com.github.wnebyte.console;

import org.junit.Test;

public class StyleTextTest {

    @Test
    public void test00() {
        StyleText styleText = new StyleTextBuilder()
                .append("wne@MSI", "green")
                .whitespace()
                .append("MINGW64", "purple")
                .whitespace()
                .append("~", "green")
                .ln()
                .append("$", "text")
                .build();
    }
}
