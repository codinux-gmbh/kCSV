package blackbox.reader

import net.codinux.csv.kcsv.reader.CommentStrategy
import net.codinux.csv.kcsv.reader.CsvReader
import net.codinux.csv.kcsv.reader.CsvRow
import net.codinux.csv.kcsv.reader.build
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

class CsvReaderBuilderTest {
  private val crb = CsvReader.builder()
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
    val it: Iterator<CsvRow> = crb.fieldSeparator(';')
      .build("foo,bar;baz").iterator()
    Assertions.assertEquals(mutableListOf("foo,bar", "baz"), it.next().getFields())
  }

  @Test
  fun quoteCharacter() {
    val it: Iterator<CsvRow> = crb.quoteCharacter('_')
      .build("_foo \", __ bar_,foo \" bar").iterator()
    Assertions.assertEquals(mutableListOf("foo \", _ bar", "foo \" bar"), it.next().getFields())
  }

  @Test
  fun commentSkip() {
    val it: Iterator<CsvRow> = crb.commentCharacter(';').commentStrategy(CommentStrategy.SKIP)
      .build("#foo\n;bar\nbaz").iterator()
    Assertions.assertEquals(listOf("#foo"), it.next().getFields())
    Assertions.assertEquals(listOf("baz"), it.next().getFields())
  }

  @Test
  fun builderToString() {
    Assertions.assertEquals(
      "CsvReaderBuilder[fieldSeparator=,, quoteCharacter=\", "
        + "commentStrategy=NONE, commentCharacter=#, skipEmptyRows=true, "
        + "errorOnDifferentFieldCount=false, hasHeader=false]", crb.toString()
    )
  }

  @Test
  fun reader() {
    val list = crb
      .build(DATA).stream()
      .collect(Collectors.toList())
    Assertions.assertEquals(EXPECTED, list[0].getFields())
  }

  @Test
  fun string() {
    val list = crb.build(DATA).stream()
      .collect(Collectors.toList())
    Assertions.assertEquals(EXPECTED, list[0].getFields())
  }

  @Test
  @Throws(IOException::class)
  fun path(@TempDir tempDir: Path) {
    val file = tempDir.resolve("fastcsv.csv")
    Files.write(file, DATA.toByteArray(StandardCharsets.UTF_8))
    val list: List<CsvRow>
    crb.build(file).stream().use { stream -> list = stream.collect(Collectors.toList()) }
    Assertions.assertEquals(EXPECTED, list[0].getFields())
  }

  @Test
  fun chained() {
    val reader = CsvReader.builder()
      .fieldSeparator(',')
      .quoteCharacter('"')
      .commentStrategy(CommentStrategy.NONE)
      .commentCharacter('#')
      .skipEmptyRows(true)
      .errorOnDifferentFieldCount(false)
      .build("foo")
    Assertions.assertNotNull(reader)
  }

  companion object {
    private const val DATA = "foo,bar\n"
    private val EXPECTED: List<String> = mutableListOf("foo", "bar")
  }
}