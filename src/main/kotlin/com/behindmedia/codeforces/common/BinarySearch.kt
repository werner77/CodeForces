package com.behindmedia.codeforces.common

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
    Less, LessOrEqual, GreaterOrEqual, Greater
}

private inline fun <T : Comparable<T>> count(
    size: Int,
    value: T,
    comparisonResult: ComparisonResult,
    range: IntRange = 0 until size,
    valueForIndex: (Int) -> T
): Int {
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

private fun <T : Comparable<T>> Array<T>.count(
    value: T,
    comparisonResult: ComparisonResult,
    range: IntRange = indices
): Int {
    return count(size = this.size, value = value, comparisonResult = comparisonResult, range = range) { this[it] }
}

private fun LongArray.count(value: Long, comparisonResult: ComparisonResult, range: IntRange = indices): Int {
    return count(size = this.size, value = value, comparisonResult = comparisonResult, range = range) { this[it] }
}

private fun IntArray.count(value: Int, comparisonResult: ComparisonResult, range: IntRange = indices): Int {
    return count(size = this.size, value = value, comparisonResult = comparisonResult, range = range) { this[it] }
}