package net.codinux.csv.example

import net.codinux.csv.writer.CsvWriter
import net.codinux.csv.writer.writer
import java.nio.file.StandardOpenOption
import kotlin.io.path.Path
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class CsvWriterExampleJvm {

    @Test
    fun readFromFile() {
        val file = Path("<path_to_csv_file>")

        CsvWriter.builder()
            .writer(file, Charsets.UTF_8, StandardOpenOption.CREATE_NEW) // UTF_8 is the default charset
            .writeRow("header1", "header2")
            .writeRow("value1", "value2")
    }

}