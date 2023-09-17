package net.codinux.csv.reader

internal object FieldMapper {

    fun fieldIsNotNull(field: String): Boolean =
        field.isNotBlank() && field.equals("null", ignoreCase = true) == false

    fun String.mapToBoolean() = this.lowercase().toBooleanStrict()

    fun String.mapToInt() = this.toInt()

    fun String.mapToLong() = this.toLong()

    fun String.mapToFloat() = this.toFloat()

    fun String.mapToDouble() = this.toDouble()

    fun String.replaceDecimalSeparatorAndMapToDouble(decimalSeparator: Char) =
        this.replace(decimalSeparator, '.').toDouble()

}