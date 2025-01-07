package com.behindmedia.codeforces.common

/**
 * minLength to target is an optional heuristic to use A* instead of Dijkstra
 */
class Path<N: Any> @JvmOverloads constructor(
    val destination: N,
    val length: Int,
    val parent: Path<N>?,
    val minLengthToTarget: Int = 0
) : Comparable<Path<N>> {
    val nodeCount: Int by lazy { if (parent == null) 1 else parent.nodeCount + 1 }

    override fun compareTo(other: Path<N>): Int {
        return (this.length + minLengthToTarget).compareTo(other.length + other.minLengthToTarget)
    }

    operator fun contains(node: N): Boolean {
        return any { it == node }
    }

    inline fun any(where: (N) -> Boolean): Boolean {
        var current: Path<N>? = this
        while (current != null) {
            if (where.invoke(current.destination)) return true
            current = current.parent
        }
        return false
    }

    inline fun nodes(where: (N) -> Boolean): List<N> {
        val result = ArrayDeque<N>()
        any {
            if (where(it)) result.addFirst(it)
            false
        }
        return result
    }

    val allNodes: List<N>
        get() {
            return nodes { true }
        }
}


/**
 * Breadth first search algorithm to find the shortest paths between unweighted nodes.
 */
inline fun <reified N: Any, T> shortestPath(
    from: N,
    neighbours: (Path<N>) -> Collection<N>,
    reachable: (Path<N>, N) -> Boolean = { _, _ -> true },
    process: (Path<N>) -> T?
): T? {
    val pending = ArrayDeque<Path<N>>()
    val visited = mutableSetOf<N>()
    pending.add(Path(from, 0, null))
    visited.add(from)
    while (true) {
        val current = pending.removeFirstOrNull() ?: return null
        process(current)?.let {
            return it
        }
        for (neighbour in neighbours(current)) {
            if (reachable(current, neighbour) && visited.add(neighbour)) {
                pending.add(Path(neighbour, current.length + 1, current))
            }
        }
    }
}

/**
 * Dijkstra's algorithm to find the shortest path between weighted nodes.
 */
inline fun <N: Any, T> shortestWeightedPath(
    from: N,
    neighbours: (N) -> Collection<Pair<N, Int>>,
    minLengthToTarget: (N) -> Int = { _ -> 0 },
    findAll: Boolean = false,
    process: (Path<N>) -> T?
): T? {
    val pending = SortedQueue<Path<N>>()
    pending.add(Path(from, 0, null))
    val settled = mutableMapOf<N, Int>(from to 0)
    var minLength = Int.MAX_VALUE
    while (pending.isNotEmpty()) {
        val current = pending.removeFirst()
        if (current.length > minLength) {
            break
        }
        val result = process(current)
        if (result != null) {
            minLength = current.length
            if (findAll) {
                continue
            } else {
                return result
            }
        }
        for ((neighbour, neighbourWeight) in neighbours(current.destination)) {
            val newWeight = current.length + neighbourWeight
            val existingWeight = settled[neighbour] ?: Int.MAX_VALUE
            val valid = if (findAll) newWeight <= existingWeight else newWeight < existingWeight
            if (valid) {
                if (newWeight < existingWeight) {
                    settled[neighbour] = newWeight
                }
                pending.add(Path(neighbour, newWeight, current, minLengthToTarget.invoke(neighbour)))
            }
        }
    }
    return null
}
