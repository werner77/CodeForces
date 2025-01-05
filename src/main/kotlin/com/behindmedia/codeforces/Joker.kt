import java.io.PrintWriter
import java.util.StringTokenizer
import kotlin.math.max
import kotlin.math.min

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

private fun count(ranges: Array<IntRange?>): Int {
    return ranges.sumOf { if (it == null || it.isEmpty()) 0 else it.last - it.first + 1 }.also {
        debug { "Count: $it" }
    }
}

private const val INF = Int.MAX_VALUE - 10

private fun solve(a: IntArray, n: Int, m: Int, forEach: (Int, Int) -> Unit) {
    val ranges = Array<IntRange?>(3) {
        if (it == 0) {
            null
        } else if (it == 1) {
            m..m
        } else {
            null
        }
    }

    fun queue(step: Int, range: IntRange) {
        var (lower, middle, upper) = ranges

        if (range.contains(1)) {
            // lower
            lower = if (lower == null) {
                range
            } else {
                1..max(lower.last, range.last)
            }
        }
        if (range.contains(n)) {
            upper = if (upper == null) {
                range
            } else {
                min(upper.first, range.first)..n
            }
        }
        if (1 !in range && n !in range) {
            middle = range
        }

        // Ensure the ranges don't overlap
        if (lower != null) {
            lower = 1..minOf(lower.last, (middle?.first ?: INF) - 1,  (upper?.first ?: INF) - 1)
            if (lower.isEmpty()) {
                lower = null
            }
        }
        if (middle != null) {
            middle = max((lower?.last ?: -INF) + 1, middle.first)..min(middle.last, (upper?.first ?: INF) - 1)
            if (middle.isEmpty()) {
                middle = null
            }
        }
        if (upper != null) {
            upper = maxOf((lower?.last ?: -INF) + 1, (middle?.last ?: -INF) + 1, upper.first)..n
            if (upper.isEmpty()) {
                upper = null
            }
        }

        ranges[0] = lower
        ranges[1] = middle
        ranges[2] = upper

        debug { "Step: $step: set ranges to: {$lower, $middle, $upper}" }
    }

    for (index in 0 until a.size) {
        val move = a[index]
        for (range in ranges.toList()) {
            if (range == null || range.isEmpty()) continue
            if (move < range.first) {
                queue(index, range.first - 1..range.last)
            } else if (move > range.last) {
                queue(index, range.first..range.last + 1)
            } else {
                // Move in range
                if (1 !in range || range.first == range.last) {
                    queue(index, 1..1)
                }
                if (range.first != range.last) {
                    queue(index, range)
                } else if (1 !in range && n !in range){
                    // Remove this range
                    ranges[1] = null
                }
                if (n !in range || range.first == range.last) {
                    queue(index, n..n)
                }
            }
        }
        forEach.invoke(index, count(ranges))
    }
}

fun main() {
    try {
        repeat(readInt()) {
            val (n, m, q) = readIntList()
            val a = readIntArray(q)

            solve(a, n, m) { i, v ->
                if (i > 0) out.print(' ')
                out.print(v)
            }
            out.println()
        }
        out.flush()
    } catch (e: Exception) {
        debug { "Caught exception: $e" }
        throw e
    }
}
