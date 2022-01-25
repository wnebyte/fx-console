package com.github.wnebyte.console.util;

import org.junit.Assert;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import static com.github.wnebyte.console.util.StringUtils.*;

public class UtilsTest {

    @Test
    public void test02() {
        String s = "test|hej";
        String substring = s.substring(0, s.indexOf("|"));
        System.out.println(substring);
    }

    @Test
    public void test03() {
        String s = "\nparagraph1\nparagraph2\nparagraph3\n";
        List<String> list = split(s);
        System.out.println(Arrays.toString(list.toArray()) + "\n" + list.size());
        Assert.assertEquals(7, list.size());
    }

    @Test
    public void test04() {
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
    public void test05() {
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
