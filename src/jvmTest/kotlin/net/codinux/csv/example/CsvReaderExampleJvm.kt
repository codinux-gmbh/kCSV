package net.codinux.csv.example

import net.codinux.csv.reader.CsvReader
import net.codinux.csv.reader.read
import kotlin.io.path.Path
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class CsvReaderExampleJvm {

    @Test
    fun readFromFile() {
        val file = Path("<path_to_csv_file>")

        CsvReader()
            .read(file, Charsets.UTF_8) // UTF_8 is the default charset
            .forEach { row -> println(row.fields.joinToString()) }
    }

}