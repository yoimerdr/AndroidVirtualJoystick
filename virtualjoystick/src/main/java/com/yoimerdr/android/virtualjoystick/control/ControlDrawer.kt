package com.yoimerdr.android.virtualjoystick.control

import android.graphics.Canvas
import android.graphics.Paint
import com.yoimerdr.android.virtualjoystick.enums.Direction
import com.yoimerdr.android.virtualjoystick.exceptions.ControlDrawerPositionException
import com.yoimerdr.android.virtualjoystick.models.Position
import com.yoimerdr.android.virtualjoystick.models.Size
import kotlin.math.atan2
import kotlin.math.hypot

abstract class ControlDrawer(
    protected val paint: Paint,
    val position: Position,
    innerRadius: Float,
) {
    data class VirtualDrawerProperties(
        val innerRadius: Float,
        var maximumRadius: Float,
        var radius: Float
    )

    protected open val properties: VirtualDrawerProperties = VirtualDrawerProperties(
        innerRadius = innerRadius,
        maximumRadius = 0f,
        radius = 0f
    )

    protected val center: Position

    init {
        validatePositionValues()
        center = Position(position.x / 2, position.x / 2)
    }

    /**
     * Checks the position values to ensure they are non-negative.
     *
     * @throws ControlDrawerPositionException If any of the position values is negative.
     */
    private fun validatePositionValues() {
        if(position.x < 0 || position.y < 0)
            throw ControlDrawerPositionException("None of the position values can be negative.")
    }

    /**
     * An abstract method to set radius restrictions based on the view size.
     *
     * This method should be implemented in subclasses to define specific radius restrictions
     * based on the view size.
     * @param size The size of the view.
     */
    protected abstract fun setRadiusRestriction(size: Size)

    /**
     * Called (or call it) when the size of the view changes.
     * It updates the position and applies radius restrictions from [setRadiusRestriction].
     * @param size The size of the view.
     */
    fun onSizeChanged(size: Size) {
        (size.width / 2f).also {
            this.position.set(it, it)
        }

        setRadiusRestriction(size)
    }

    /**
     * Abstract method to draw the control
     * @param canvas The canvas on which the background will be drawn
     * @param size The size of the view.
     *
     * This method should be implemented in subclasses to define the drawing behavior for the custom control drawer.
     */
    abstract fun onDraw(canvas: Canvas, size: Size)

    /**
     * Determines the directional orientation based on the angle and distance from the center.
     *
     * If the distance from the center is less than the inner radius specified, the
     * direction is considered as [Direction.NONE].
     * @return A [Direction] enum representing the computed direction.
     */
    fun getDirection(): Direction {
        if (distanceFromCenter() < properties.innerRadius)
            return Direction.NONE

        val angleDegrees = getDegreesAngle()
        return when {
            angleDegrees <= 22.5 || angleDegrees >= 337.5 -> Direction.RIGHT
            angleDegrees in 22.5..67.5 -> Direction.UP_RIGHT
            angleDegrees in 67.5 .. 112.5 -> Direction.UP
            angleDegrees in 112.5 .. 157.5 -> Direction.UP_LEFT
            angleDegrees in 157.5 .. 202.5 -> Direction.LEFT
            angleDegrees in 202.5 .. 247.5 -> Direction.BOTTOM_LEFT
            angleDegrees in 247.5 .. 292.5 -> Direction.BOTTOM
            angleDegrees in 292.5 .. 337.5 -> Direction.BOTTOM_RIGHT
            else -> Direction.NONE
        }
    }

    /**
     * Sets the current position of the drawer to the provided position and performs additional validations with [validatePositionLimits].
     * @param position The new position to be assigned.
     */
    fun setPosition(position: Position) {
        this.position.set(position)
        validatePositionLimits()
    }

    /**
     * Sets the current position to center.
     */
    fun toCenterPosition() = position.set(center)

    /**
     * Sets the current paint of drawer
     * @param paint The new drawer paint
     */
    fun setPaint(paint: Paint) {
        this.paint.set(paint)
    }

    /**
     * Calculates the difference in the x-coordinate between the current position and the plant.
     * @return The calculated difference.
     */
    protected open fun deltaX(): Float = position.x - center.x

    /**
     * Calculates the difference in the y-coordinate between the current position and the plant.
     * @return The calculated difference.
     */
    protected open fun deltaY(): Float = position.y - center.y

    /**
     * Calculates the Euclidean distance between current position and center
     * @return The calculated distance.
     */
    protected open fun distanceFromCenter(): Float = hypot(deltaX(), deltaY())
    /**
     * @return The angle formed from the current position and the center position (sexagesimal degrees counterclockwise).
     */
    open fun getDegreesAngle(): Double {
        val degrees = Math.toDegrees(getRadianAngle())
        return if(degrees < 0) degrees + 360 else degrees
    }

    /**
     * @return The angle formed from the current position and the center position. (angle in the range from -PI to PI radians counterclockwise)
     */
    protected open fun getRadianAngle(): Double = atan2(center.y - position.y, deltaX()).toDouble()

    /**
     * Check if the distance at the current position and the center is greater than the set maximum radius.
     * If so, change the position to the extreme maximum at that position.
     */
    protected open fun validatePositionLimits() {
        val distance = distanceFromCenter()
        if (distance > properties.maximumRadius) {
            val proportion = properties.maximumRadius / distance
            position.set(deltaX() * proportion + center.x, deltaY() * proportion + center.y)
        }
    }
}