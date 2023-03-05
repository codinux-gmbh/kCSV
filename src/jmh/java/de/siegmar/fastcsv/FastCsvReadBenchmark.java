package de.siegmar.fastcsv;

import net.codinux.csv.kcsv.reader.CloseableIterator;
import net.codinux.csv.kcsv.reader.CsvReader;
import net.codinux.csv.kcsv.reader.CsvRow;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import java.io.IOException;

public class FastCsvReadBenchmark {

    @Benchmark
    public CsvRow read(final ReadState state) {
        return state.it.next();
    }

    @State(Scope.Benchmark)
    public static class ReadState {

        private CloseableIterator<CsvRow> it;

        @Setup
        public void setup() {
            it = new CsvReader(new InfiniteDataReader(Constants.DATA)).iterator();
        }

        @TearDown
        public void teardown() throws IOException {
            it.close();
        }

    }

}
