# kCSV

[![Maven Central](https://img.shields.io/maven-central/v/net.codinux.csv/kCSV.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.codinux.csv%22%20AND%20a:%22kCSV%22)
[![GitHub license](https://img.shields.io/badge/license-MIT%20License-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)


kCSV is a port of the great [FastCSV](https://github.com/osiegmar/FastCSV) CSV library for Kotlin Multiplatform supporting all KMP targets: Java, iOS, macOS, watchOS, tvOS, Linux, Windows, Node.js and JS Browser (Android *should* work with Android 8.0 (API level 26), but we haven't tested it).

It has all the features FastCSV has e.g.:
- [RFC 4180](https://tools.ietf.org/html/rfc4180) compliant CSV library
- Ultra fast
- Small footprint
- Support for line endings CRLF (Windows), CR (old macOS) and LF (Unix)
- Configurable field separator, quote escaping and comment characters

For all features see [https://github.com/osiegmar/FastCSV#features](https://github.com/osiegmar/FastCSV#features)

## Setup

### Gradle:
```
dependencies {
  implementation "net.codinux.csv:kcsv:1.0.1"
}
```

### Maven:
Maven does not support automatic platform resolution as Gradle does, therefore the specific platform must be specified here:
```
<dependency>
   <groupId>net.codinux.csv</groupId>
   <artifactId>kcsv-jvm</artifactId>
   <version>1.0.1</version>
</dependency>
```


## Usage

### CsvReader

#### CSV data from a string

```kotlin
CsvReader("foo1,bar1\nfoo2,bar2").forEach { row ->
    println(row.fields.joinToString())
}
```

#### CSV data with a header

```kotlin
NamedCsvReader("header 1,header 2\nfield 1,field 2").forEach { row ->
    println(row.getField("header 2"))
}
```

#### Custom settings

```kotlin
CsvReader(
    "foo1;'bar1'\r\n#foo2,bar2",
    fieldSeparator = ';',
    quoteCharacter = '\'',
    commentStrategy = CommentStrategy.SKIP,
    commentCharacter = '#',
    skipEmptyRows = true,
    errorOnDifferentFieldCount = false,
    hasHeaderRow = false,
    ignoreInvalidQuoteChars = false
).forEach { row ->
    println(row)
}
```

#### Field mappings

```kotlin
NamedCsvReader("Int,Double,Boolean,NullableLong,Instant\n42,3.14,true,,2023-06-05T22:19:44.475Z").forEach { row ->
    // of course works also with CsvReader and row indices
    println("Int: ${row.getInt("Int")}")
    println("Double: ${row.getDouble("Double")}")
    println("Boolean: ${row.getBoolean("Boolean")}")
    // all methods also have a 'OrNull()' variant for nullable values
    println("Nullable Long: ${row.getLongOrNull("NullableLong")}")
    // add "org.jetbrains.kotlinx:kotlinx-datetime" dependency to classpath to use this function
    println("Instant: ${row.getInstant("Instant")}")
}
```

#### CSV data from file

Reading from file is currently only available on JVM (use e.g. [kotlinx-io](https://github.com/Kotlin/kotlinx-io) for other platforms to read CSV file to a string):

```kotlin
CsvReader(DataReader.reader(Path("<path_to_csv_file>"))).forEach { row ->
    println(row.fields.joinToString())
}
```

For more example see
[CsvReaderExample.kt](src/commonTest/kotlin/example/CsvReaderExample.kt)

### CsvWriter

#### Write to StringBuilder

```kotlin
val stringBuilder = StringBuilder()

CsvWriter(DataWriter.writer(stringBuilder))
    .writeRow("header1", "header2")
    .writeRow("value1", "value2")

println(stringBuilder.toString())
```

#### Custom settings

```kotlin
val stringBuilder = StringBuilder()
val writer = CsvWriter(
    DataWriter.writer(stringBuilder),
    fieldSeparator = ';',
    quoteCharacter = '\'',
    quoteStrategy = QuoteStrategy.REQUIRED,
    commentCharacter = '#',
    lineDelimiter = LineDelimiter.CRLF
)

writer
    .writeRow("header1", "header2")
    .writeRow("value;", "value2")

println(stringBuilder.toString())
```

#### Write to a file

Writing to file is currently only available on JVM (use e.g. [kotlinx-io](https://github.com/Kotlin/kotlinx-io) for other platforms):

```kotlin
CsvWriter(DataWriter.writer(Path("<path_to_csv_file>")))
    .writeRow("header1", "header2")
    .writeRow("value1", "value2")
```

For more example see
[CsvWriterExample.kt](src/commonTest/kotlin/example/CsvWriterExample.kt)