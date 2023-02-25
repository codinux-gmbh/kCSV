package example

import net.codinux.csv.kcsv.reader.NamedCsvReader
import net.codinux.csv.kcsv.writer.CsvWriter
import net.codinux.csv.kcsv.writer.LineDelimiter
import net.codinux.csv.kcsv.writer.QuoteStrategy
import java.io.IOException
import java.io.StringWriter
import java.nio.file.Files

object CsvWriterExample {
  @Throws(IOException::class)
  @JvmStatic
  fun main(args: Array<String>) {
    simple()
    advancedConfiguration()
    file()
    transformData()
  }

  private fun simple() {
    val sw = StringWriter()
    CsvWriter.builder().build(sw).writeRow("value1", "value2")
    print("Simple CSV: $sw")
  }

  private fun advancedConfiguration() {
    val sw = StringWriter()
    CsvWriter.builder()
      .fieldSeparator(';')
      .quoteCharacter('\'')
      .quoteStrategy(QuoteStrategy.ALWAYS)
      .lineDelimiter(LineDelimiter.LF)
      .build(sw)
      .writeComment("File created by foo on 2021-02-07")
      .writeRow("header1", "header2")
      .writeRow("value1", "value2")
    println("Advanced CSV:")
    println(sw)
  }

  @Throws(IOException::class)
  private fun file() {
    val path = Files.createTempFile("fastcsv", ".csv")
    CsvWriter.builder().build(path).use { csv ->
      csv
        .writeRow("header1", "header2")
        .writeRow("value1", "value2")
    }
    Files.lines(path)
      .forEach { line: String -> println("Line from path: $line") }
  }

  @Throws(IOException::class)
  private fun transformData() {
    val out = StringWriter()
    NamedCsvReader.builder().build(
      "firstname,lastname,age\njohn,smith,30"
    ).use { reader ->
      CsvWriter.builder().build(out).use { writer ->
        // transform firstname,lastname,age => name,age
        writer.writeRow("name", "age")
        for (csvRow in reader) {
          writer.writeRow(
            csvRow.getField("firstname") + " " + csvRow.getField("lastname"),
            csvRow.getField("age")
          )
        }
      }
    }
    println("Transformed CSV:")
    println(out)
  }
}