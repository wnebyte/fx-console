import org.junit.Assert;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import static com.github.wnebyte.fxconsole.util.StringUtils.*;

public class UtilsTest {

    @Test
    public void test00() {
        String s = "paragraph1\r\nparagraph2\nparagraph3";
        List<String> list = split(s);
        Assert.assertEquals(3, list.size());

        s = "paragraph1" + System.lineSeparator() + "paragraph2" + "\n" + "paragraph3";
        list = split(s);
        Assert.assertEquals(3, list.size());
    }

    @Test
    public void test01() {
        String s = "paragraph1";
        List<String> list = split(s);
        System.out.println(Arrays.toString(list.toArray()));

        s = "paragraph1\n";
        list = split(s);
        System.out.println(Arrays.toString(list.toArray()));
    }

    @Test
    public void test02() {
        String s = "test|hej";
        String substring = s.substring(0, s.indexOf("|"));
        System.out.println(substring);
    }

    @Test
    public void test03() {
        String s = "\nparagraph1\nparagraph2\nparagraph3\n";
        List<String> list = toParagraphs(s);
        System.out.println(Arrays.toString(list.toArray()) + "\n" + list.size());
        Assert.assertEquals(7, list.size());
    }

    @Test
    public void test04() {
        String s = "\n\nparagraph1";
        List<String> list = toParagraphs(s);
        Assert.assertEquals(3, list.size());

        s = "\n";
        list = toParagraphs(s);
        Assert.assertEquals(1, list.size());

        s = "paragraph1";
        list = toParagraphs(s);
        Assert.assertEquals(1, list.size());


        s = "\n\nparagraph1\n\n";
        list = toParagraphs(s);
        Assert.assertEquals(5, list.size());

        s = "";
        list = toParagraphs(s);
        Assert.assertEquals(0, list.size());

        s = "a";
        list = toParagraphs(s);
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
