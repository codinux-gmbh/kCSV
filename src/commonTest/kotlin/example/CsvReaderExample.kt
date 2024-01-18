package example

import net.codinux.csv.reader.CommentStrategy
import net.codinux.csv.reader.CsvReader
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class CsvReaderExample {

    @Test
    fun withoutHeader() {
        CsvReader().read("foo,1.0\nbar,2.0").forEach { row ->
            println("${row[0]}: ${row.getDouble(1)}")
        }
    }

    @Test
    fun withHeader() {
        CsvReader(hasHeaderRow = true)
            .read("Name,Age\nMahatma,78")
            .forEach { row -> println("${row["Name"]}${row.getInt("Age")}") }
    }

    @Test
    fun settings() {
        CsvReader(
            fieldSeparator = ',',
            hasHeaderRow = false,
            quoteCharacter = '\"',
            commentCharacter = '#',
            commentStrategy = CommentStrategy.NONE,
            skipEmptyRows = true,
            reuseRowInstance = false,
            ignoreColumns = emptySet(),
            errorOnDifferentFieldCount = false,
            ignoreInvalidQuoteChars = false
        )
            .read("""foo1,"bar1"\r\n#foo2,bar2""")
            .forEach { row -> println(row) }
    }

    @Test
    fun fieldMapping() {
        CsvReader(hasHeaderRow = true)
            .read("Int,Double,Boolean,NullableLong\n42,3.14,true,")
            .forEach { row ->
            // of course works also with column indices instead of header names
            println("Int: ${row.getInt("Int")}")
            println("Double: ${row.getDouble("Double")}")
            println("Boolean: ${row.getBoolean("Boolean")}")
            // all methods also have a 'OrNull()' variant for values that might not be set
            println("Nullable Long: ${row.getLongOrNull("NullableLong")}")
        }
    }

}