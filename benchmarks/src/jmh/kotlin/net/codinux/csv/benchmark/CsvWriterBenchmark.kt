package net.codinux.csv.benchmark

import net.codinux.csv.benchmark.utils.Constants
import net.codinux.csv.benchmark.utils.NullWriter
import net.codinux.csv.writer.CsvWriter
import net.codinux.csv.writer.LineDelimiter
import net.codinux.csv.writer.writer
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
class CsvWriterBenchmark {

    private lateinit var writer: CsvWriter

    @Setup
    fun setup(blackhole: Blackhole) {
        writer = CsvWriter.builder(lineDelimiter = LineDelimiter.LF, bufferSize = 0)
            .writer(NullWriter(blackhole))
    }

    @TearDown
    fun teardown() {
        writer.close()
    }


    @Benchmark
    fun write() {
        writer.writeRow(*Constants.Row)
    }

}