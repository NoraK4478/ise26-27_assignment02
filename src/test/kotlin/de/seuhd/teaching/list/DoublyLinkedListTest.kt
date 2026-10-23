package de.seuhd.teaching.list

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeoutPreemptively
import java.time.Duration
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * [assertEquals] compares values; [assertSame] checks whether references point to the same object.
 * [assertContentEquals] compares array elements in order; [assertEquals] would compare array references.
 * [assertConsistent] checks the recorded length and the links in both directions after a list operation.
 */
class DoublyLinkedListTest {
    // JUnit creates a new instance of the test class for each test, so every test starts with an empty list.
    private val list = DoublyLinkedList()

    @Test
    fun testAppendUpdatesLengthAndLinks() {
        list.append(0.9)
        assertEquals(1, list.length)
        val first = assertNotNull(list.begin)
        assertSame(first, list.end)
        assertEquals(0.9, first.value)
        assertNull(first.prev)
        assertNull(first.next)

        list.append(0.5)
        assertEquals(2, list.length)
        val second = assertNotNull(list.end)
        assertSame(first, list.begin)
        assertSame(second, first.next)
        assertSame(first, second.prev)
        assertEquals(0.5, second.value)
        assertNull(second.next)
        assertConsistent(list)

        list.append(0.4)
        assertEquals(3, list.length)
        val third = assertNotNull(list.end)
        assertSame(first, list.begin)
        assertSame(third, second.next)
        assertSame(second, third.prev)
        assertEquals(0.4, third.value)
        assertNull(third.next)
        assertConsistent(list)
    }

    @Test
    fun testAppendCopiesElementValue() {
        list.append(doubleArrayOf(0.9, 0.5, 0.4))
        val element = assertNotNull(assertNotNull(list.begin).next)

        val list2 = DoublyLinkedList()
        list2.append(element)
        assertEquals(1, list2.length)
        val appended = assertNotNull(list2.begin)
        assertEquals(0.5, appended.value)
        assertNull(appended.prev)
        assertNull(appended.next)
        assertConsistent(list2)

        // the source list is unchanged
        assertContentEquals(doubleArrayOf(0.9, 0.5, 0.4), list.toDoubleArray())
        assertConsistent(list)
    }

    @Test
    fun testToDoubleArrayPreservesOrder() {
        assertContentEquals(doubleArrayOf(), list.toDoubleArray())
        list.append(0.9)
        list.append(0.5)
        list.append(0.4)
        assertContentEquals(doubleArrayOf(0.9, 0.5, 0.4), list.toDoubleArray())
    }

    @Test
    fun testClearRemovesAllElements() {
        assertTrue(list.isEmpty())
        list.append(0.9)
        list.append(0.5)
        list.append(0.4)
        assertFalse(list.isEmpty())
        list.clear()
        assertEquals(0, list.length)
        assertTrue(list.isEmpty())
        assertContentEquals(doubleArrayOf(), list.toDoubleArray())
        assertConsistent(list)
    }

    @Test
    fun testAppendPreservesArrayOrder() {
        list.append(doubleArrayOf(0.9, 0.5, 0.4))
        assertContentEquals(doubleArrayOf(0.9, 0.5, 0.4), list.toDoubleArray())
        assertConsistent(list)
    }

    @Test
    fun testAppendCopiesListValues() {
        val otherList = DoublyLinkedList()
        otherList.append(doubleArrayOf(0.9, 0.5, 0.4))
        list.append(otherList)
        assertContentEquals(doubleArrayOf(0.9, 0.5, 0.4), list.toDoubleArray())
        assertContentEquals(doubleArrayOf(0.9, 0.5, 0.4), otherList.toDoubleArray())
        assertConsistent(list)
        assertConsistent(otherList)
    }

    @Test
    fun testAppendDuplicatesValuesWhenAppendingSelf() {
        list.append(doubleArrayOf(0.9, 0.5))
        // If the endless loop comes back, the test usually ends with an OutOfMemoryError that crashes
        // the test executor before the timeout fires. The build still fails in that case.
        assertTimeoutPreemptively(Duration.ofSeconds(1)) { list.append(list) }
        assertContentEquals(doubleArrayOf(0.9, 0.5, 0.9, 0.5), list.toDoubleArray())
        assertConsistent(list)
    }

