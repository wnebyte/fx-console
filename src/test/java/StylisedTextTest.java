import com.github.wnebyte.fxconsole.StylisedText;
import com.github.wnebyte.fxconsole.util.StylisedTextBuilder;
import org.junit.Test;

public class StylisedTextTest {

    @Test
    public void test00() {
        StylisedText stylisedText = new StylisedTextBuilder()
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
