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
 */
abstract class Control(
    /**
     * Invalid radius to be taken into account when obtaining control direction
     */
    invalidRadius: Float,
) {

    /**
     * The control position.
     */
    protected val position: MutablePosition

    /**
     * The center of the view.
     */
    protected val center: MutablePosition

    /**
     * Control's inner circle.
     */
    protected val inCircle: Circle

    /**
     * Control's outer circle.
     */
    protected val outCircle: Circle

     var invalidRadius: Float = invalidRadius
        set(value) {
            field = value
            validateInvalidRadius()
        }

    /**
     * The control drawer.
     *
     * Must be initialized in classes that inherit from [Control].
     */
    lateinit var drawer: ControlDrawer

    init {
        center = Position()
        position = Position()
        inCircle = Circle(1f, center)
        outCircle = Circle(1f, center)
        validateInvalidRadius()
    }

    /**
     * Validates the control [position] values.
     *
     * @throws InvalidControlPositionException If any of the [position] values is negative.
     */
    @Throws(InvalidControlPositionException::class)
    protected fun validatePositionValues() {
        if(position.x < 0 || position.y < 0)
            throw InvalidControlPositionException("None of the position values can be negative.")
    }

    /**
     * Validates the [invalidRadius] value.
     *
     * @throws IllegalArgumentException If [invalidRadius] value is negative.
     */
    @Throws(IllegalArgumentException::class)
    protected fun validateInvalidRadius() {
        if(invalidRadius < 0)
            throw IllegalArgumentException("Invalid radius value must be positive.")
    }

    /**
     * Abstract method to set radius restrictions of the control.
     *
     * This method should be implemented in subclasses to define specific radius restrictions
     * based on the view size.
     * @param size The size of the view.
     */
    protected abstract fun setRadiusRestriction(size: Size)

    /**
     * Abstract method for validate the position when is changed with [setPosition] methods.
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
     * Method to draw the control using the [drawer].
     * @param canvas The canvas on which the control will be drawn
     *
     */
    open fun onDraw(canvas: Canvas) {
        drawer.draw(canvas, this)
    }

    /**
     * Gets the direction of the control.
     *
     * The direction is based on the angle and distance of the control [position] from the [center].
     *
     *
     * If the distance from the center is less than the [invalidRadius], the
     * direction is considered as [Direction.NONE].
     *
     * @return A [Direction] enum representing the direction.
     */

    open val direction: Direction get() {
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
     * Gets the immutable position of control center.
     *
     * @return A new instance of the control [center] as [ImmutablePosition].
     */
    val immutableCenter: ImmutablePosition get() = center.toImmutable()

    /**
     * Gets the immutable position of control position.
     *
     * @return A new instance of the control [position] as [ImmutablePosition].
     */
    val immutablePosition: ImmutablePosition get() = position.toImmutable()

    /**
     * Gets the radius of the control's inner circle.
     */
    val innerRadius: Float get() = inCircle.radius

    /**
     * Gets the radius of the control's outer circle.
     */
    val outerRadius: Float get() = outCircle.radius

    /**
     * Gets the euclidean distance between current [position] and [center].
     * @return The calculated distance.
     */
    val distanceFromCenter: Float get() = inCircle.distanceFrom(position)

    /**
     * Gets the angle (clockwise) formed from the current [position] and the [center].
     * @return A double value in the range from 0 to 2PI radians
     */
    val anglePosition: Double get() = inCircle.angleFrom(position)

    /**
     * Gets the parametric position of current position in the control's outer circle.
     *
     * @return A new instance of the parametric position
     */

    val outParametricPosition: ImmutablePosition get() = outCircle.parametricPositionFrom(anglePosition)

    /**
     * Gets the parametric position of current [position] in the control's inner circle.
     *
     * @return A new instance of the parametric position
     */
    val inParametricPosition: ImmutablePosition get() = inCircle.parametricPositionFrom(anglePosition)

    /**
     * Sets the current position of the control.
     *
     * This method performs additional validations with [validatePositionLimits] & [validatePositionValues]
     *
     * If you want set the position from [validatePositionLimits],
     * use the set methods of control position.
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
     * Sets the current position of the control.
     *
     * This method performs additional validations with [validatePositionLimits] & [validatePositionValues]
     *
     * If you want set the position from [validatePositionLimits],
     * use the set methods of control [position].
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
     * Sets the current [position] to [center].
     */
    fun toCenter() = setPosition(center)

    /**
     * Checks if current [position] is [center].
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
}