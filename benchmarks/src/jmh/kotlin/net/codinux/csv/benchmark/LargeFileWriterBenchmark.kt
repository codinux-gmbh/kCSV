package net.codinux.csv.benchmark

import net.codinux.csv.reader.CsvReader
import net.codinux.csv.reader.read
import net.codinux.csv.writer.CsvWriter
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import java.io.StringWriter
import kotlin.io.path.Path

@BenchmarkMode(Mode.AverageTime)
class LargeFileWriterBenchmark {

    companion object {
        private val LargeCsvFilePath = Path("src/jmh/resources/data/ZhvAllStationsResponse_2022-11-11_cleaned.csv")

        private val data = CsvReader().read(LargeCsvFilePath).map { row -> row.fields }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    fun kCsv() {
        val stringBuilder = StringBuilder()
        val writer = CsvWriter.builder().writer(stringBuilder)

        data.forEach { row ->
            writer.writeRow(row)
        }

        assertResult(stringBuilder.toString())
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    fun fastCsv() {
        val stringWriter = StringWriter()
        val writer = de.siegmar.fastcsv.writer.CsvWriter.builder().build(stringWriter)

        data.forEach { row ->
            writer.writeRecord(row)
        }

        assertResult(stringWriter.toString())
    }

    private fun assertResult(result: String) {
        assert(result.length == 842_242) // TODO
    }

}