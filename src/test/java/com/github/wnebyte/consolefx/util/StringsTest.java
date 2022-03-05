package com.github.wnebyte.consolefx.util;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import static com.github.wnebyte.consolefx.util.Strings.replaceSequence;
import static com.github.wnebyte.consolefx.util.Strings.split;

public class StringsTest {

    @Test
    public void testSplit00() {
        String s = "\nparagraph1\nparagraph2\nparagraph3\n";
        List<String> list = split(s);
        Assert.assertEquals(7, list.size());
    }

    @Test
    public void testSplit01() {
        String s = "\n\nparagraph1";
        List<String> list = split(s);
        Assert.assertEquals(3, list.size());

        s = "\n";
        list = split(s);
        Assert.assertEquals(1, list.size());

        s = "paragraph1";
        list = split(s);
        Assert.assertEquals(1, list.size());


        s = "\n\nparagraph1\n\n";
        list = split(s);
        Assert.assertEquals(5, list.size());

        s = "";
        list = split(s);
        Assert.assertEquals(0, list.size());

        s = "a";
        list = split(s);
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testReplaceSequence00() {
        String input = "wne *****";
        String s = replaceSequence(input, "mypassword", '*');
        Assert.assertEquals("wne mypassword", s);

        input = "login wne *";
        s = replaceSequence(input, "mypassword", '*');
        Assert.assertEquals("login wne mypassword", s);

        input = "login wne *";
        s = replaceSequence(input, "mypassword", '*');
        Assert.assertEquals("login wne mypassword", s);
    }
}
