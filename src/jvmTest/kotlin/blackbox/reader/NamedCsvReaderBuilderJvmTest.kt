package blackbox.reader

import net.codinux.csv.reader.NamedCsvReader
import net.codinux.csv.reader.NamedCsvRow
import net.codinux.csv.reader.datareader.DataReader
import net.codinux.csv.reader.reader
import net.codinux.csv.reader.stream
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class NamedCsvReaderBuilderJvmTest {

  private val crb = NamedCsvReader.builder()

  @Test
  fun path(@TempDir tempDir: Path) {
    val file = tempDir.resolve("kcsv.csv")
    Files.write(file, DATA.toByteArray(StandardCharsets.UTF_8))
    val list: List<NamedCsvRow>
    crb.build().read(DataReader.reader(file)).stream().use { stream -> list = stream.collect(Collectors.toList()) }
    Assertions.assertEquals(EXPECTED, list[0].fields.toString())
  }

  companion object {
    private const val DATA = "header1,header2\nfoo,bar\n"
    private const val EXPECTED = "{header1=foo, header2=bar}"
  }
}