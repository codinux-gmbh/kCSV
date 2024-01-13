package net.codinux.csv

import net.codinux.csv.utils.Constants
import net.codinux.csv.utils.InfiniteDataReader
import net.codinux.csv.reader.CsvReader
import net.codinux.csv.reader.CsvRow
import net.codinux.csv.reader.read
import org.openjdk.jmh.annotations.*

@State(Scope.Benchmark)
class CsvReaderBenchmark {

    private val reader = CsvReader().read(InfiniteDataReader(Constants.Data))

    @TearDown
    fun teardown() {
        reader.close()
    }


    @Benchmark
    fun read(): CsvRow {
        return reader.next()
    }

}