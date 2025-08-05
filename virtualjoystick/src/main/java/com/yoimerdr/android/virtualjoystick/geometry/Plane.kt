package com.yoimerdr.android.virtualjoystick.geometry

import android.annotation.SuppressLint
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import com.yoimerdr.android.virtualjoystick.geometry.position.FixedPosition
import com.yoimerdr.android.virtualjoystick.geometry.position.ImmutablePosition
import com.yoimerdr.android.virtualjoystick.utils.extensions.requirePositive
import kotlin.math.atan2
import kotlin.math.hypot

object Plane {

    enum class MaxQuadrants {
        FOUR,
        EIGHT;

        fun toInt(): Int {
            return if (this == FOUR) 4
            else 8
        }
    }

    /**
     * Gets the quadrant of the given angle.
     * @param angle The angle (degrees) of the position. Must be positive.
     * @param maxQuadrants The max quadrants in the circle.
     * @param useMiddle Sets true if you want to use the middle of angle quadrant.
     * @return if [maxQuadrants] is [MaxQuadrants.FOUR], a value in the range 1 to 4;
     * otherwise, a value in the range 1 to 8.
     * @throws IllegalArgumentException If the [angle] is negative.
     */
    @JvmStatic
    @JvmOverloads
    @Throws(IllegalArgumentException::class)
    @IntRange(from = 1,to = 8)
    fun quadrantOf(
        angle: Double,
        maxQuadrants: MaxQuadrants = MaxQuadrants.FOUR,
        useMiddle: Boolean = false,
    ): Int {
        angle.requirePositive()
        val quadrants = maxQuadrants.toInt()
        val angleQuadrant = Circle.DEGREE_SPIN / quadrants

        var startAngle = 0.0f
        var end = if (useMiddle) angleQuadrant / 2 else angleQuadrant

        val mAngle = if (angle > Circle.DEGREE_SPIN) angle.mod(Circle.DEGREE_SPIN) else angle

        for (quadrant in 0..quadrants) {
            if (mAngle in startAngle..end) {
                return if (useMiddle && quadrant == quadrants) 1
                else quadrant + 1
            }

            startAngle = end

            end += if (useMiddle && quadrant == quadrants) angleQuadrant / 2
            else angleQuadrant
        }

        return quadrants
    }


    /**
     * Gets the quadrant of the given angle.
     * @param angle The degree of the position. Must be in range 0 to 360.
     * @param useMiddle Sets true if you want to use the middle of angle quadrant.
     * @return A value in the range 1 to 4.
     * @throws IllegalArgumentException If the [angle] is negative.
     */
    @JvmStatic
    @IntRange(from = 1, to = 4)
    @Throws(IllegalArgumentException::class)
    @SuppressLint("Range")
    fun quadrantOf(angle: Double, useMiddle: Boolean): Int {
        return quadrantOf(angle, MaxQuadrants.FOUR, useMiddle)
    }

    /**
     * Calculates the distance between the given positions.
     * @param positionA The position A.
     * @param positionB The position B
     * @return The distance between the 2 positions.
     */
    @JvmStatic
    fun distanceBetween(positionA: ImmutablePosition, positionB: ImmutablePosition): Float {
        return hypot(positionA.deltaX(positionB), positionA.deltaY(positionB))
    }

    /**
     * Calculates the distance between the given coordinates.
     * @param xA The x coordinate of position the A.
     * @param yA The y coordinate of position the A.
     * @param xB The x coordinate of position the B.
     * @param yB The y coordinate of position the B.
     * @return The distance between the coordinates.
     */
    @JvmStatic
    fun distanceBetween(xA: Float, yA: Float, xB: Float, yB: Float): Float {
        return hypot(xA - xB, yA - yB)
    }

    /**
     * Calculates the angle between the given positions.
     *
     * The method use the [Math.atan2] for gets the angle.
     * @param positionA The position A.
     * @param positionB The position B
     * @return A value in the range from 0 to 2PI radians clockwise.
     */
    @JvmStatic
    @FloatRange(from = 0.0, to = Circle.RADIAN_SPIN)
    fun angleBetween(positionA: ImmutablePosition, positionB: ImmutablePosition): Double {
        var angle = atan2(positionA.deltaY(positionB), positionA.deltaX(positionB)).toDouble()

        if (angle < 0)
            angle += Circle.RADIAN_SPIN

        return angle
    }

    /**
     * Calculates the angle between the given positions.
     *
     * The method use the [Math.atan2] for gets the angle.
     * @param xA The x coordinate of the position A.
     * @param yA The y coordinate of the position A.
     * @param xB The x coordinate of the position B.
     * @param yB The y coordinate of the position B.
     * @see [Plane.angleBetween]
     * @return A value in the range from 0 to 2PI radians clockwise.
     */
    @JvmStatic
    @FloatRange(from = 0.0, to = Circle.RADIAN_SPIN)
    fun angleBetween(xA: Float, yA: Float, xB: Float, yB: Float): Double {
        return angleBetween(FixedPosition(xA, yA), FixedPosition(xB, yB))
    }
}