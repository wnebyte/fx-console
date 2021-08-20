import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;


public class EncodingTest {

    @Test
    public void test00() {
        String a = "ä";
        System.out.println(a);
    }

    @Test
    public void test01() throws IOException {
        String a = "ä";
        OutputStreamWriter out = new OutputStreamWriter(System.out, StandardCharsets.UTF_8);
        out.write(a);
        out.flush();
    }

    @Test
    public void test03() {
        String a = "ä";
        System.out.println(a);
    }

    @Test
    public void test04() {
        String text = "hello äää";
        System.out.println(text);
        ByteBuffer buffer = StandardCharsets.UTF_16.encode(text);
        String utf8EncodedString = StandardCharsets.UTF_16.decode(buffer).toString();
        Assert.assertEquals(text, utf8EncodedString);
    }

}
