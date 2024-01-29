package com.yoimerdr.android.virtualjoystick.control

import android.graphics.Canvas
import com.yoimerdr.android.virtualjoystick.control.drawer.ControlDrawer
import com.yoimerdr.android.virtualjoystick.enums.Direction
import com.yoimerdr.android.virtualjoystick.enums.DirectionType
import com.yoimerdr.android.virtualjoystick.exceptions.InvalidControlPositionException
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.ImmutablePosition
import com.yoimerdr.android.virtualjoystick.geometry.MutablePosition
import com.yoimerdr.android.virtualjoystick.geometry.Plane
import com.yoimerdr.android.virtualjoystick.geometry.Position
import com.yoimerdr.android.virtualjoystick.geometry.Size

/**
 * Abstract class that represents a virtual joystick control.
 *
 * Custom control must inherit from this class.
 *
 */
abstract class Control(
    invalidRadius: Float,
    /**
     * The control directions type.
     *
     * Used to determine how many directions, in addition to [Direction.NONE],
     * will be taken into account when calling [direction].
     */
    var directionType: DirectionType
) {

    /**
     * The control position.
     */
    private val mPosition: MutablePosition

    /**
     * The center of the view.
     */
    private val mCenter: MutablePosition


    /**
     * Circle representing the area of the view where the control is used.
     */
    private val viewCircle: Circle


    /**
     * Invalid radius to be taken into account when obtaining control [direction].
     */
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
    abstract var drawer: ControlDrawer

    init {
        mCenter = Position()
        mPosition = Position()
        viewCircle = Circle(1f, mCenter)
        validateInvalidRadius()
    }

    /**
     * Gets the direction of the control.
     *
     * The direction is based on the [anglePosition] value.
     *
     * If the distance from the center is less than the [invalidRadius], the
     * direction is considered as [Direction.NONE].
     *
     * @return A [Direction] enum representing the direction.
     *
     * If [directionType] is [DirectionType.FOUR] possible values are:
     * [Direction.NONE], [Direction.LEFT], [Direction.RIGHT], [Direction.UP] and [Direction.DOWN].
     *
     * Otherwise, possible values are all [Direction] enum entries.
     */

    open val direction: Direction get() {
        if (distanceFromCenter <= invalidRadius)
            return Direction.NONE

        val angleDegrees = Math.toDegrees(anglePosition)

        if(directionType == DirectionType.EIGHT)
            return when(Plane.quadrantOf(angleDegrees, Plane.MaxQuadrants.EIGHT,true)) {
                1 -> Direction.RIGHT
                2 -> Direction.DOWN_RIGHT
                3 -> Direction.DOWN
                4 -> Direction.DOWN_LEFT
                5 -> Direction.LEFT
                6 -> Direction.UP_LEFT
                7 -> Direction.UP
                8 -> Direction.UP_RIGHT
                else -> Direction.NONE
            }

        return when(Plane.quadrantOf(angleDegrees, true)) {
            1 -> Direction.RIGHT
            2 -> Direction.DOWN
            3 -> Direction.LEFT
            4 -> Direction.UP
            else -> Direction.NONE
        }
    }

    /**
     * Gets the immutable position of control center.
     *
     * @return A new instance of the control center as [ImmutablePosition].
     */
    val center: ImmutablePosition get() = mCenter.toImmutable()

    /**
     * Gets the immutable position of control position.
     *
     * @return A new instance of the control position as [ImmutablePosition].
     */
    open val position: ImmutablePosition get() = mPosition.toImmutable()


    /**
     * Calculates the distance between current position and center.
     * @return The calculated distance.
     */
    val distanceFromCenter: Float get() = viewCircle.distanceTo(mPosition)

    /**
     * Calculates the angle (clockwise) formed from the current position and center.
     * @return A double value in the range from 0 to 2PI radians.
     */
    val anglePosition: Double get() = viewCircle.angleTo(mPosition)

    /**
     * Gets the parametric position of current position in the view circle.
     *
     * @return A new instance of the parametric position.
     */

    val viewParametricPosition: ImmutablePosition get() = viewCircle.parametricPositionOf(anglePosition)


    /**
     * Gets the radius of the view where the control is used.
     */
    val viewRadius: Double get() = viewCircle.radius

    /**
     * Validates the control position values.
     *
     * @throws InvalidControlPositionException If any of the position values is negative.
     */
    @Throws(InvalidControlPositionException::class)
    protected fun validatePositionValues() {
        if(mPosition.x < 0 || mPosition.y < 0)
            throw InvalidControlPositionException("None of the position values can be negative.")
    }

    /**
     * Validates the [invalidRadius] value.
     *
     * @throws IllegalArgumentException If [invalidRadius] value is negative.
     */
    @Throws(IllegalArgumentException::class)
    private fun validateInvalidRadius() {
        if(invalidRadius < 0)
            throw IllegalArgumentException("Invalid radius value must be positive.")
    }

    /**
     * Checks if [distanceFromCenter] is greater than the [viewRadius].
     * If so, changes the position to the [viewParametricPosition].
     */
    private fun validatePositionLimits() {
        if (distanceFromCenter > viewRadius)
            mPosition.set(viewParametricPosition)
    }

    /**
     * Called (or call it) when the size of the view changes.
     *
     * It updates the drawer position and center.
     * @param size The size of the view.
     * @throws InvalidControlPositionException If any of the position values is negative.
     */
    @Throws(InvalidControlPositionException::class)
    fun onSizeChanged(size: Size) {
        size.apply {
            (width.coerceAtMost(height) / 2f).also {
                mCenter.set(it, it)
                viewCircle.radius = it.toDouble()
                toCenter()
            }
        }
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
     * Sets the current position of the control.
     *
     * This method performs additional validations with [validatePositionLimits] & [validatePositionValues]
     *
     * @param position The new position to be assigned.
     * @throws InvalidControlPositionException If any of the position values is negative.
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
     * @param x The x coordinate to be assigned.
     * @param y The y coordinate to be assigned.
     *
     * @throws InvalidControlPositionException If any of the position values is negative.
     */
    @Throws(InvalidControlPositionException::class)
    fun setPosition(x: Float, y: Float) {
        mPosition.set(x, y)
        validatePositionLimits()
        validatePositionValues()
    }

    /**
     * Sets the current position to center.
     * @throws InvalidControlPositionException If any of the position values is negative.
     */
    @Throws(InvalidControlPositionException::class)
    fun toCenter() = setPosition(mCenter)

    /**
     * Checks if current position is an the same center coordinates.
     *
     * @return True if is position is equals to center; otherwise, false.
     */
    fun isInCenter(): Boolean = mPosition == mCenter

    /**
     * Calculates the difference in the x-coordinate between the current position and the center.
     * @return The calculated difference.
     */
    protected fun deltaX(): Float = mPosition.deltaX(mCenter)

    /**
     * Calculates the difference in the y-coordinate between the current position and the center.
     * @return The calculated difference.
     */
    protected fun deltaY(): Float = mPosition.deltaY(mCenter)
}