package blackbox.writer

import net.codinux.csv.use
import net.codinux.csv.writer.CsvWriter
import net.codinux.csv.writer.writer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

class CsvWriterTest {

  @Test
  fun path(@TempDir tempDir: Path) {
    val file = tempDir.resolve("kcsv.csv")
    CsvWriter.builder().writer(file).use { csv -> csv.writeRow("value1", "value2") }
    Assertions.assertEquals(
      "value1,value2\r\n",
      String(Files.readAllBytes(file), StandardCharsets.UTF_8)
    )
  }

  @Test
  fun streaming() {
    val stream = Stream.of(listOf("header1", "header2"), listOf("value1", "value2"))
    val sw = StringWriter()
    val csvWriter = CsvWriter.builder().writer(sw)
    stream.forEach { values: List<String> -> csvWriter.writeRow(values) }
    Assertions.assertEquals("header1,header2\r\nvalue1,value2\r\n", sw.toString())
  }

}