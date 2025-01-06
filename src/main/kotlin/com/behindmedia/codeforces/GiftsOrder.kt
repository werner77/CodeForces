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

private class GiftsOrder {
    private interface SegmentNode<N : SegmentNode<N, V>, V : Any> {
        operator fun plus(other: N): N
        fun update(with: V)
    }

    private class SegmentTree<N : SegmentNode<N, V>, V : Any>(val size: Int, dataLocator: (Int) -> V, nodeConstructor: () -> N) {

        private val info: MutableList<N>

        private fun log2(value: Int): Int {
            require(value > 0)
            return 31 - value.countLeadingZeroBits()
        }

        init {
            val infoSize = if (size <= 1) 2 else 2 * (1 shl (log2(size - 1) + 1))
            info = MutableList<N>(infoSize) {
                nodeConstructor()
            }
            fun build(v: Int, l: Int, r: Int) {
                if (l == r) {
                    info[v].update(with = dataLocator(l))
                    return
                }
                val m = (l + r) / 2
                build(v + v, l, m)
                build(v + v + 1, m + 1, r)
                info[v] = info[v + v] + info[v + v + 1]
            }
            build(1, 0, size - 1)
        }

        private fun rangeQuery(v: Int, l: Int, r: Int, tl: Int, tr: Int): N? {
            if (r < tl || l > tr) {
                return null
            }
            if (l >= tl && r <= tr) {
                return info[v]
            }
            val m = (l + r) / 2
            val left = rangeQuery(v + v, l, m, tl, tr)
            val right = rangeQuery(v + v + 1, m + 1, r, tl, tr)
            return if (left == null) {
                right
            } else if (right == null) {
                left
            } else {
                left + right
            }
        }

        fun query(range: IntRange): N? {
            return rangeQuery(1, 0, size - 1, range.first, range.last)
        }

        private fun modify(v: Int, l: Int, r: Int, i: Int, x: V) {
            if (l == r) {
                info[v].update(x)
                return
            }
            val m = (l + r) / 2
            if (i <= m) {
                modify(v + v, l, m, i, x)
            } else {
                modify(v + v + 1, m + 1, r, i, x)
            }
            info[v] = info[v + v] + info[v + v + 1]
        }

        fun modify(index: Int, value: V) {
            modify(1, 0, size - 1, index, value);
        }

        private fun query(v: Int, l: Int, r: Int, i: Int): N {
            if (l == r) {
                return info[v]
            }
            val m = (l + r) / 2
            return if (i <= m) {
                query(v + v, l, m, i)
            } else {
                query(v + v + 1, m + 1, r, i)
            }
        }

        fun query(index: Int): N {
            return query(1, 0, size - 1, index)
        }
    }

    private class Node: SegmentNode<Node, Pair<Int, Int>> {
        var min1: Int = Int.MAX_VALUE
        var min2: Int = Int.MAX_VALUE
        var max1: Int = Int.MIN_VALUE
        var max2: Int = Int.MIN_VALUE
        var ans1: Int = 0
        var ans2: Int = 0

        override fun plus(other: Node): Node {
            val a = this
            val b = other
            val res = Node()
            res.min1 = min(a.min1, b.min1)
            res.min2 = min(a.min2, b.min2)
            res.max1 = max(a.max1, b.max1)
            res.max2 = max(a.max2, b.max2)
            res.ans1 = maxOf(a.ans1, b.ans1, b.max1 - a.min1)
            res.ans2 = maxOf(a.ans2, b.ans2, a.max2 - b.min2)
            return res
        }

        override fun update(with: Pair<Int, Int>) {
            min1 = with.first
            min2 = with.second
            max1 = with.first
            max2 = with.second
            ans1 = 0
            ans2 = 0
        }

        val ans: Int
            get() = max(ans1, ans2)

        companion object {
            operator fun invoke(): Node = Node()
        }
    }

    fun solve() {
        val (n, q) = readIntList()
        val a = readIntArray(n)
        fun getData(index: Int): Pair<Int, Int> = a[index] - index to a[index] - (n - 1 - index)
        val tree = SegmentTree<Node, Pair<Int, Int>>(a.size, ::getData, Node::invoke)
        out.println(tree.query(0 until tree.size)!!.ans)
        repeat(q) {
            val (p, x) = readIntList()
            val i = p - 1
            tree.modify(i, x - i to x - (n - 1 - i))
            out.println(tree.query(0 until tree.size)!!.ans)
        }
    }
}

fun main() {
    repeat(readInt()) {
        GiftsOrder().solve()
    }
    out.flush()
}
