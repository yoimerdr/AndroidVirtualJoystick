package com.yoimerdr.android.virtualjoystick.control

import android.graphics.Canvas
import android.graphics.Color
import androidx.annotation.ColorInt
import com.yoimerdr.android.virtualjoystick.control.drawer.ArcControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.CircleControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.ControlDrawer
import com.yoimerdr.android.virtualjoystick.exceptions.InvalidControlPositionException
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.ImmutablePosition
import com.yoimerdr.android.virtualjoystick.geometry.MutablePosition
import com.yoimerdr.android.virtualjoystick.geometry.Plane
import com.yoimerdr.android.virtualjoystick.geometry.Position
import com.yoimerdr.android.virtualjoystick.geometry.Size
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme
import com.yoimerdr.android.virtualjoystick.views.JoystickView
import com.yoimerdr.android.virtualjoystick.views.JoystickView.Direction
import com.yoimerdr.android.virtualjoystick.views.JoystickView.DirectionType

/**
 * Represents a virtual joystick control.
 *
 * Custom control must inherit from this class.
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
     * Invalid radius to be taken into account when obtaining control direction.
     *
     * @see [Control.direction]
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

    enum class DefaultType(val id: Int) {
        CIRCLE(0),
        ARC(1),
        CIRCLE_ARC(2);
        companion object {
            /**
             * @param id The id for the enum value
             * @return The enum value for the given id. If not found, returns the value [CIRCLE].
             */
            @JvmStatic
            fun fromId(id: Int): Control.DefaultType {
                for(type in entries)
                    if(type.id == id)
                        return type

                return CIRCLE
            }
        }
    }

    /**
     * A builder class to build a control for the default ones.
     *
     * @see [ArcControl]
     * @see [CircleControl]
     * @see [CircleArcControl]
     */
    class Builder {
        private val colors: ColorsScheme = ColorsScheme(Color.RED, Color.WHITE)
        private var type: Control.DefaultType = Control.DefaultType.CIRCLE
        private var directionType: JoystickView.DirectionType = JoystickView.DirectionType.COMPLETE
        private var invalidRadius: Float = 70f

        // for arc type
        private var arcStrokeWidth: Float = 13f
        private var arcSweepAngle: Float = 90f

        // for circle type
        private var circleRadiusRatio: Float = 0.25f

        fun primaryColor(@ColorInt color: Int): Builder {
            colors.primary = color
            return this
        }

        fun accentColor(@ColorInt color: Int): Builder {
            colors.accent = color
            return this
        }

        fun colors(@ColorInt primary: Int, @ColorInt accent: Int): Builder {
            return primaryColor(primary)
                .accentColor(accent)
        }

        fun colors(scheme: ColorsScheme): Builder {
            return colors(scheme.primary, scheme.accent)
        }

        fun invalidRadius(radius: Float): Builder {
            invalidRadius = radius
            return this
        }

        fun invalidRadius(radius: Double): Builder = invalidRadius(radius.toFloat())

        fun arcStrokeWidth(width: Float): Builder {
            arcStrokeWidth = ArcControlDrawer.getStrokeWidth(width)
            return this
        }

        fun arcStrokeWidth(width: Double) = arcStrokeWidth(width.toFloat())

        fun arcStrokeWidth(width: Int) = arcStrokeWidth(width.toFloat())

        fun arcSweepAngle(angle: Float): Builder {
            arcSweepAngle = ArcControlDrawer.getSweepAngle(angle)
            return this
        }

        fun arcSweepAngle(angle: Double) = arcSweepAngle(angle.toFloat())

        fun arcSweepAngle(angle: Int) = arcSweepAngle(angle.toFloat())

        fun circleRadiusRatio(ratio: Float): Builder {
            circleRadiusRatio = CircleControlDrawer.getRadiusRatio(ratio)
            return this
        }

        fun circleRadiusRatio(ratio: Double) = circleRadiusRatio(ratio.toFloat())

        fun type(type: Control.DefaultType): Builder {
            this.type = type
            return this
        }

        fun directionType(type: JoystickView.DirectionType): Builder {
            this.directionType = type
            return this
        }

        fun build(): Control {
            return when (type) {
                Control.DefaultType.ARC -> ArcControl(
                    colors,
                    invalidRadius,
                    directionType,
                    arcStrokeWidth,
                    arcSweepAngle
                )

                Control.DefaultType.CIRCLE_ARC -> CircleArcControl(
                    colors,
                    invalidRadius,
                    directionType,
                    arcStrokeWidth,
                    arcSweepAngle,
                    circleRadiusRatio
                )

                Control.DefaultType.CIRCLE -> CircleControl(
                    colors,
                    invalidRadius,
                    directionType,
                    circleRadiusRatio
                )
            }
        }
    }

    /**
     * Gets the direction to which the control is pointing.
     * It is based on the [anglePosition] value, but if [distanceFromCenter] is less than [invalidRadius], the
     * direction is considered as [Direction.NONE].
     *
     * @return A [Direction] enum representing the direction.
     *
     * If [directionType] is [DirectionType.SIMPLE] possible values are:
     * [Direction.NONE], [Direction.LEFT], [Direction.RIGHT],
     * [Direction.UP] and [Direction.DOWN].
     *
     * Otherwise, possible values are all [Direction] enum entries.
     */

    open val direction: Direction
        get() {
        if (distanceFromCenter <= invalidRadius)
            return Direction.NONE

        val angleDegrees = Math.toDegrees(anglePosition)

        if(directionType == DirectionType.COMPLETE)
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
     * @return A value in the range from 0 to 2PI radians.
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
     * @throws InvalidControlPositionException If any of the position coordinates is negative.
     */
    @Throws(InvalidControlPositionException::class)
    protected fun validatePositionValues() {
        if(mPosition.x < 0 || mPosition.y < 0)
            throw InvalidControlPositionException()
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
     * @throws InvalidControlPositionException If any of the position coordinates is negative.
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
     * @param position The new position to be assigned.
     * @throws InvalidControlPositionException If any of the position coordinates is negative.
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
     * @param x The x coordinate to be assigned.
     * @param y The y coordinate to be assigned.
     *
     * @throws InvalidControlPositionException If any of the position coordinates is negative.
     */
    @Throws(InvalidControlPositionException::class)
    fun setPosition(x: Float, y: Float) {
        mPosition.set(x, y)
        validatePositionLimits()
        validatePositionValues()
    }

    /**
     * Sets the current position to center.
     * @throws InvalidControlPositionException If any of the position coordinates is negative.
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