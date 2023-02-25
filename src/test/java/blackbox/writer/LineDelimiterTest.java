package blackbox.writer;

import net.codinux.csv.kcsv.writer.LineDelimiter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LineDelimiterTest {

    @Test
    public void test() {
        assertEquals("\n", LineDelimiter.LF.toString());
        assertEquals("\r", LineDelimiter.CR.toString());
        assertEquals("\r\n", LineDelimiter.CRLF.toString());
        assertEquals(System.lineSeparator(), LineDelimiter.PLATFORM.toString());
    }

    @Test
    public void testOf() {
        assertEquals(LineDelimiter.CRLF, LineDelimiter.of("\r\n"));
        assertEquals(LineDelimiter.LF, LineDelimiter.of("\n"));
        assertEquals(LineDelimiter.CR, LineDelimiter.of("\r"));

        final IllegalArgumentException e =
            assertThrows(IllegalArgumentException.class, () -> LineDelimiter.of(";"));
        assertEquals("Unknown line delimiter: ;", e.getMessage());
    }

}
