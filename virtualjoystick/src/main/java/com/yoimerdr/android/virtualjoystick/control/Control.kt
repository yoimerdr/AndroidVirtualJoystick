package com.yoimerdr.android.virtualjoystick.control

import android.graphics.Canvas
import com.yoimerdr.android.virtualjoystick.control.drawer.ControlDrawer
import com.yoimerdr.android.virtualjoystick.enums.Direction
import com.yoimerdr.android.virtualjoystick.exceptions.ControlDrawerPositionException
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.Position
import com.yoimerdr.android.virtualjoystick.geometry.Size

/**
 * Abstract class used to draw and obtain information about the virtual joystick control.
 *
 * Custom control drawers must inherit from this class.
 */
abstract class Control(
    /**
     * The current control control position.
     */
    val position: Position,
    /**
     * Invalid radius to be taken into account when obtaining control direction
     */
    val invalidRadius: Int,
) {

    /**
     * Center of the view.
     */
    protected val center: Position

    /**
     * Inner area of the view for draw the control.
     */
    protected val inCircle: Circle

    /**
     * Total area of the view.
     *
     * Used to validate the maximum zone that the current [position] of the control can take.
     */
    protected val outCircle: Circle

    var drawer: ControlDrawer? = null

    init {
        validatePositionValues()
        center = Position()
        inCircle = Circle(1f, center)
        outCircle = Circle(1f, center)
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
     * It updates the drawer position and center, and applies radius restrictions from [setRadiusRestriction].
     * @param size The size of the view.
     */
    open fun onSizeChanged(size: Size) {
        (size.width / 2f).also {
            this.center.set(it, it)
            this.toCenterPosition()
        }

        setRadiusRestriction(size)
    }

    /**
     * Method to draw the control
     * @param canvas The canvas on which the control will be drawn
     *
     * This method should be implemented in subclasses to define the drawing behavior for the custom control drawer.
     */
    open fun onDraw(canvas: Canvas) {
        drawer?.draw(canvas, this)
    }

    /**
     * Determines the directional orientation based on the angle and distance of the drawer position from the center.
     *
     * If the distance from the center is less than the invalid radius specified, the
     * direction is considered as [Direction.NONE].
     * @return A [Direction] enum representing the computed direction.
     */
    fun getDirection(): Direction {
        if (distanceFromCenter() <= invalidRadius)
            return Direction.NONE

        val angleDegrees = getDegreesAngle()
        return when {
            angleDegrees <= 22.5 || angleDegrees >= 337.5 -> Direction.RIGHT
            angleDegrees in 22.5..67.5 -> Direction.BOTTOM_RIGHT
            angleDegrees in 67.5 .. 112.5 -> Direction.BOTTOM
            angleDegrees in 112.5 .. 157.5 -> Direction.BOTTOM_LEFT
            angleDegrees in 157.5 .. 202.5 -> Direction.LEFT
            angleDegrees in 202.5 .. 247.5 -> Direction.UP_LEFT
            angleDegrees in 247.5 .. 292.5 -> Direction.UP
            angleDegrees in 292.5 .. 337.5 -> Direction.UP_RIGHT
            else -> Direction.NONE
        }
    }

    /**
     * Sets the current position of the drawer to the provided position and performs additional validations with [validatePositionLimits].
     * @param position The new position to be assigned.
     */
    open fun setPosition(position: Position) {
        this.position.set(position)
        validatePositionLimits()
    }

    /**
     * Sets the current position to center.
     */
    fun toCenterPosition() = position.set(center)

    /**
     * Checks if current position is center.
     *
     * @return True if is position is equals to center; otherwise, false.
     */
    fun isInCenter(): Boolean = position == center

    /**
     * Calculates the difference in the x-coordinate between the current position and the center.
     * @return The calculated difference.
     */
    protected open fun deltaX(): Float = position.deltaX(center)

    /**
     * Calculates the difference in the y-coordinate between the current position and the center.
     * @return The calculated difference.
     */
    protected open fun deltaY(): Float = position.deltaY(center)

    /**
     * Calculates the Euclidean distance between current position and center
     * @return The calculated distance.
     */
    open fun distanceFromCenter(): Float = inCircle.distanceFrom(position)
    /**
     * @return The angle formed from the current position and the center position (sexagesimal degrees clockwise).
     */
    open fun getDegreesAngle(): Double = Math.toDegrees(getRadianAngle())

    /**
     * @return The angle formed from the current position and the center position. (angle in the range from 0 to 2PI radians clockwise)
     */
    open fun getRadianAngle(): Double = inCircle.angleFrom(position)

    /**
     * Abstract method for validate the position when is changed with [setPosition]
     */
    abstract fun validatePositionLimits()
}