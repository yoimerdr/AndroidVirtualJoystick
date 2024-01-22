package com.yoimerdr.android.virtualjoystick.control

import android.graphics.Canvas
import com.yoimerdr.android.virtualjoystick.control.drawer.ControlDrawer
import com.yoimerdr.android.virtualjoystick.enums.Direction
import com.yoimerdr.android.virtualjoystick.exceptions.InvalidControlPositionException
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.ImmutablePosition
import com.yoimerdr.android.virtualjoystick.geometry.MutablePosition
import com.yoimerdr.android.virtualjoystick.geometry.Position
import com.yoimerdr.android.virtualjoystick.geometry.Size

/**
 * Abstract class that represents a virtual joystick control.
 *
 * Custom control must inherit from this class.
 *
 * @param invalidRadius The invalid radius
 */
abstract class Control(
    /**
     * Invalid radius to be taken into account when obtaining control direction
     */
    var invalidRadius: Float,
) {

    /**
     * The control position.
     */
    protected val position: MutablePosition

    /**
     * The center of the view.
     */
    private val center: MutablePosition

    /**
     * Inner area of the view for the control.
     */
    protected val inCircle: Circle

    /**
     * Total (Outer) area of the view.
     *
     * Used to validate the maximum zone that the current [position] of the control can take.
     */
    protected val outCircle: Circle

    /**
     * The control drawer.
     *
     * Must be initialized on control children class.
     */
    lateinit var drawer: ControlDrawer

    init {
        center = Position()
        position = Position()
        inCircle = Circle(1f, center)
        outCircle = Circle(1f, center)
    }

    /**
     * Checks the [position] values to ensure they are non-negative.
     *
     * @throws InvalidControlPositionException If any of the [position] values is negative.
     */
    @Throws(InvalidControlPositionException::class)
    private fun validatePositionValues() {
        if(position.x < 0 || position.y < 0)
            throw InvalidControlPositionException("None of the position values can be negative.")
    }

    /**
     * Abstract method to set radius restrictions based on the view size.
     *
     * This method should be implemented in subclasses to define specific radius restrictions
     * based on the view size.
     * @param size The size of the view.
     */
    protected abstract fun setRadiusRestriction(size: Size)

    /**
     * Abstract method for validate the position when is changed with [setPosition]
     */
    abstract fun validatePositionLimits()

    /**
     * Called (or call it) when the size of the view changes.
     * It updates the drawer position and center, and applies radius restrictions from [setRadiusRestriction].
     * @param size The size of the view.
     */
    open fun onSizeChanged(size: Size) {
        (size.width / 2f).also {
            this.center.set(it, it)
            this.toCenter()
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
        drawer.draw(canvas, this)
    }

    /**
     * Determines the directional orientation based on the angle and distance of the drawer position from the center.
     *
     * If the distance from the center is less than the invalid radius specified, the
     * direction is considered as [Direction.NONE].
     * @return A [Direction] enum representing the computed direction.
     */

    val direction: Direction get() {
        if (distanceFromCenter <= invalidRadius)
            return Direction.NONE

        val angleDegrees = Math.toDegrees(anglePosition)
        return when {
            angleDegrees <= 22.5 || angleDegrees >= 337.6 -> Direction.RIGHT
            angleDegrees in 22.6..67.5 -> Direction.BOTTOM_RIGHT
            angleDegrees in 67.6 .. 112.5 -> Direction.BOTTOM
            angleDegrees in 112.6 .. 157.5 -> Direction.BOTTOM_LEFT
            angleDegrees in 157.6 .. 202.5 -> Direction.LEFT
            angleDegrees in 202.6 .. 247.5 -> Direction.UP_LEFT
            angleDegrees in 247.6 .. 292.5 -> Direction.UP
            angleDegrees in 292.6 .. 337.5 -> Direction.UP_RIGHT
            else -> Direction.NONE
        }
    }

    /**
     * @return The immutable position of control center
     */
    val immutableCenter: ImmutablePosition get() = center.toImmutable()

    /**
     * @return The immutable position of control position
     */
    val immutablePosition: ImmutablePosition get() = position.toImmutable()

    /**
     * @return The inner circle radius
     */
    val innerRadius: Float get() = inCircle.radius

    /**
     * @return The outer circle radius
     */
    val outerRadius: Float get() = outCircle.radius

    /**
     * Sets the current position of the drawer to the provided position.
     *
     * This method also performs additional validations with [validatePositionLimits] & [validatePositionValues]
     *
     * @param position The new position to be assigned.
     */
    @Throws(InvalidControlPositionException::class)
    fun setPosition(position: ImmutablePosition) {
        position.apply {
            setPosition(x, y)
        }
    }

    /**
     * Sets the current position of the drawer to the provided position.
     *
     * This method also performs additional validations with [validatePositionLimits] & [validatePositionValues]
     *
     * @param x The x coordinate to be assigned.
     * @param y The y coordinate to be assigned.
     */
    @Throws(InvalidControlPositionException::class)
    fun setPosition(x: Float, y: Float) {
        position.set(x, y)
        validatePositionLimits()
        validatePositionValues()
    }

    /**
     * Sets the current position to center.
     */
    fun toCenter() = position.set(center)

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
    protected fun deltaX(): Float = position.deltaX(center)

    /**
     * Calculates the difference in the y-coordinate between the current position and the center.
     * @return The calculated difference.
     */
    protected fun deltaY(): Float = position.deltaY(center)

    /**
     * Gets the Euclidean distance between current position and center
     * @return The calculated distance.
     */
    val distanceFromCenter: Float get() = inCircle.distanceFrom(position)
    /**
     * @return The angle formed from the current position and the center position (sexagesimal degrees clockwise).
     */

    /**
     * @return The angle formed from the current position and the center position. (angle in the range from 0 to 2PI radians clockwise)
     */
    val anglePosition: Double get() = inCircle.angleFrom(position)

    val outParametricPosition: ImmutablePosition get() = outCircle.parametricPositionFrom(anglePosition)

    val inParametricPosition: ImmutablePosition get() = inCircle.parametricPositionFrom(anglePosition)
}