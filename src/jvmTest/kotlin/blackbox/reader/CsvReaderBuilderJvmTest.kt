package blackbox.reader

import net.codinux.csv.kcsv.reader.CsvReader
import net.codinux.csv.kcsv.reader.CsvRow
import net.codinux.csv.kcsv.reader.build
import net.codinux.csv.kcsv.reader.stream
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class CsvReaderBuilderJvmTest {

  private val crb = CsvReader.builder()

  @Test
  fun path(@TempDir tempDir: Path) {
    val file = tempDir.resolve("fastcsv.csv")
    Files.write(file, DATA.toByteArray(StandardCharsets.UTF_8))
    val list: List<CsvRow>
    crb.build(file).stream().use { stream -> list = stream.collect(Collectors.toList()) }
    Assertions.assertEquals(EXPECTED, list[0].getFields())
  }

  companion object {
    private const val DATA = "foo,bar\n"
    private val EXPECTED: List<String> = mutableListOf("foo", "bar")
  }
}