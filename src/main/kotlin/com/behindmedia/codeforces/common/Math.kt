package com.behindmedia.codeforces.common

import kotlin.math.abs

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
