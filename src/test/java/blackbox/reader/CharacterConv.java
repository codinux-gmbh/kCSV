package blackbox.reader

internal object CharacterConv {
  private val STANDARD = arrayOf(" ", "\r", "\n")
  private val CONV = arrayOf("␣", "␍", "␊")
  private const val FIELD_SEPARATOR = '↷'
  private const val LINE_SEPARATOR = '⏎'
  private const val EMPTY_STRING = "◯"
  private const val EMPTY_LIST = "∅"
  fun print(data: List<List<String?>>): String {
    if (data.isEmpty()) {
      return EMPTY_LIST
    }
    val sb = StringBuilder()
    val iter = data.iterator()
    while (iter.hasNext()) {
      val datum = iter.next()
      val iterator = datum.iterator()
      while (iterator.hasNext()) {
        sb.append(print(iterator.next()))
        if (iterator.hasNext()) {
          sb.append(FIELD_SEPARATOR)
        }
      }
      if (iter.hasNext()) {
        sb.append(LINE_SEPARATOR)
      }
    }
    return sb.toString()
  }

  fun print(str: String?): String {
    return if (str!!.isEmpty()) EMPTY_STRING else replaceEach(str, STANDARD, CONV)!!
  }

  private fun replaceEach(
    text: String?, searchList: Array<String>,
    replacementList: Array<String>
  ): String? {
    var ret = text
    for (i in searchList.indices) {
      ret = ret!!.replace(searchList[i], replacementList[i])
    }
    return ret
  }

  fun parse(str: String?): String? {
    return replaceEach(str, CONV, STANDARD)
  }
}