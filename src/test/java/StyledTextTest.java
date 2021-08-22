import com.github.wnebyte.fxconsole.StyledText;
import com.github.wnebyte.fxconsole.util.StyledTextBuilder;
import org.junit.Test;

public class StyledTextTest {

    @Test
    public void test00() {
        StyledText styledText = new StyledTextBuilder()
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
