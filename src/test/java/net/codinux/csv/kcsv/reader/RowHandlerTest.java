package net.codinux.csv.kcsv.reader;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RowHandlerTest {

    @Test
    public void test() {
        final RowHandler rh = new RowHandler(1);
        rh.add("foo");
        rh.add("bar");
        final CsvRow csvRow = rh.buildAndReset();

        assertNotNull(csvRow);
        assertEquals("CsvRow[originalLineNumber=1, fields=[foo, bar], comment=false]",
            csvRow.toString());
    }

}
