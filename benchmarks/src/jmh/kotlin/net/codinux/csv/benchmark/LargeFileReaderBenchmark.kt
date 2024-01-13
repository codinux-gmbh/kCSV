package net.codinux.csv.benchmark

import net.codinux.csv.reader.CsvReader
import net.codinux.csv.reader.read
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import kotlin.io.path.Path

@BenchmarkMode(Mode.AverageTime)
class LargeFileReaderBenchmark {

    companion object {
        private val LargeCsvFilePath = Path("src/jmh/resources/data/ZhvAllStationsResponse_2022-11-11_cleaned.csv")
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    fun kCsv() {
        val reader = CsvReader().read(LargeCsvFilePath)

        val names = reader.map { it[4] }

        assert(names.size == 842_242)
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    fun fastCsv() {
        val reader = de.siegmar.fastcsv.reader.CsvReader.builder().ofCsvRecord(LargeCsvFilePath)

        val names = reader.map { row -> row.getField(4) }

        assert(names.size == 842_242)
    }

}