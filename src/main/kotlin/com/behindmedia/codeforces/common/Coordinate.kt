package com.behindmedia.codeforces.common

import kotlin.math.abs

/**
 * Describes a two-dimensional coordinate or vector.
 */
data class Coordinate(override val x: Int, override val y: Int) : Comparable<Coordinate>, Point2D<Int, Coordinate> {

    companion object {
        val origin = Coordinate(0, 0)
        val up = Coordinate(0, -1)
        val down = Coordinate(0, 1)
        val left = Coordinate(-1, 0)
        val right = Coordinate(1, 0)
        val upLeft = up + left
        val downLeft = down + left
        val upRight = up + right
        val downRight = down + right
        val directNeighbourDirections = arrayOf(up, left, right, down)
        val indirectNeighbourDirections = arrayOf(upLeft, downLeft, upRight, downRight)
        val allNeighbourDirections = directNeighbourDirections + indirectNeighbourDirections
    }

    /**
     * Comparison, ordering from top-left to bottom-right
     */
    override fun compareTo(other: Coordinate): Int {
        return compareValuesBy(this, other, Coordinate::y, Coordinate::x)
    }

    override fun offset(xOffset: Int, yOffset: Int): Coordinate {
        return Coordinate(x + xOffset, y + yOffset)
    }

    /**
     * Returns the diff with the supplied coordinate as a new coordinate (representing the vector)
     */
    override fun vector(to: Coordinate): Coordinate {
        return Coordinate(to.x - this.x, to.y - this.y)
    }

    /**
     * Returns the Manhatten distance to the specified coordinate
     */
    override fun manhattenDistance(to: Coordinate): Int {
        return abs(x - to.x) + abs(y - to.y)
    }

    /**
     * The invers
     */
    override fun inverted(): Coordinate {
        return Coordinate(-x, -y)
    }

    /**
     * Normalizes the coordinate by dividing both x and y by their greatest common divisor
     */
    override fun normalized(): Coordinate {
        val factor = greatestCommonDivisor(abs(this.x.toLong()), abs(this.y.toLong())).toInt()
        return Coordinate(this.x / factor, this.y / factor)
    }

    /**
     * Rotates this coordinate (representing a vector) using the specified rotation direction
     */
    override fun rotate(direction: RotationDirection): Coordinate {
        return when (direction) {
            RotationDirection.Left -> Coordinate(this.y, -this.x)
            RotationDirection.Right -> Coordinate(-this.y, this.x)
        }
    }

    /**
     * Optionally rotates this coordinate (representing a vector). Does nothing if the supplied direction is null.
     */
    override fun optionalRotate(direction: RotationDirection?): Coordinate {
        return when (direction) {
            null -> this
            else -> rotate(direction)
        }
    }

    override fun directNeighbourSequence(): Sequence<Coordinate> {
        return sequence {
            repeat(4) {
                yield(this@Coordinate + directNeighbourDirections[it])
            }
        }
    }

    override fun indirectNeighbourSequence(): Sequence<Coordinate> {
        return sequence {
            repeat(4) {
                yield(this@Coordinate + indirectNeighbourDirections[it])
            }
        }
    }

    override fun allNeighbourSequence(): Sequence<Coordinate> {
        return sequence {
            repeat(8) {
                yield(this@Coordinate + allNeighbourDirections[it])
            }
        }
    }

    /**
     * Returns the direct neighbours of this coordinate
     */
    override val directNeighbours: List<Coordinate>
        get() {
            return List(4) {
                this + directNeighbourDirections[it]
            }
        }

    /**
     * Returns the indirect neighbours of this coordinate, defined as the diagonal neighbours
     */
    override val indirectNeighbours: List<Coordinate>
        get() {
            return List(4) {
                this + indirectNeighbourDirections[it]
            }
        }

    override val allNeighbours: List<Coordinate>
        get() = List(8) {
            this + allNeighbourDirections[it]
        }

    override val isHorizontal: Boolean
        get() = this.y == 0 && this.x != 0

    override val isVertical: Boolean
        get() = this.x == 0 && this.y != 0

    override operator fun times(other: Coordinate): Coordinate {
        return Coordinate(this.x * other.x, this.y * other.y)
    }

    override operator fun times(scalar: Int): Coordinate {
        return Coordinate(this.x * scalar, this.y * scalar)
    }

    operator fun rangeTo(other: Coordinate): CoordinateRange {
        return CoordinateRange(this, other)
    }

    override fun toString(): String {
        return "($x,$y)"
    }
}

fun Collection<Coordinate>.range(): CoordinateRange = CoordinateRange(this)

val Collection<Coordinate>.minX: Int
    get() = this.minOf { it.x }

val Collection<Coordinate>.minY: Int
    get() = this.minOf { it.y }

val Collection<Coordinate>.maxX: Int
    get() = this.maxOf { it.x }

val Collection<Coordinate>.maxY: Int
    get() = this.maxOf { it.y }

val Collection<Coordinate>.sizeX: Int
    get() = this.maxX + 1

