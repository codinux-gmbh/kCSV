package test

import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun <T> assertElementsEqual(list1: List<T>, list2: List<T>) {
    assertEquals(list1.size, list2.size, "List 1 (${list1.size}) should have the same size as List 2 (${list2.size})")

    list1.forEachIndexed { index, firstElement ->
        val secondElement = list2[index]
        assertEquals(firstElement, secondElement, "At index $index element of first List ($firstElement) does not equal element of second List ($secondElement)")
    }
}

fun <T> assertElementsEqual(set1: Set<T>, set2: Set<T>) {
    assertEquals(set1.size, set2.size, "Set 1 (${set1.size}) should have the same size as Set 2 (${set2.size})")

    set1.forEachIndexed { index, firstElement ->
        assertTrue(set2.contains(firstElement), "At index $index first Set contains element '$firstElement that second Set does not contain")
    }
}

fun <T> assertElementsEqual(array1: Array<out T>, array2: Array<out T>) {
    assertEquals(array1.size, array2.size, "Array 1 (${array1.size}) should have the same size as Array 2 (${array2.size})")

    array1.forEachIndexed { index, firstElement ->
        val secondElement = array2[index]
        assertEquals(firstElement, secondElement, "At index $index element of first Array ($firstElement) does not equal element of second Array ($secondElement)")
    }
}