package net.codinux.csv.benchmark.utils

import java.nio.file.Files
import java.util.zip.ZipInputStream
import kotlin.io.path.Path
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

object TestData {

    val LargeCsvFileZippedPath by lazy {
        Path("src/jmh/resources/data/ZhvAllStationsResponse_2022-11-11_cleaned.zip")
    }

    val extractedLargeCsvFile by lazy {
        val tempFile = Files.createTempFile("ZhvAllStationsResponse_2022-11-11_cleaned", ".csv")

        tempFile.outputStream().use { outputStream ->
            largeCsvFileZipInputStream().use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        tempFile
    }

    fun largeCsvFileZipInputStream() =
        ZipInputStream(LargeCsvFileZippedPath.inputStream()).apply {
            var zipEntry = this.nextEntry
            while (zipEntry != null && zipEntry.name.endsWith(".csv") == false) {
                zipEntry = this.nextEntry
            }
        }

}