val Collection<Coordinate>.sizeY: Int
    get() = this.maxY + 1

val <E> Map<Coordinate, E>.minX: Int
    get() = this.keys.minX

val <E> Map<Coordinate, E>.minY: Int
    get() = this.keys.minY

val <E> Map<Coordinate, E>.maxX: Int
    get() = this.keys.maxX

val <E> Map<Coordinate, E>.maxY: Int
    get() = this.keys.maxY

val <E> Map<Coordinate, E>.sizeX: Int
    get() = this.maxX + 1

val <E> Map<Coordinate, E>.sizeY: Int
    get() = this.maxY + 1

val <E> Map<Coordinate, E>.minCoordinate: Coordinate
    get() = Coordinate(minX, minY)

val <E> Map<Coordinate, E>.maxCoordinate: Coordinate
    get() = Coordinate(maxX, maxY)

val <E> Map<Coordinate, E>.coordinateRange: CoordinateRange
    get() = CoordinateRange(keys)


/**
 * Range to enumerate coordinates between the (minx, miny) and (maxx, maxy) found in a list of coordinates.
 */
class CoordinateRange(private val minMaxCoordinate: Pair<Coordinate, Coordinate>) : Iterable<Coordinate>,
    ClosedRange<Coordinate> {

    constructor(minCoordinate: Coordinate, width: Int, height: Int) : this(
        Pair(
            minCoordinate,
            minCoordinate + Coordinate(width - 1, height - 1)
        )
    )

    constructor(minCoordinate: Coordinate, maxCoordinate: Coordinate) : this(Pair(minCoordinate, maxCoordinate))
    constructor(collection: Collection<Coordinate>) : this(collection.minMaxCoordinate())

    companion object {
        private fun Collection<Coordinate>.minMaxCoordinate(): Pair<Coordinate, Coordinate> {
            var minX = Integer.MAX_VALUE
            var minY = Integer.MAX_VALUE
            var maxX = Integer.MIN_VALUE
            var maxY = Integer.MIN_VALUE
            for (coordinate in this) {
                minX = kotlin.math.min(minX, coordinate.x)
                minY = kotlin.math.min(minY, coordinate.y)
                maxX = kotlin.math.max(maxX, coordinate.x)
                maxY = kotlin.math.max(maxY, coordinate.y)
            }
            return Pair(Coordinate(minX, minY), Coordinate(maxX, maxY))
        }
    }

    private class CoordinateIterator(
        val minCoordinate: Coordinate,
        val maxCoordinate: Coordinate,
        val reversed: Boolean
    ) :
        Iterator<Coordinate> {
        private var nextCoordinate: Coordinate? =
            if (reversed) {
                if (minCoordinate <= maxCoordinate) maxCoordinate else null
            } else {
                if (minCoordinate <= maxCoordinate) minCoordinate else null
            }

        override fun hasNext(): Boolean {
            return nextCoordinate != null
        }

        override fun next(): Coordinate {
            val next = nextCoordinate
                ?: throw IllegalStateException("Next called on iterator while there are no more elements to iterate over")
            nextCoordinate = if (reversed) {
                when {
                    next.x > minCoordinate.x -> next.offset(-1, 0)
                    next.y > minCoordinate.y -> Coordinate(maxCoordinate.x, next.y - 1)
                    else -> null
                }
            } else {
                when {
                    next.x < maxCoordinate.x -> next.offset(1, 0)
                    next.y < maxCoordinate.y -> Coordinate(minCoordinate.x, next.y + 1)
                    else -> null
                }
            }
            return next
        }
    }

    override fun iterator(): Iterator<Coordinate> = CoordinateIterator(minMaxCoordinate.first, minMaxCoordinate.second, false)

    fun reverseIterator(): Iterator<Coordinate> = CoordinateIterator(minMaxCoordinate.first, minMaxCoordinate.second, true)

    override fun toString(): String {
        return "$start..$endInclusive"
    }

    override val endInclusive: Coordinate
        get() = minMaxCoordinate.second
    override val start: Coordinate
        get() = minMaxCoordinate.first


    val sizeX: Int
        get() = endInclusive.x - start.x + 1

    val sizeY: Int
        get() = endInclusive.y - start.y + 1

    val size: Int
        get() = sizeX * sizeY

    override fun contains(value: Coordinate): Boolean {
        return value.x in start.x..endInclusive.x && value.y in start.y..endInclusive.y
    }

    fun inset(insets: Insets): CoordinateRange {
        return CoordinateRange(
            start + Coordinate(insets.left, insets.top),
            endInclusive - Coordinate(insets.right, insets.bottom)
        )
    }
}

data class Insets(val left: Int, val right: Int, val top: Int, val bottom: Int) {
    companion object {
        fun square(dimension: Int): Insets {
            return Insets(dimension, dimension, dimension, dimension)
        }

        fun rectangle(horizontal: Int, vertical: Int): Insets {
            return Insets(horizontal, horizontal, vertical, vertical)
        }
    }
}