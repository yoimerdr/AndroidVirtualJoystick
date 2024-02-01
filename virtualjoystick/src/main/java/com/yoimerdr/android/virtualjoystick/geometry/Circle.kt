package com.yoimerdr.android.virtualjoystick.geometry

import androidx.annotation.IntRange
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Circle(
    radius: Double,
    val center: MutablePosition
) {
    constructor(radius: Float, center: MutablePosition) : this(radius.toDouble(), center)
    constructor(radius: Int, center: MutablePosition) : this(radius.toDouble(), center)


    /**
     * Getter and setter for the circle radius.
     *
     * If you use the setter, the value must be higher than zero.
     * @throws IllegalArgumentException if in the setter, the value is lower or equal than zero.
     */
    @set:Throws(IllegalArgumentException::class)
    var radius: Double = radius
        set(value) {
            validateRadius(value)
            field = value
        }

    /**
     * Calculates the circle diameter.
     *
     * This is equals to:
     * ```
     * circle.radius * 2
     * ```
     */
    val diameter: Double get() = 2 * radius

    /**
     * Calculates the circle circumference.
     *
     * This is equals to:
     * ```
     * circle.diameter * Math.PI
     * ```
     */
    val circumference: Double get() = PI * diameter

    init {
        validateRadius(radius)
    }

    companion object {
        /**
         * The value of the angle (degrees) of a circle spin.
         */
        const val DEGREE_SPIN = 360f

        /**
         * The value of the angle (radians) of a circle spin.
         */
        const val RADIAN_SPIN = 2 * PI

        /**
         * Instance a new circle with a immutable center position.
         *
         * The [center] is converted to [MutablePosition] with [Position] class.
         * @param radius The circle radius.
         * @param center The circle center.
         * @throws IllegalArgumentException If the radius value is lower or equal than zero.
         */
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun fromImmutableCenter(radius: Double, center: ImmutablePosition): Circle {
            return Circle(radius, Position(center))
        }

    }

    @Throws(IllegalArgumentException::class)
    private fun validateRadius(radius: Double) {
        if(radius <= 0)
            throw IllegalArgumentException("The radius of the circle cannot be negative or zero.")
    }

    /**
     * Calculates the distance between the circle center and the given position.
     * @param position The position with x and y coordinates.
     * @see [Plane.distanceBetween]
     * @return The calculated distance.
     */
    fun distanceTo(position: ImmutablePosition): Float {
        return Plane.distanceBetween(position, center)
    }

    /**
     * Calculates the distance between the circle center and the given coordinates.
     * @param x The x coordinate of the position.
     * @param y The y coordinate of the position.
     * @return The calculated distance.
     */
    fun distanceTo(x: Float, y: Float): Float {
        return distanceTo(FixedPosition(x, y))
    }

    /**
     * Calculates the angle formed from the given position and the circle center.
     * @param position The position with x and y coordinates.
     * @see [Plane.angleBetween]
     * @return A double value in the range from 0 to 2PI radians clockwise.
     */
    fun angleTo(position: ImmutablePosition): Double {
        return Plane.angleBetween(position, center)
    }

    /**
     * Calculates the angle formed from the circle center and the given coordinates.
     * @param x The x coordinate of the position.
     * @param y The y coordinate of the position.
     * @return A double value in the range from 0 to 2PI radians clockwise.
     */
    fun angleTo(x: Float, y: Float): Double {
        return angleTo(FixedPosition(x, y))
    }

    /**
     * Calculates the circle parametric position for given angle
     * @param angle The angle in the range from 0 to 2PI radians clockwise.
     * @return A [ImmutablePosition] instance with the calculates coordinates.
     */
    fun parametricPositionOf(angle: Double): ImmutablePosition {
        val x = radius * cos(angle) + center.x
        val y = radius * sin(angle) + center.y

        return FixedPosition(x, y)
    }

    /**
     * Gets the quadrant of the given position according the circle center.
     *
     * This method does not check if the position is in the circle area.
     * @param position The position with x and y coordinates.
     * @param maxQuadrants The max quadrants in the circle.
     * @param useMiddle Sets true if you want to use the middle of angle quadrant.
     * @see [Plane.quadrantOf]
     * @return if [maxQuadrants] is [Plane.MaxQuadrants.FOUR], a value in the range 1 to 4;
     * otherwise, a value in the range 1 to 8.
     */
    fun quadrantOf(position: ImmutablePosition, maxQuadrants: Plane.MaxQuadrants, useMiddle: Boolean): Int {
        return Plane.quadrantOf(angleTo(position), maxQuadrants, useMiddle)
    }

    /**
     * Gets the quadrant of the given position according the circle center.
     *
     * This method does not check if the position is in the circle area.
     * @param position The position with x and y coordinates.
     * @param maxQuadrants The max quadrants in the circle.
     * @return if [maxQuadrants] is [Plane.MaxQuadrants.FOUR], a value in the range 1 to 4;
     * otherwise, a value in the range 1 to 8.
     */
    fun quadrantOf(position: ImmutablePosition, maxQuadrants: Plane.MaxQuadrants): Int {
        return quadrantOf(position, maxQuadrants, false)
    }

    /**
     * Gets the quadrant of the given position.
     *
     * This method does not check if the position is in the circle area.
     * @param position The position with x and y coordinates.
     * @param useMiddle Sets true if you want to use the middle of angle quadrant.
     * @return A value in the range 1 to 4.
     */
    @IntRange(from = 1, to = 4)
    fun quadrantOf(position: ImmutablePosition, useMiddle: Boolean): Int {
        return quadrantOf(position, Plane.MaxQuadrants.FOUR, useMiddle)
    }

    /**
     * Gets the quadrant of the given position.
     *
     * This method does not check if the position is in the circle area.
     * @param position The position with x and y coordinates.
     * @return A value in the range 1 to 4.
     */
    @IntRange(from = 1, to = 4)
    fun quadrantOf(position: ImmutablePosition): Int {
        return quadrantOf(position, false)
    }

    fun setCenter(position: ImmutablePosition) {
        this.center.set(position)
    }

    fun setCenter(x: Float, y: Float) {
        center.set(x, y)
    }
}