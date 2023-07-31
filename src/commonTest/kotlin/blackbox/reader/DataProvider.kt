package blackbox.reader

object DataProvider {

  // ignore 'Redundant escape character' warning, without it JavaScript will crash
  private val LINE_PATTERN = Regex("^(?<input>\\S+)\\s+(?<expected>\\S+)(?:\\s+\\[(?<flags>\\w+)\\])?")

  fun loadTestData(): List<TestData> {
    val data: MutableList<TestData> = ArrayList()
    var lineNo = 0

    CsvTestData.split('\n').forEach { line ->
      lineNo++
      if (line.isEmpty() || line[0] == '#') {
        return@forEach
      }

      val matcher = LINE_PATTERN.find(line)
      if (matcher != null) {
        val input = matcher.groups[1]!!.value // "input"
        val expected = matcher.groups[2]!!.value // "expected"
        val flags = matcher.groups[3]?.value // "flags"
        data.add(TestData(lineNo, line, input, expected, flags))
      }
    }

    return data
  }

  private val CsvTestData = """
    # Format: INPUT EXPECTED [FLAGS]
    # Format of INPUT: SPACE = ␣ // CR = ␍ // LF = ␊
    # Format of EXPECTED: New row = ⏎ // Separated columns = ↷ // Empty field = ◯ // Empty list = ∅

    # Simple columns / Single Row
    D                       D
    D,D                     D↷D
    ,D                      ◯↷D

    # Spaces
    ␣                       ␣
    ␣,␣                     ␣↷␣
    ,␣                      ◯↷␣
    ␣D                      ␣D
    ␣D␣,␣D␣                 ␣D␣↷␣D␣

    # Trailing field separator
    D,                      D↷◯
    A,␊B                    A↷◯⏎B
    ␣,                      ␣↷◯
    ␣,␊D                    ␣↷◯⏎D

    # Newlines with Linefeed (Unix)
    A␊B                     A⏎B
    D␊                      D
    ␊D                      ◯⏎D
    ␊D                      D           [skipEmptyLines]

    # Newlines with Carriage-Return (Legacy Mac)
    A␍B                     A⏎B
    D␍                      D
    ␍D                      ◯⏎D
    ␍D                      D           [skipEmptyLines]

    # Newlines with Linefeed and Carriage-Return (Windows)
    A␍␊B                    A⏎B
    D␍␊                     D
    ␍␊D                     ◯⏎D
    ␍␊D                     D           [skipEmptyLines]

    # Quotation
    "␣D␣"                   ␣D␣
    "D"                     D
    "D",D                   D↷D
    D,"D"                   D↷D

    # Open Quotation
    A,"B                    A↷B
    A,B"                    A↷B"
    "A,B                    A,B

    # Escape Quotation
    ""${'"'}D"                   "D
    "D""${'"'}                   D"
    "A""B"                  A"B

    # Multiline
    "A␊B"                   A␊B
    "A␍B"                   A␍B
    "A␍␊B"                  A␍␊B

    # Different column count
    A␊B,C                   A⏎B↷C
    A,B␊C                   A↷B⏎C

    # Comments
    A␊;B,C␊D                A⏎;B↷C⏎D

    A␊;B,C␊D                A⏎B,C⏎D     [readComments]
    A␍;B,C␍D                A⏎B,C⏎D     [readComments]
    A␍␊;B,C␍␊D              A⏎B,C⏎D     [readComments]
    ;A␊;B␊C                 A⏎B⏎C       [readComments]
    ;A␊␣;B␊C                A⏎␣;B⏎C     [readComments]
    A,;B,C                  A↷;B↷C      [readComments]
    ;                       ◯           [readComments]
    ;␊;                     ◯⏎◯         [readComments]
    ;A␊;                    A⏎◯         [readComments]
    ;␊;A                    ◯⏎A         [readComments]
    ;␊;␍␊;                  ◯⏎◯⏎◯       [readComments]

    A␊;B,C␊D                A⏎D         [skipComments]
    A␍;B,C␍D                A⏎D         [skipComments]
    A␍␊;B,C␍␊D              A⏎D         [skipComments]
    ;A␊;B␊C                 C           [skipComments]
    ;A␊␣;B␊C                ␣;B⏎C       [skipComments]
    A,;B,C                  A↷;B↷C      [skipComments]
    ;                       ∅           [skipComments]
    ;␊;                     ∅           [skipComments]
    ;A␊;                    ∅           [skipComments]
    ;␊;A                    ∅           [skipComments]
    ;␊;␍␊;                  ∅           [skipComments]

    ### NON RFC CONFORMING DATA ###

    "D"␣                    D␣
    "A,B"␣                  A,B␣
    ␣"D"                    ␣"D"
    ␣"D"␣                   ␣"D"␣
    "D"z                    Dz
    "A,B"z                  A,Bz
    z"D"                    z"D"
    z"A,B"                  z"A↷B"
    z"D"z                   z"D"z

  """.trimIndent()

  class TestData(val lineNo: Int, val line: String, val input: String, val expected: String, flags: String?) {

    val isSkipEmptyLines = flags == "skipEmptyLines"
    val isReadComments = flags == "readComments"
    val isSkipComments = flags == "skipComments"

    override fun toString(): String {
      return TestData::class.simpleName + "[" +
        "lineNo=$lineNo, " +
        "line='$line', " +
        "input='$input', " +
        "expected='$expected', " +
        "skipEmptyLines=$isSkipEmptyLines, " +
        "readComments=$isReadComments, " +
        "skipComments=$isSkipComments" +
        "]"
    }
  }
}