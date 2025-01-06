import java.io.PrintWriter
import java.util.StringTokenizer
import kotlin.math.*

private val DEBUG = System.getProperty("ONLINE_JUDGE") == null
private val INPUT = System.`in`
private val OUTPUT = System.out
private val ERROR = System.err

private val bufferedReader = INPUT.bufferedReader()
private val out = PrintWriter(OUTPUT, false)
private val err = PrintWriter(ERROR, true)
private fun readLn() = bufferedReader.readLine()!!

private fun readList() = readLn().split(' ')
private var tokenizer = StringTokenizer("")
private fun read(): String {
    while (tokenizer.hasMoreTokens().not()) tokenizer = StringTokenizer(readLn(), " ")
    return tokenizer.nextToken()
}

private fun readInt() = read().toInt()
private fun readLong() = read().toLong()
private fun readDouble() = read().toDouble()

private fun readIntList() = readList().map { it.toInt() }
private fun readLongList() = readList().map { it.toLong() }
private fun readDoubleList() = readList().map { it.toDouble() }

private inline fun debug(string: () -> Any) {
    if (DEBUG) {
        err.println(string.invoke())
    }
}

private fun readIntArray(n: Int = 0) =
    if (n == 0) readList().run { IntArray(size) { get(it).toInt() } } else IntArray(n) { readInt() }

private fun readLongArray(n: Int = 0) =
    if (n == 0) readList().run { LongArray(size) { get(it).toLong() } } else LongArray(n) { readLong() }

private fun readDoubleArray(n: Int = 0) =
    if (n == 0) readList().run { DoubleArray(size) { get(it).toDouble() } } else DoubleArray(n) { readDouble() }

private infix fun Int.over(k: Int): Long {
    val n = this
    require(k in 1..n)
    var result = 1L
    for (i in n - k + 1..n) {
        result *= i
    }
    return result
}

private val Int.faculty: Long
    get() {
        var result = 1L
        for (i in 1..this) {
            result *= i
        }
        return result
    }

private val Int.digits: List<Int>
    get() {
        if (this == 0) {
            return listOf(0)
        }
        val result = ArrayDeque<Int>(10)
        var value = abs(this)
        while (value != 0) {
            result.addFirst(value % 10)
            value /= 10
        }
        return result
    }

private val Long.digits: List<Int>
    get() {
        if (this == 0L) {
            return listOf(0)
        }
        val result = ArrayDeque<Int>(20)
        var value = abs(this)
        while (value != 0L) {
            result.addFirst((value % 10L).toInt())
            value /= 10L
        }
        return result
    }

private fun Double.isAlmostEqual(other: Double, allowedDifference: Double = 0.000001): Boolean {
    return abs(this - other) < allowedDifference
}

private val Long.numberOfDigits: Int
    get() {
        val value = abs(this)
        return when {
            value < 10L -> 1
            value < 100L -> 2
            value < 1000L -> 3
            value < 10_000L -> 4
            value < 100_000L -> 5
            value < 1_000_000L -> 6
            value < 10_000_000L -> 7
            value < 100_000_000L -> 8
            value < 1_000_000_000L -> 9
            value < 10_000_000_000L -> 10
            value < 100_000_000_000L -> 11
            value < 1_000_000_000_000L -> 12
            value < 10_000_000_000_000L -> 13
            value < 100_000_000_000_000L -> 14
            value < 1_000_000_000_000_000L -> 15
            value < 10_000_000_000_000_000L -> 16
            value < 100_000_000_000_000_000L -> 17
            value < 1_000_000_000_000_000_000L -> 18
            else -> 19
        }
    }

/**
 * Finds the greatest or smallest (if inverse == true) index for which the specified predicate returns true.
 */
private inline fun binarySearch(low: Int, high: Int, inverse: Boolean = false, predicate: (Int) -> Boolean): Int? {
    var l = low
    var h = high
    var best: Int? = null
    while (l <= h) {
        val mid = (l + h) / 2
        if (predicate(mid)) {
            // Predicate is true
            best = mid
            if (inverse) {
                // decrease high to find a lesser index for which the predicate is true
                h = mid - 1
            } else {
                // increase low to find a greater index for which the predicate is true
                l = mid + 1
            }
        } else {
            if (inverse) {
                // We are too low: increase low
                l = mid + 1
            } else {
                // We are too high: decrease high
                h = mid - 1
            }
        }
    }
    return best
}

private const val Less = 1
private const val LessOrEqual = 2
private const val GreaterOrEqual = 3
private const val Greater = 4

private inline fun <T : Comparable<T>> count(
    size: Int,
    value: T,
    comparisonResult: Int,
    range: IntRange = 0 until size,
    valueForIndex: (Int) -> T
): Int {
    val inverse = comparisonResult == Less || comparisonResult == LessOrEqual
    val index = binarySearch(low = range.first, high = range.last, inverse = inverse) {
        val valueAtIndex = valueForIndex(it)
        when (comparisonResult) {
            Less -> valueAtIndex >= value
            LessOrEqual -> valueAtIndex > value
            GreaterOrEqual -> valueAtIndex < value
            Greater -> valueAtIndex <= value
            else -> error("Invalid comparison result")
        }
    }
    return if (inverse) {
        index ?: size
    } else {
        if (index == null) size else size - index - 1
    }
}

private fun <T : Comparable<T>> Array<T>.count(
    value: T,
    comparisonResult: Int,
    range: IntRange = indices
): Int {
    return count(size = this.size, value = value, comparisonResult = comparisonResult, range = range) { this[it] }
}

private fun LongArray.count(value: Long, comparisonResult: Int, range: IntRange = indices): Int {
    return count(size = this.size, value = value, comparisonResult = comparisonResult, range = range) { this[it] }
}

private fun IntArray.count(value: Int, comparisonResult: Int, range: IntRange = indices): Int {
    return count(size = this.size, value = value, comparisonResult = comparisonResult, range = range) { this[it] }
}

private fun sum(list: List<Int>): Int {
    var sum = 0
    for (i in list.indices) {
        for (j in i +1 until list.size) {
            val first = list[i]
            val second = list[j]
            sum += (first xor second)
        }
    }
    return sum
}

private fun solve(low: Int, high: Int): List<Int> {
    val leading = (low xor high).countLeadingZeroBits()
    val mostSignificantBit = 31 - leading
    val a = low or ((1 shl mostSignificantBit) - 1)
    val b = a + 1
    val c = if (a == low) high else low
    return listOf(a, b, c)
}

fun main() {
    repeat(readInt()) {
        val (l, r) = readIntList()
        val values = solve(l, r)
        debug { sum(values) }
        out.println(values.joinToString(" "))
    }
    out.flush()
}
