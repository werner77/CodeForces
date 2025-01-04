import java.io.PrintWriter
import java.util.StringTokenizer

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

fun main() {
    val word = readLn()
    val base = 37L  // A prime number as the base for hashing
    val mod = 1L.shl(61) - 1L  // A large prime modulus to prevent overflow
    var startHash = 0L
    var endHash = 0L
    var power = 1L
    val startHashes = LongArray(word.length)
    val endHashes = LongArray(word.length)
    for (i in word.indices) {
        val j = word.length - 1 - i
        // Update the hash for the prefix (startHash)
        startHash = (startHash * base + word[i].code) % mod

        // Update the hash for the suffix (endHash)
        endHash = (endHash + word[j].code * power) % mod

        // Update power for the next character in the suffix
        power = (power * base) % mod

        startHashes[j] = startHash
        endHashes[j] = endHash
    }
    var found = false
    val maxLength = if (word.length % 2 == 0) word.length / 2 else word.length / 2 + 1
    for (k in 1 until maxLength) {
        if (startHashes[k] == endHashes[k]) {
            val foundWord = word.substring(k)
            if (foundWord == word.substring(0, word.length - k)) {
                out.println("YES")
                out.println(word.substring(k))
                found = true
                break
            }
        }
    }
    if (!found) out.println("NO")
    out.flush()
}
