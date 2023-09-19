package example

import net.codinux.csv.writer.CsvWriter
import net.codinux.csv.writer.LineDelimiter
import net.codinux.csv.writer.QuoteStrategy
import net.codinux.csv.writer.datawriter.DataWriter
import kotlin.test.Test

class CsvWriterExample {

    @Test
    fun writeToStringBuilder() {
        val stringBuilder = StringBuilder()

        CsvWriter(DataWriter.writer(stringBuilder))
            .writeRow("header1", "header2")
            .writeRow("value1", "value2")

        println(stringBuilder.toString())
    }

    @Test
    fun customSettings() {
        val stringBuilder = StringBuilder()
        val writer = CsvWriter(
            DataWriter.writer(stringBuilder),
            fieldSeparator = ';',
            quoteCharacter = '\'',
            quoteStrategy = QuoteStrategy.REQUIRED,
            lineDelimiter = LineDelimiter.CRLF,
            commentCharacter = '#'
        )

        writer
            .writeRow("header1", "header2")
            .writeRow("value;", "value2")

        println(stringBuilder.toString())
    }

}