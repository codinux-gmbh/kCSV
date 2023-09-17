package net.codinux.csv.writer

import net.codinux.csv.TestData
import net.codinux.csv.containsNot
import net.codinux.csv.forEachLineIndexed
import net.codinux.csv.reader.CsvReader
import net.codinux.csv.reader.read
import net.codinux.csv.use
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.io.Reader

class CsvWriterLargeFileTest {

  @Test
  fun writeLargeFile() {
    val reader = CsvReader(
      fieldSeparator = ';',
      hasHeaderRow = true,
      ignoreInvalidQuoteChars = true
    ).read(TestData.largeCsvFileWithInvalidQuoteCharsZipInputStream())

    val destinationFile = File.createTempFile("kcsv_large_file_test_", ".csv")

    CsvFormat(';', quoteStrategy = QuoteStrategy.ALWAYS).writer(destinationFile).use { underTest ->
      underTest.writeRow(reader.header)

      reader.forEach { row -> underTest.writeRow(row.fields) }
    }

    // reading the whole text at once is too large, results in OutOfMemoryError, so do it line by line
    val writtenFileReader = destinationFile.bufferedReader()
    val originalFileReader = TestData.largeCsvFileWithInvalidQuoteCharsZipInputStream().bufferedReader()

    originalFileReader.forEachLineIndexed { index, expectedLine ->
      val actualLine = writtenFileReader.readLine()
      if (LinesWithIllegalQuotes.containsNot(index)) {
        assertThat(actualLine).isEqualTo(expectedLine)
      }
    }
  }

  companion object {
    private val LinesWithIllegalQuotes = hashSetOf(
      19725,
      28544,
      54164,
      78854,
      78860,
      231114,
      231129,
      321344,
      321345,
      364112,
      364113,
      364684,
      364685,
      365349,
      365350,
      365351,
      594732,
      650944,
      655490,
      655741,
      668685,
      668686,
      673801,
      673821,
      674001,
      686478,
      686479,
      708971,
      797547,
      807671,
      807673,
      812708,
      812711
    )
  }

}