package de.siegmar.fastcsv;

import net.codinux.csv.reader.CloseableIterator;
import net.codinux.csv.reader.CsvReader;
import net.codinux.csv.reader.CsvRow;
import net.codinux.csv.reader.JvmExtensionsKt;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import java.io.IOException;

public class KCsvReadBenchmark {

    @Benchmark
    public CsvRow read(final ReadState state) {
        return state.it.next();
    }

    @State(Scope.Benchmark)
    public static class ReadState {

        private CloseableIterator<CsvRow> it;

        @Setup
        public void setup() {
            it = new CsvReader(JvmExtensionsKt.dataReader(new InfiniteDataReader(Constants.DATA))).iterator();
        }

        @TearDown
        public void teardown() throws IOException {
            it.close();
        }

    }

}
