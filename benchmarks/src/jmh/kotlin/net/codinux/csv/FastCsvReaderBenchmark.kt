package net.codinux.csv

import de.siegmar.fastcsv.reader.CsvReader
import de.siegmar.fastcsv.reader.CsvRecord
import net.codinux.csv.utils.Constants
import net.codinux.csv.utils.InfiniteDataReader
import org.openjdk.jmh.annotations.*

@State(Scope.Benchmark)
class FastCsvReaderBenchmark {

    private val reader = CsvReader.builder().ofCsvRecord(InfiniteDataReader(Constants.Data)).iterator()

    @TearDown
    fun teardown() {
        reader.close()
    }


    @Benchmark
    fun read(): CsvRecord {
        return reader.next()
    }

}