package net.codinux.csv.kcsv.writer.datawriter

interface DataWriter {

  companion object {

    fun writer(builder: StringBuilder): DataWriter = StringBuilderDataWriter(builder)

  }


  fun write(char: Char)

  fun write(charArray: CharArray, offset: Int, length: Int)

  fun write(string: String, offset: Int, length: Int)

  fun flush()

  fun close()

}