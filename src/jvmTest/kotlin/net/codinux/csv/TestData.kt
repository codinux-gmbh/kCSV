package net.codinux.csv

import java.io.File
import java.util.zip.ZipInputStream
import kotlin.io.path.inputStream

object TestData {

    val LargeCsvFileWithInvalidQuoteCharsPath by lazy {
        File(ClassLoader.getSystemResource("ZhvAllStationsResponse_2022-11-11.zip").toURI()).toPath()
    }

    fun largeCsvFileWithInvalidQuoteCharsZipInputStream() =
        ZipInputStream(LargeCsvFileWithInvalidQuoteCharsPath.inputStream()).apply {
            var zipEntry = this.nextEntry
            while (zipEntry != null && zipEntry.name.endsWith(".csv") == false) {
                zipEntry = this.nextEntry
            }
        }

}