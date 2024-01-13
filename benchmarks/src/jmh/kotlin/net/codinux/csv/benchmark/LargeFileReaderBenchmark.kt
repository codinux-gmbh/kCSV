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

        assertResult(names)
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    fun kCsv_DoNotMaterializeOtherColumns() {
        val columnsToIgnore = hashSetOf(0, 1, 2, 3, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17)
        val reader = CsvReader(ignoreColumns = columnsToIgnore).read(LargeCsvFilePath)

        val names = reader.map { it[4] }

        assertResult(names)
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    fun fastCsv() {
        val reader = de.siegmar.fastcsv.reader.CsvReader.builder().ofCsvRecord(LargeCsvFilePath)

        val names = reader.map { row -> row.getField(4) }

        assertResult(names)
    }

    private fun assertResult(names: List<String>) {
        assert(names.size == 842_242)

        val stopPointsWithEmptyNames = names.filter { it.isBlank() }
        assert(stopPointsWithEmptyNames.size == 31852)

        assert(names.contains("S+U Berlin Hauptbahnhof (tief)"))
        assert(names.contains("Glückstadt"))
        assert(names.contains("Frankfurt (Main) Flughafen Fernbahnhof"))
    }

}