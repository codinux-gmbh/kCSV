package net.codinux.csv.kcsv.reader

/**
 * Exception to be thrown when malformed csv data is read.
 */
class MalformedCsvException(message: String?) : RuntimeException(message)