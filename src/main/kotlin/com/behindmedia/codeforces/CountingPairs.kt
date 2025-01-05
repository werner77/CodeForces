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

private fun binarySearch(low: Int, high: Int, inverse: Boolean, eval: (Int) -> Boolean): Int? {
    var l = low
    var h = high
    var best: Int? = null
    while (l <= h) {
        val mid = (l + h) / 2
        if (eval(mid)) {
            best = mid
            if (inverse) {
                h = mid - 1
            } else {
                l = mid + 1
            }
        } else {
            if (inverse) {
                l = mid + 1
            } else {
                h = mid - 1
            }
        }
    }
    return best
}

fun main() {
    repeat(readInt()) {
        val (nLong, x, y) = readLongList()
        val n = nLong.toInt()
        val a = readLongArray(n = n)
        a.sort()
        val totalSum = a.sum()
        var ans = 0L
        for (i in 0 until n) {
            // val remaining = totalSum - a[i] - a[j] ->  remaining in x..y

            // -a[j] in (x - totalSum + a[i])..(y - totalSum + a[i])
            // a[j] in (totalSum - a[i] - y)..(totalSum - a[i] - x)

            val lower = binarySearch(0, a.size - 1, true) {
                a[it] >= totalSum - a[i] - y
            }
            val upper = binarySearch(0, a.size - 1, false) {
                a[it] <= totalSum - a[i] - x
            }
            if (lower != null && upper != null) {
                if (i in lower..upper) {
                    ans += (upper - lower)
                } else {
                    ans += (upper - lower + 1)
                }
            }
        }
        out.println(ans / 2)
    }
    out.flush()
}
