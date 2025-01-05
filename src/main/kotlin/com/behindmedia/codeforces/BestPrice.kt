import java.io.PrintWriter
import java.util.StringTokenizer
import kotlin.math.max

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

private enum class ComparisonResult {
    Less, LessOrEqual, GreaterOrEqual, Greater;
}

private inline fun <T: Comparable<T>> count(size: Int, value: T, comparisonResult: ComparisonResult, range: IntRange = 0 until size, valueForIndex: (Int) -> T): Int {
    val inverse = comparisonResult == ComparisonResult.Less || comparisonResult == ComparisonResult.LessOrEqual
    val index = binarySearch(low = range.first, high = range.last, inverse = inverse) {
        val valueAtIndex = valueForIndex(it)
        when (comparisonResult) {
            ComparisonResult.Less -> valueAtIndex >= value
            ComparisonResult.LessOrEqual -> valueAtIndex > value
            ComparisonResult.GreaterOrEqual -> valueAtIndex < value
            ComparisonResult.Greater -> valueAtIndex <= value
        }
    }
    return if (inverse) {
        index ?: size
    } else {
        if (index == null) size else size - index - 1
    }
}

private fun <T: Comparable<T>> Array<T>.count(value: T, comparisonResult: ComparisonResult, range: IntRange = indices): Int {
    return count(size = this.size, value = value, comparisonResult = comparisonResult, range = range) { this[it] }
}

private fun LongArray.count(value: Long, comparisonResult: ComparisonResult, range: IntRange = indices): Int {
    return count(size = this.size, value = value, comparisonResult = comparisonResult, range = range) { this[it] }
}

private fun IntArray.count(value: Int, comparisonResult: ComparisonResult, range: IntRange = indices): Int {
    return count(size = this.size, value = value, comparisonResult = comparisonResult, range = range) { this[it] }
}

fun main() {
    repeat(readInt()) {
        val (n, k) = readIntList()
        val a = readLongArray(n)
        val b = readLongArray(n)

        a.sort()
        b.sort()

        fun totalEarnings(price: Long): Long? {
            val countBuyPositive = count(a.size, price, ComparisonResult.GreaterOrEqual) { a[it] }
            val countWontBuy = count(b.size, price, ComparisonResult.Less) { b[it] }
            val countBuyNegative = a.size - countBuyPositive - countWontBuy
            return if (countBuyNegative > k) {
                null
            } else {
                (countBuyPositive + countBuyNegative) * price
            }
         }

        // We only need to consider prices in a and/or b, how to calculate the total amount effectively
        var maxEarnings = Long.MIN_VALUE
        for (i in 0 until 2 * n) {
            val price = if (i >= n) {
                b[i - n]
            } else {
                a[i]
            }
            val totalEarnings = totalEarnings(price) ?: continue
            maxEarnings = max(maxEarnings, totalEarnings)
        }
        out.println(maxEarnings)
    }
    out.flush()
}
