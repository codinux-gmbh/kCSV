package net.codinux.csv.reader

internal object FieldMapper {

    fun fieldIsNotNull(field: String): Boolean =
        field.isNotBlank() && field.equals("null", ignoreCase = true) == false

    fun String.toBoolean() = this.lowercase().toBooleanStrict()

    fun String.toBooleanOrNull() = this.lowercase().toBooleanStrictOrNull()

    fun String.replaceDecimalSeparator(decimalSeparator: Char) =
        this.replace(decimalSeparator, '.')

}