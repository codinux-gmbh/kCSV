package example

import net.codinux.csv.kcsv.reader.*
import java.io.IOException
import java.nio.file.Files
import java.util.function.Consumer
import java.util.stream.Collectors

object CsvReaderExample {
  @Throws(IOException::class)
  @JvmStatic
  fun main(args: Array<String>) {
    simple()
    forEachLambda()
    stream()
    iterator()
    header()
    advancedConfiguration()
    file()
  }

  private fun simple() {
    print("For-Each loop: ")
    for (csvRow in CsvReader.builder().build("foo,bar")) {
      println(csvRow.getFields())
    }
  }

  private fun forEachLambda() {
    print("Loop using forEach lambda: ")
    CsvReader.builder().build("foo,bar")
      .forEach(Consumer { x: CsvRow? -> println(x) })
  }

  private fun stream() {
    System.out.printf(
      "CSV contains %d rows%n",
      CsvReader.builder().build("foo,bar").stream().count()
    )
  }

  private operator fun iterator() {
    print("Iterator loop: ")
    val iterator: Iterator<CsvRow> = CsvReader.builder()
      .build("foo,bar\nfoo2,bar2").iterator()
    while (iterator.hasNext()) {
      val csvRow = iterator.next()
      print(csvRow.getFields())
      if (iterator.hasNext()) {
        print(" || ")
      } else {
        println()
      }
    }
  }

  private fun header() {
    val first = NamedCsvReader.builder()
      .build("header1,header2\nvalue1,value2")
      .stream().findFirst()
    first.ifPresent { row: NamedCsvRow -> println("Header/Name based: " + row.getField("header2")) }
  }

  private fun advancedConfiguration() {
    val data = "#commented row\n'quoted ; column';second column\nnew row"
    val parsedData = CsvReader.builder()
      .fieldSeparator(';')
      .quoteCharacter('\'')
      .commentStrategy(CommentStrategy.SKIP)
      .commentCharacter('#')
      .skipEmptyRows(true)
      .errorOnDifferentFieldCount(false)
      .build(data)
      .stream()
      .map { csvRow: CsvRow -> csvRow.getFields().toString() }
      .collect(Collectors.joining(" || "))
    println("Parsed via advanced config: $parsedData")
  }

  @Throws(IOException::class)
  private fun file() {
    val path = Files.createTempFile("fastcsv", ".csv")
    Files.write(path, listOf("foo,bar\n"))
    CsvReader.builder().build(path).use { csvReader -> csvReader.forEach(Consumer { x: CsvRow? -> println(x) }) }
  }
}