package blackbox.reader

import net.codinux.csv.kcsv.reader.NamedCsvReader
import net.codinux.csv.kcsv.reader.NamedCsvRow
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class NamedCsvReaderBuilderTest {
  private val crb = NamedCsvReader.builder()
  @ParameterizedTest
  @NullSource
  fun nullInput(text: String?) {
    Assertions.assertThrows(NullPointerException::class.java) {
      crb.build(
        text!!
      )
    }
  }

  @Test
  fun fieldSeparator() {
    val it: Iterator<NamedCsvRow> = crb.fieldSeparator(';')
      .build("h1;h2\nfoo,bar;baz").iterator()
    Assertions.assertEquals("{h1=foo,bar, h2=baz}", it.next().fields.toString())
  }

  @Test
  fun quoteCharacter() {
    val it: Iterator<NamedCsvRow> = crb.quoteCharacter('_')
      .build("h1,h2\n_foo \", __ bar_,foo \" bar").iterator()
    Assertions.assertEquals("{h1=foo \", _ bar, h2=foo \" bar}", it.next().fields.toString())
  }

  @Test
  fun commentSkip() {
    val it: Iterator<NamedCsvRow> = crb.commentCharacter(';').skipComments(true)
      .build("h1\n#foo\n;bar\nbaz").iterator()
    Assertions.assertEquals("{h1=#foo}", it.next().fields.toString())
    Assertions.assertEquals("{h1=baz}", it.next().fields.toString())
  }

  @Test
  fun builderToString() {
    Assertions.assertEquals(
      "NamedCsvReaderBuilder[fieldSeparator=,, quoteCharacter=\", "
        + "commentCharacter=#, skipComments=false]", crb.toString()
    )
  }

  @Test
  fun reader() {
    val list = crb
      .build(DATA).stream()
      .collect(Collectors.toList())
    Assertions.assertEquals(EXPECTED, list[0].fields.toString())
  }

  @Test
  fun string() {
    val list = crb.build(DATA).stream()
      .collect(Collectors.toList())
    Assertions.assertEquals(EXPECTED, list[0].fields.toString())
  }

  @Test
  @Throws(IOException::class)
  fun path(@TempDir tempDir: Path) {
    val file = tempDir.resolve("fastcsv.csv")
    Files.write(file, DATA.toByteArray(StandardCharsets.UTF_8))
    val list: List<NamedCsvRow>
    crb.build(file).stream().use { stream -> list = stream.collect(Collectors.toList()) }
    Assertions.assertEquals(EXPECTED, list[0].fields.toString())
  }

  @Test
  fun chained() {
    val reader = NamedCsvReader.builder()
      .fieldSeparator(',')
      .quoteCharacter('"')
      .commentCharacter('#')
      .skipComments(false)
      .build("foo")
    Assertions.assertNotNull(reader)
  }

  companion object {
    private const val DATA = "header1,header2\nfoo,bar\n"
    private const val EXPECTED = "{header1=foo, header2=bar}"
  }
}