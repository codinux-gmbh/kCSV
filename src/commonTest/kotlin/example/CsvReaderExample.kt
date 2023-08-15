package example

import net.codinux.csv.reader.CommentStrategy
import net.codinux.csv.reader.CsvReader
import net.codinux.csv.reader.NamedCsvReader
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
        NamedCsvReader().read("header 1,header 2\nfield 1,field 2").forEach { row ->
            println(row.getField("header 2"))
        }
    }

    @Test
    fun customSettings() {
        CsvReader(
            fieldSeparator = ';',
            quoteCharacter = '\'',
            commentStrategy = CommentStrategy.SKIP,
            commentCharacter = '#',
            skipEmptyRows = true,
            errorOnDifferentFieldCount = false,
            hasHeaderRow = false,
            ignoreInvalidQuoteChars = false
        )
            .read("foo1;'bar1'\r\n#foo2,bar2")
            .forEach { row ->
            println(row)
        }
    }

    @Test
    fun fieldMapping() {
        NamedCsvReader()
            .read("Int,Double,Boolean,NullableLong,Instant,LocalDateTime\n42,3.14,true,,2023-06-05T22:19:44.475Z,2023-06-07T08:47:23")
            .forEach { row ->
            // of course works also with CsvReader and row indices
            println("Int: ${row.getInt("Int")}")
            println("Double: ${row.getDouble("Double")}")
            println("Boolean: ${row.getBoolean("Boolean")}")
            // all methods also have a 'OrNull()' variant for nullable values
            println("Nullable Long: ${row.getLongOrNull("NullableLong")}")
            // add "org.jetbrains.kotlinx:kotlinx-datetime" dependency to classpath to use this function
            println("Instant: ${row.getInstant("Instant")}")
            println("LocalDateTime: ${row.getLocalDateTime("LocalDateTime")}")
        }
    }

}