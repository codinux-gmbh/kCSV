package net.codinux.csv.writer.datawriter

import net.codinux.csv.Closeable

interface DataWriter : Closeable {

  companion object {

    fun writer(): DataWriter = writer(StringBuilder())

    fun writer(builder: StringBuilder): DataWriter = StringBuilderDataWriter(builder)

  }


  fun write(char: Char)

  fun write(charArray: CharArray, offset: Int, length: Int)

  fun write(string: String, offset: Int, length: Int)

  fun flush()

}