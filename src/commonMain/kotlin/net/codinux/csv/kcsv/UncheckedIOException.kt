package net.codinux.csv.kcsv

class UncheckedIOException(message: String?, cause: Throwable? = null) : RuntimeException(message, cause) {

  constructor(cause: Throwable) : this(cause.toString(), cause)

}