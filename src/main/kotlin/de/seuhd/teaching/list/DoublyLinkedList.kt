package de.seuhd.teaching.list

/**
 * A doubly linked list of [Double] values.
 */
class DoublyLinkedList {
    var begin: Element? = null
        private set
    var end: Element? = null
        private set
    var length: Int = 0
        private set

    /**
     * Appends the value of [e] to the end of this list.
     * [e] itself is not linked into this list, so it may belong to another list.
     */
    fun append(e: Element) = append(e.value)

    /**
     * Appends a new element with the given [value] to the end of this list.
     */
    fun append(value: Double) {
        val e = Element(value)
        val oldEnd = end
        if (oldEnd == null) {
            begin = e
        } else {
            oldEnd.next = e
            e.prev = oldEnd
        }
        end = e
        length++
    }

    /**
     * Appends the values of [list] to the end of this list.
     * The values are copied first, so [list] is not changed and may be this list itself.
     */
    fun append(list: DoublyLinkedList) = append(list.toDoubleArray())

    /**
     * Appends one new element per entry of [values] to the end of this list, in the same order.
     */
    fun append(values: DoubleArray) {
        for (value in values) {
            append(value)
        }
    }

    /**
     * Returns a new array with the values of this list, in list order.
     */
    fun toDoubleArray(): DoubleArray {
        val array = DoubleArray(length)
        var element = begin
        var arrayPos = 0
        while (element != null) {
            array[arrayPos] = element.value
            arrayPos++
            element = element.next
        }
        return array
    }

    /**
     * Prints the values of this list to the standard output, one value per line, or "Empty." for an empty list.
     */
    fun print() {
        if (isEmpty()) {
            println("Empty.")
        } else {
            var pos = begin
            while (pos != null) {
                println(pos.value)
                pos = pos.next
            }
        }
    }

    /**
     * Removes all elements from this list.
     */
    fun clear() {
        begin = null
        end = null
        length = 0
    }

    fun isEmpty(): Boolean {
        return begin == null && end == null && length == 0
    }

    /**
     * Returns the value at position [index].
     */
    fun get(index: Int): Double {
        if (index < 0 || index >= length) throw IndexOutOfBoundsException("Index: $index, Length: $length")
        var element = begin
        repeat(index) { element = element!!.next }
        return element!!.value
    }

    /**
     * Inserts the value of [e] into this list, which must be sorted by [Double.compareTo].
     * NaN values sort last.
     * [e] itself is not linked into this list, so it may belong to another list.
     */
    fun insert(e: Element) = insert(e.value)

    /**
     * Inserts a new element with the given [value] into this list, which must be sorted by [Double.compareTo].
     * NaN values sort last.
     * The new element goes before the first element that compares greater than or equal to [value].
     */
    fun insert(value: Double) {
        // Find position pos, before which the new element is supposed to be located
        var pos = begin
        while (pos != null && pos.value.compareTo(value) < 0) {
            pos = pos.next
        }
        if (pos == null) { // No element is greater than or equal to value => append new element to the list
            append(value)
            return
        }
        val e = Element(value)
        val pred = pos.prev
        e.prev = pred
        e.next = pos
        pos.prev = e
        if (pred == null) { // pos is the first element in the list
            begin = e
        } else {
            pred.next = e
        }
        length++
    }

    /**
     * An element of a [DoublyLinkedList], holding one [value] and links to its neighbors.
     */
    class Element(
        val value: Double
    ) {
        var next: Element? = null
            internal set
        var prev: Element? = null
            internal set
    }
}
