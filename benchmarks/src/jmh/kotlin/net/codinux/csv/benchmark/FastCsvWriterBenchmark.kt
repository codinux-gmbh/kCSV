package net.codinux.csv.benchmark

import de.siegmar.fastcsv.writer.CsvWriter
import de.siegmar.fastcsv.writer.LineDelimiter
import net.codinux.csv.benchmark.utils.Constants
import net.codinux.csv.benchmark.utils.NullWriter
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
class FastCsvWriterBenchmark {

    private lateinit var writer: CsvWriter

    @Setup
    fun setup(blackhole: Blackhole) {
        writer = CsvWriter.builder().lineDelimiter(LineDelimiter.LF).bufferSize(0)
            .build(NullWriter(blackhole))
    }

    @TearDown
    fun teardown() {
        writer.close()
    }


    @Benchmark
    fun write() {
        writer.writeRecord(*Constants.Row)
    }

}