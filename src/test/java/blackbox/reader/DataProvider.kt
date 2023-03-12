package blackbox.reader

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.regex.Pattern

object DataProvider {
  private val LINE_PATTERN = Pattern.compile("^(?<input>\\S+)\\s+(?<expected>\\S+)(?:\\s+\\[(?<flags>\\w+)])?")

  fun loadTestData(name: String): List<TestData> {
    val data: MutableList<TestData> = ArrayList()
    resource(name).use { r ->
      var lineNo = 0
      while (true) {
        val line = r.readLine() ?: break
        lineNo++
        if (line.isEmpty() || line[0] == '#') {
          continue
        }
        val matcher = LINE_PATTERN.matcher(line)
        if (matcher.matches()) {
          val input = matcher.group("input")
          val expected = matcher.group("expected")
          val flags = matcher.group("flags")
          data.add(TestData(lineNo, line, input, expected, flags))
        }
      }
    }
    return data
  }

  private fun resource(name: String): BufferedReader {
    return BufferedReader(
      InputStreamReader(
        Objects.requireNonNull(DataProvider::class.java.getResourceAsStream(name)), StandardCharsets.UTF_8
      )
    )
  }

  class TestData internal constructor(
    val lineNo: Int, val line: String, val input: String, val expected: String,
    flags: String?
  ) {
    val isSkipEmptyLines: Boolean
    val isReadComments: Boolean
    val isSkipComments: Boolean

    init {
      isSkipEmptyLines = "skipEmptyLines" == flags
      isReadComments = "readComments" == flags
      isSkipComments = "skipComments" == flags
    }

    override fun toString(): String {
      return StringJoiner(", ", TestData::class.java.simpleName + "[", "]")
        .add("lineNo=$lineNo")
        .add("line='$line'")
        .add("input='$input'")
        .add("expected='$expected'")
        .add("skipEmptyLines=" + isSkipEmptyLines)
        .add("readComments=" + isReadComments)
        .add("skipComments=" + isSkipComments)
        .toString()
    }
  }
}