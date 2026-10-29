package de.seuhd.teaching.list

/**
 * Main function for demo purposes.
 */
@Suppress("MagicNumber") // detekt normally flags numeric literals; these are sample values for the demo.
fun main() {
    // Create a list and add some values
    val list = DoublyLinkedList()
    list.append(doubleArrayOf(0.5, 4.2, 3.3, 0.9))

    println("The list contains the following elements:")
    list.print()
    println("First element: ${list.get(0)}")
}
