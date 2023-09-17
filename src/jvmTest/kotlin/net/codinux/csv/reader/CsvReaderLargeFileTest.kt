package net.codinux.csv.reader

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.doubles.shouldNotBeNaN
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import net.codinux.csv.TestData
import net.codinux.csv.containsNot
import net.codinux.csv.countOccurrencesOf
import net.codinux.csv.indexOfOrNull
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CsvReaderLargeFileTest {

  @Test
  fun readLargeFile() {
    val underTest = CsvReader(
      fieldSeparator = ';',
      hasHeaderRow = true,
      reuseRowInstance = true,
      ignoreInvalidQuoteChars = true
    ).read(TestData.largeCsvFileWithInvalidQuoteCharsZipInputStream())

    val lineNumbers = mutableSetOf<Long>()

    val stations = underTest.mapIndexed { index, row ->
      lineNumbers.add(row.originalLineNumber)
      assertRow(index, row)
      row.getString("Name") to row.getString("DHID")
    }

    underTest.close()

    assertThat(stations).hasSize(842271)
    assertThat(lineNumbers).hasSize(stations.size)
    assertThat(stations.map { it.second }.toSet()).hasSize(stations.size)
  }

  private fun assertRow(index: Int, row: CsvRow) {
    index.shouldBe(row.originalLineNumber - 2) // -2 due to heder row and that originalLineNumber is one based and index zero based
    row.getInt("SeqNo").shouldBe(index)
    row.getString("Type").shouldBeIn("S", "A", "Q")

    assertDhId(index, row.getString("DHID"))
    assertDhId(index, row.getString("Parent"))

    row.getString("Latitude").replace(',', '.').toDoubleOrNull().shouldNotBeNull().shouldNotBeNaN()
    row.getString("Longitude").replace(',', '.').toDoubleOrNull().shouldNotBeNull().shouldNotBeNaN()
  }

  private fun assertDhId(rowIndex: Int, dhId: String) {
    dhId.startsWith("de:").shouldBeTrue() // ZHV contains only German DHID; dhId.shouldStartWith("de:") is very slow, takes 40 s for all calls!
    dhId.countOccurrencesOf(":").shouldBeIn(2, 3, 4) // a DHID has three to five segments separated by ';'

    val secondIndexOfColon = dhId.indexOf(':', 4)
    val thirdIndexOfColon = dhId.indexOfOrNull(':', secondIndexOfColon + 1) ?: dhId.length

    val municipalityCode = dhId.substring(3, secondIndexOfColon)
    shouldNotThrowAny { municipalityCode.toInt() }

    val stationCode = dhId.substring(secondIndexOfColon + 1, thirdIndexOfColon)
    if (StationsWithNonIntegerStationIds.containsNot(rowIndex)) { // Hagnau Hafen and it descendants have an invalid DHID of de:08435:NVB_208
      shouldNotThrowAny { stationCode.toInt() }
    }
  }

  companion object {
    private val StationsWithNonIntegerStationIds = listOf(
      80516,
      80517,
      80518,
    )
  }

}