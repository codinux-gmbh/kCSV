package net.codinux.csv

class UncheckedIOException(message: String?, cause: Throwable? = null) : RuntimeException(message, cause) {

  constructor(cause: Throwable) : this(cause.toString(), cause)

}