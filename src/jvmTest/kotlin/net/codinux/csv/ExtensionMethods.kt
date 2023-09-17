package net.codinux.csv

import java.io.Reader

fun String.countOccurrencesOf(toFind: String): Int {
    var countOccurrences = 0

    var currentFindIndex = this.indexOf(toFind)
    while (currentFindIndex > -1) {
        countOccurrences++
        currentFindIndex = this.indexOf(toFind, currentFindIndex + 1)
    }

    return countOccurrences
}

fun String.indexOfOrNull(char: Char, startIndex: Int = 0, ignoreCase: Boolean = false): Int? =
    this.indexOf(char, startIndex, ignoreCase).takeUnless { it == -1 }

fun <E> Collection<E>.containsNot(element: E) = this.contains(element) == false

fun Reader.forEachLineIndexed(action: (Int, String) -> Unit) {
    var index = 0
    this.forEachLine { line -> action(index++, line) }
}