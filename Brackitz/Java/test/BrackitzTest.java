import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Test
public class BrackitzTest {

    static final String YES = "YES";
    static final String NO = "NO";

    static String[] main(String[] values) {
        List<String> ret = new ArrayList<>();
        for (String input : values) {
            ret.add(new Brackitz(input).isValid() ? YES : NO);
        }
        return ret.toArray(new String[0]);
    }

    public void shouldPass() {
        assertTrue(new Brackitz("{}").isValid());
        assertTrue(new Brackitz("").isValid());
        assertTrue(new Brackitz("[{(())}]").isValid());

        assertFalse(new Brackitz("}").isValid());
        assertFalse(new Brackitz("{").isValid());
        assertFalse(new Brackitz("{{}").isValid());
        assertFalse(new Brackitz("{}}").isValid());
        assertFalse(new Brackitz("({}").isValid());
        assertFalse(new Brackitz("{})").isValid());
    }
}