# kCSV

[![Maven Central](https://img.shields.io/maven-central/v/net.codinux.csv/kcsv.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.codinux.csv%22%20AND%20a:%22kcsv%22)
[![GitHub license](https://img.shields.io/badge/license-MIT%20License-blue.svg?style=flat)](https://mit-license.org/)


kCSV is a port of the great [FastCSV](https://github.com/osiegmar/FastCSV) CSV library for Kotlin Multiplatform supporting all KMP targets: Java, iOS, macOS, watchOS, tvOS, Linux, Windows, Node.js and JS Browser (Android *should* work with Android 8.0 (API level 26), but we haven't tested it).

It has all the features FastCSV has e.g.:
- [RFC 4180](https://tools.ietf.org/html/rfc4180) compliant CSV library
- Ultra fast
- Small footprint
- Support for line endings CRLF (Windows), CR (old macOS) and LF (Unix)
- Configurable field separator, quote escaping and comment characters

For all features see [https://github.com/osiegmar/FastCSV#features](https://github.com/osiegmar/FastCSV#features)

In addition to FastCSV, it also has these features:
- Better API for Kotlin
- Runs on all Kotlin Multiplatform targets
- Smaller memory footprint
- Reuse row instances. Saves a lot of memory for large CSV files (see option `reuseRowInstance`)
- Ignoring columns. Avoids copying column values you don't need from the InputStream, thus saving additional memory (see option `ignoreColumns`)
- Ignore invalid quote chars (see option `ignoreInvalidQuoteChars`)
- Convenience methods to read field values like `row.getBoolean(5)` or `row.getIntOrNull("Age")` 

## Setup

### Gradle:

```
dependencies {
  implementation "net.codinux.csv:kcsv:2.1.1"
}
```

### Maven:

Maven does not support automatic platform resolution as Gradle does, therefore the specific platform must be specified here:

```
<dependency>
   <groupId>net.codinux.csv</groupId>
   <artifactId>kcsv-jvm</artifactId>
   <version>2.1.1</version>
</dependency>
```


## Usage

### CsvReader

#### CSV data from a string

```kotlin
CsvReader().read("foo,1.0\nbar,2.0").forEach { row ->
    println("${row[0]}: ${row.getDouble(1)}")
}
```

#### CSV data with a header

```kotlin
CsvReader(hasHeaderRow = true)
    .read("Name,Age\nMahatma,78")
    .forEach { row -> println("${row["Name"]}${row.getInt("Age")}") }
```

#### Supported settings

CsvReader has these settings, all are stated with their default value:

```kotlin
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
```

#### Field mappings

```kotlin
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
```

#### CSV data from File, Path, InputStream, ...

Reading from File, InputStream, ... is only available on JVM (use e.g. [kotlinx-io](https://github.com/Kotlin/kotlinx-io) for other platforms to read CSV file to a string):

```kotlin
val file = Path("<path_to_csv_file>")

CsvReader()
    .read(file, Charsets.UTF_8) // UTF_8 is the default charset
    .forEach { row -> println(row.fields.joinToString()) }
```

For more example see
[CsvReaderExample.kt](src/commonTest/kotlin/example/CsvReaderExample.kt)

### CsvWriter

#### Write to StringBuilder

```kotlin
val stringBuilder = StringBuilder()

CsvWriter.builder().writer(stringBuilder)
    .writeRow("header1", "header2")
    .writeRow("value1", "value2")

println(stringBuilder.toString())
```

#### Supported settings

```kotlin
val stringBuilder = StringBuilder()

val writer = CsvWriter.builder(
    fieldSeparator = ';',
    quoteCharacter = '\'',
    quoteStrategy = QuoteStrategy.REQUIRED,
    lineDelimiter = LineDelimiter.CRLF,
    commentCharacter = '#'
).writer(stringBuilder)

writer
    .writeRow("header1", "header2")
    .writeRow("value;", "value2") // ';' gets quoted with '\''

println(stringBuilder.toString())
```

#### Write to a file

Writing to File, OutputStream, ... is currently only available on JVM (use e.g. [kotlinx-io](https://github.com/Kotlin/kotlinx-io) for other platforms):

```kotlin
val file = Path("<path_to_csv_file>")

CsvWriter.builder()
    .writer(file, Charsets.UTF_8, StandardOpenOption.CREATE_NEW) // UTF_8 is the default charset
    .writeRow("header1", "header2")
    .writeRow("value1", "value2")
```

For more example see
[CsvWriterExample.kt](src/commonTest/kotlin/example/CsvWriterExample.kt)