    @Test
    fun testAppendKeepsCopiedListIndependent() {
        val otherList = DoublyLinkedList()
        otherList.append(doubleArrayOf(0.9, 0.5))
        list.append(otherList)

        // changing the other list afterwards does not change this list
        otherList.append(0.4)
        assertEquals(2, list.length)
        assertContentEquals(doubleArrayOf(0.9, 0.5), list.toDoubleArray())
        assertConsistent(list)
    }

    @Test
    fun testInsertMaintainsSortedOrder() {
        list.append(doubleArrayOf(0.2, 0.4, 0.5, 0.8))
        list.insert(DoublyLinkedList.Element(0.1))
        assertContentEquals(doubleArrayOf(0.1, 0.2, 0.4, 0.5, 0.8), list.toDoubleArray())
        assertConsistent(list)
        list.insert(DoublyLinkedList.Element(0.9))
        assertContentEquals(doubleArrayOf(0.1, 0.2, 0.4, 0.5, 0.8, 0.9), list.toDoubleArray())
        assertConsistent(list)
        list.insert(DoublyLinkedList.Element(0.4))
        assertContentEquals(doubleArrayOf(0.1, 0.2, 0.4, 0.4, 0.5, 0.8, 0.9), list.toDoubleArray())
        assertConsistent(list)
    }

    @Test
    fun testInsertPlacesNaNAfterNumbers() {
        list.append(doubleArrayOf(1.0, 2.0))
        list.insert(Double.NaN)
        assertContentEquals(doubleArrayOf(1.0, 2.0, Double.NaN), list.toDoubleArray())
        assertConsistent(list)

        list.insert(3.0)
        list.insert(Double.NaN)
        assertContentEquals(doubleArrayOf(1.0, 2.0, 3.0, Double.NaN, Double.NaN), list.toDoubleArray())
        assertConsistent(list)
    }

    @Test
    fun testInsertOrdersSpecialDoubleValues() {
        for (value in doubleArrayOf(Double.NaN, 0.0, Double.POSITIVE_INFINITY, -0.0, Double.NEGATIVE_INFINITY)) {
            list.insert(value)
        }
        assertContentEquals(
            doubleArrayOf(Double.NEGATIVE_INFINITY, -0.0, 0.0, Double.POSITIVE_INFINITY, Double.NaN),
            list.toDoubleArray()
        )
        assertConsistent(list)
    }

    @Test
    fun testInsertInitializesEmptyList() {
        list.insert(DoublyLinkedList.Element(0.5))
        assertContentEquals(doubleArrayOf(0.5), list.toDoubleArray())
        val element = assertNotNull(list.begin)
        assertSame(element, list.end)
        assertNull(element.prev)
        assertNull(element.next)
        assertConsistent(list)
    }

    @Test
    fun testInsertCopiesElementValue() {
        val otherList = DoublyLinkedList()
        otherList.append(doubleArrayOf(0.5, 0.1))
        list.append(doubleArrayOf(0.2, 0.4))

        // the inserted element is the end of the other list and has a predecessor there
        list.insert(assertNotNull(otherList.end))
        assertContentEquals(doubleArrayOf(0.1, 0.2, 0.4), list.toDoubleArray())
        assertConsistent(list)
        assertContentEquals(doubleArrayOf(0.5, 0.1), otherList.toDoubleArray())
        assertConsistent(otherList)
    }
}

/**
 * Checks that following `next` from `begin` and `prev` from `end` reaches the same elements in reverse order.
 * Both element counts must equal `list.length`. Each traversal is limited to `list.length + 1` elements
 * to detect cycles without an endless loop.
 */
private fun assertConsistent(list: DoublyLinkedList) {
    val forward = generateSequence(list.begin) { it.next }.take(list.length + 1).toList()
    val backward = generateSequence(list.end) { it.prev }.take(list.length + 1).toList()
    assertEquals(list.length, forward.size)
    assertEquals(forward, backward.reversed())
}
