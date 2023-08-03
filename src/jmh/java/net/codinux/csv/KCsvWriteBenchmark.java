package de.siegmar.fastcsv;

import net.codinux.csv.writer.CsvWriter;
import net.codinux.csv.writer.LineDelimiter;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;

public class KCsvWriteBenchmark {

    @Benchmark
    public void write(final WriteState state) {
        state.writer.writeRow(Constants.ROW);
    }

    @State(Scope.Benchmark)
    public static class WriteState {

        private CsvWriter writer;

        @Setup
        public void setup(final Blackhole bh) {
            writer = CsvWriter.builder()
                .lineDelimiter(LineDelimiter.LF)
                .bufferSize(0)
                .build(new NullWriter(bh));
        }

        @TearDown
        public void teardown() throws IOException {
            writer.close();
        }

    }

}
