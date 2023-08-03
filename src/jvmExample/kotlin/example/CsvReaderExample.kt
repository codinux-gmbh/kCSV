//package example
//
//import net.codinux.csv.reader.*
//import net.codinux.csv.reader.datareader.DataReader
//import java.nio.file.Files
//import java.util.function.Consumer
//import java.util.stream.Collectors
//
//object CsvReaderExample {
//
//  @JvmStatic
//  fun main(args: Array<String>) {
//    simple()
//    forEachLambda()
//    stream()
//    iterator()
//    header()
//    advancedConfiguration()
//    file()
//  }
//
//  private fun simple() {
//    print("For-Each loop: ")
//    for (csvRow in CsvReader("foo,bar")) {
//      println(csvRow.getFields())
//    }
//  }
//
//  private fun forEachLambda() {
//    print("Loop using forEach lambda: ")
//    CsvReader("foo,bar")
//      .forEach(Consumer { x: CsvRow? -> println(x) })
//  }
//
//  private fun stream() {
//    System.out.printf(
//      "CSV contains %d rows%n",
//      CsvReader("foo,bar").count()
//    )
//  }
//
//  private operator fun iterator() {
//    print("Iterator loop: ")
//    val iterator: Iterator<CsvRow> = CsvReader("foo,bar\nfoo2,bar2").iterator()
//    while (iterator.hasNext()) {
//      val csvRow = iterator.next()
//      print(csvRow.getFields())
//      if (iterator.hasNext()) {
//        print(" || ")
//      } else {
//        println()
//      }
//    }
//  }
//
//  private fun header() {
//    val first = NamedCsvReader("header1,header2\nvalue1,value2")
//      .stream()
//      .findFirst()
//    first.ifPresent { row: NamedCsvRow -> println("Header/Name based: " + row.getField("header2")) }
//  }
//
//  private fun advancedConfiguration() {
//    val data = "#commented row\n'quoted ; column';second column\nnew row"
//    val parsedData = CsvReader.builder()
//      .fieldSeparator(';')
//      .quoteCharacter('\'')
//      .commentStrategy(CommentStrategy.SKIP)
//      .commentCharacter('#')
//      .skipEmptyRows(true)
//      .errorOnDifferentFieldCount(false)
//      .build(data)
//      .stream()
//      .map { csvRow: CsvRow -> csvRow.getFields().toString() }
//      .collect(Collectors.joining(" || "))
//    println("Parsed via advanced config: $parsedData")
//  }
//
//  private fun file() {
//    val path = Files.createTempFile("kcsv", ".csv")
//    Files.write(path, listOf("foo,bar\n"))
//    CsvReader(DataReader.reader(path)).use { csvReader -> csvReader.forEach { row -> println(row) } }
//  }
//}