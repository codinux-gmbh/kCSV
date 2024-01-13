package net.codinux.csv.utils

object Constants {

    /**
     * Data to read CSV.
     */
    val Data = "Simple field," +
            "\"Example with separator ,\"," +
            "\"Example with delimiter \"\"\"," +
            "\"Example with\nnewline\"," +
            "\"Example with , and \"\" and \nnewline\"" +
            "\n"

    /**
     * Data to write CSV.
     */
    val Row = arrayOf(
        "Simple field",
        "Example with separator ,",
        "Example with delimiter \"",
        "Example with\nnewline",
        "Example with , and \" and \nnewline"
    )

}