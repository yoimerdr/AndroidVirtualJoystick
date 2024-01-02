package com.yoimerdr.android.virtualjoystick.geometry

import java.security.InvalidParameterException
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

class Circle(
    var radius: Float,
    val center: Position
) {

    val diameter: Float get() = 2 * radius
    val circumference: Double get() = PI * diameter

    init {
        if(radius <= 0)
            throw InvalidParameterException("The radius of the circle cannot be negative or zero.")
    }

    /**
     * Calculates the Euclidean distance between position and circle center.
     * @param position The position with x and y coordinates.
     * @return The calculated distance.
     */
    fun distanceFrom(position: Position): Float {
        return hypot(position.deltaX(center), position.deltaY(center))
    }

    /**
     * @param position The position with x and y coordinates.
     * @return The angle formed from the given position and the circle center. (angle in the range from 0 to 2PI radians clockwise)
     */
    fun angleFrom(position: Position): Double {
        var angle = atan2(position.deltaY(center), position.deltaX(center)).toDouble()

        if(angle < 0)
            angle += 2 * PI

        return angle
    }
    /**
     * @param angle The angle in the range from 0 to 2PI radians clockwise.
     * @return The circle parametric position for given angle,
     */
    fun parametricPositionFrom(angle: Double): Position {
        val x = radius * cos(angle) + center.x
        val y = radius * sin(angle) + center.y

        return Position(x, y)
    }

    fun setCenter(center: Position) = this.center.set(center)
}