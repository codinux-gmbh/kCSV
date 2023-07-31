package blackbox.reader

import net.codinux.csv.kcsv.reader.NamedCsvReader
import net.codinux.csv.kcsv.reader.NamedCsvRow
import net.codinux.csv.kcsv.reader.datareader.DataReader
import net.codinux.csv.kcsv.reader.reader
import net.codinux.csv.kcsv.reader.stream
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class NamedCsvReaderBuilderJvmTest {

  private val crb = NamedCsvReader.builder()

  @Test
  fun path(@TempDir tempDir: Path) {
    val file = tempDir.resolve("fastcsv.csv")
    Files.write(file, DATA.toByteArray(StandardCharsets.UTF_8))
    val list: List<NamedCsvRow>
    crb.build(DataReader.reader(file)).stream().use { stream -> list = stream.collect(Collectors.toList()) }
    Assertions.assertEquals(EXPECTED, list[0].fields.toString())
  }

  companion object {
    private const val DATA = "header1,header2\nfoo,bar\n"
    private const val EXPECTED = "{header1=foo, header2=bar}"
  }
}