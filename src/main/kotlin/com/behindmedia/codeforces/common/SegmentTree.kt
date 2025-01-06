package com.behindmedia.codeforces.common

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