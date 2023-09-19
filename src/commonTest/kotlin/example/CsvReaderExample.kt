package example

import net.codinux.csv.reader.CommentStrategy
import net.codinux.csv.reader.CsvReader
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class CsvReaderExample {

    @Test
    fun withoutHeader() {
        CsvReader().read("foo1,bar1\nfoo2,bar2").forEach { row ->
            println(row.fields.joinToString())
        }
    }

    @Test
    fun withHeader() {
        CsvReader(hasHeaderRow = true)
            .read("header 1,header 2\nfield 1,field 2")
            .forEach { row -> println(row.getField("header 2")) }
    }

    @Test
    fun customSettings() {
        CsvReader(
            fieldSeparator = ';',
            hasHeaderRow = false,
            quoteCharacter = '\'',
            commentCharacter = '#',
            commentStrategy = CommentStrategy.SKIP,
            skipEmptyRows = true,
            errorOnDifferentFieldCount = false,
            ignoreInvalidQuoteChars = false
        )
            .read("foo1;'bar1'\r\n#foo2,bar2")
            .forEach { row ->
            println(row)
        }
    }

    @Test
    fun fieldMapping() {
        CsvReader(hasHeaderRow = true)
            .read("Int,Double,Boolean,NullableLong\n42,3.14,true,")
            .forEach { row ->
            // of course works also with CsvReader and row indices
            println("Int: ${row.getInt("Int")}")
            println("Double: ${row.getDouble("Double")}")
            println("Boolean: ${row.getBoolean("Boolean")}")
            // all methods also have a 'OrNull()' variant for nullable values
            println("Nullable Long: ${row.getLongOrNull("NullableLong")}")
        }
    }

}