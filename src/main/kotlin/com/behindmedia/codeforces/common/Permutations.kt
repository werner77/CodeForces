package com.behindmedia.codeforces.common

enum class PermuteMode {
    Duplicate,
    Unique,
    UniqueSets
}

/**
 * Will generate permutations for the receiver depending on the mode supplied:
 *
 * - Duplicate: generate any permutation where the same element may be present multiple times, e.g. take k elements from n but after each draw put the drawn element back before performing the next draw. This is n.pow(k) iterations.
 * - Unique: generate any permutation where the same element cannot be present multiple times, e.g. take k elements from n and don't put back the drawn element before performing the next draw. This is (n over k) iterations.
 * - UniqueSets: generate any permutation where the same set of elements cannot be present multiple times. This is (n over k) / k! iterations.
 */
fun <T : Any, R : Any> Collection<T>.permute(
    count: Int = this.size,
    mode: PermuteMode = PermuteMode.Unique,
    perform: (List<T>) -> R?
): R? {
    // Use an ArrayDeque to avoid having to reallocate a new collection each time
    // We cycle through the values remaining with removeFirst and addLast.
    fun <T> permute(list: MutableList<T>, valuesLeft: ArrayDeque<T>, perform: (List<T>) -> R?): R? {
        if (list.size == count) {
            return perform(list)
        }
        val iterationsCount =
            if (mode == PermuteMode.UniqueSets) (valuesLeft.size + list.size - count + 1) else valuesLeft.size
        val restoreList: MutableList<T>? = if (mode == PermuteMode.UniqueSets) ArrayList(iterationsCount) else null
        try {
            for (i in 0 until iterationsCount) {
                val value = if (mode == PermuteMode.Duplicate) {
                    valuesLeft[i]
                } else {
                    valuesLeft.removeFirst()
                }
                restoreList?.add(value)
                try {
                    list.add(value)
                    return permute(list, valuesLeft, perform) ?: continue
                } finally {
                    list.removeLast()
                    if (mode == PermuteMode.Unique) {
                        valuesLeft.addLast(value)
                    }
                }
            }
        } finally {
            if (restoreList != null) {
                for (i in restoreList.size - 1 downTo 0) {
                    valuesLeft.addFirst(restoreList[i])
                }
            }
        }
        return null
    }
    return permute(ArrayList(this.size), ArrayDeque(this), perform)
}

/**
 * Permutates all possible combinations in a square matrix of the specified count, using values in the specified range.
 *
 * It performs the specified closure for each permutation. If the closure returns a non-null value, the function immediately returns.
 */
fun <T> permute(count: Int, range: IntRange, perform: (IntArray) -> T?): T? {
    fun <T> permute(list: IntArray, index: Int, range: IntRange, perform: (IntArray) -> T?): T? {
        if (index >= list.size) {
            return perform(list)
        }

        for (value in range.first..range.last) {
            list[index] = value
            val ret = permute(list, index + 1, range, perform)
            if (ret != null) {
                return ret
            }
        }
        return null
    }

    val list = IntArray(count) { 0 }
    return permute(list, 0, range, perform)
}

fun <T> permute(
    ranges: List<IntRange>,
    perform: (IntArray) -> T?
): T? {
    fun permute(
        ranges: List<IntRange>,
        dimension: Int,
        values: IntArray,
        perform: (IntArray) -> T?
    ): T? {
        if (dimension == values.size) {
            return perform(values)
        }
        for (i in ranges[dimension].first..ranges[dimension].last) {
            values[dimension] = i
            permute(ranges, dimension + 1, values, perform)?.let {
                if (it != Unit) return it
            }
        }
        return null
    }
    return permute(ranges, 0, IntArray(ranges.size), perform)
}

inline fun <T : Any, R : Any> List<T>.processPairs(unique: Boolean = true, block: (T, T) -> R?): R? {
    for (i in 0 until this.size) {
        val startIndex = if (unique) i + 1 else 0
        for (j in startIndex until this.size) {
            if (i == j) continue
            return block.invoke(this[i], this[j]) ?: continue
        }
    }
    return null
}

inline fun <T : Any, R : Any> List<T>.processPairsIndexed(
    unique: Boolean = true,
    block: (IndexedValue<T>, IndexedValue<T>) -> R?
): R? {
    for (i in 0 until this.size) {
        val startIndex = if (unique) i + 1 else 0
        for (j in startIndex until this.size) {
            if (i == j) continue
            return block.invoke(IndexedValue(i, this[i]), IndexedValue(j, this[j])) ?: continue
        }
    }
    return null
}
