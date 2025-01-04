#if (${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end

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

fun main() {
    repeat(readInt()) {
        // TODO: Implement
    }
    out.flush()
}
