package blackbox.reader

import net.codinux.csv.reader.CsvReader
import net.codinux.csv.reader.read
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

class CsvReaderBuilderJvmTest {

  @Test
  fun path(@TempDir tempDir: Path) {
    val file = tempDir.resolve("kcsv.csv")
    Files.write(file, DATA.toByteArray(StandardCharsets.UTF_8))
    val reader = CsvReader.builder().build()
    val rows = reader.read(file).toList()
    Assertions.assertEquals(EXPECTED, rows[0].fields)
  }

  @Test
  fun path_WithHeaderRow(@TempDir tempDir: Path) {
    val file = tempDir.resolve("kcsv.csv")
    Files.write(file, DATA_WITH_HEADER.toByteArray(StandardCharsets.UTF_8))
    val reader = CsvReader.builder().hasHeaderRow(true).build()
    val rows = reader.read(file).toList()
    Assertions.assertEquals(EXPECTED, rows[0].fields)
  }

  companion object {
    private const val DATA = "foo,bar\n"
    private const val DATA_WITH_HEADER = "header1,header2\nfoo,bar\n"
    private val EXPECTED: List<String> = mutableListOf("foo", "bar")
  }
}