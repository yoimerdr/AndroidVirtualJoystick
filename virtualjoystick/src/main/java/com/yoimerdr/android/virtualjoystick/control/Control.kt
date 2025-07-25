package com.yoimerdr.android.virtualjoystick.control

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.yoimerdr.android.virtualjoystick.control.Control.Direction
import com.yoimerdr.android.virtualjoystick.control.drawer.arc.ArcControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.arc.RatioCircleArcControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.ColorfulControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.ControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.arc.RadiusCircleArcControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.circle.RadiusCircleControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.circle.RatioCircleControlDrawer
import com.yoimerdr.android.virtualjoystick.exceptions.LowerNumberException
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.position.FixedPosition
import com.yoimerdr.android.virtualjoystick.geometry.position.ImmutablePosition
import com.yoimerdr.android.virtualjoystick.geometry.position.MutablePosition
import com.yoimerdr.android.virtualjoystick.geometry.Plane
import com.yoimerdr.android.virtualjoystick.geometry.position.Position
import com.yoimerdr.android.virtualjoystick.geometry.size.ImmutableSize
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme
import com.yoimerdr.android.virtualjoystick.utils.extensions.firstOrdinal
import com.yoimerdr.android.virtualjoystick.utils.extensions.requirePositive
import kotlin.math.min

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
    var directionType: DirectionType,
) {

    /**
     * The control position.
     */
    private val mPosition: MutablePosition = Position()

    /**
     * The center of the view.
     */
    private val mCenter: MutablePosition = Position()


    /**
     * Circle representing the area of the view where the control is used.
     */
    private val mViewCircle: Circle = Circle(1f, mCenter)


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
        validateInvalidRadius()
    }

    /**
     * The possibles directions of the control.
     */
    enum class Direction {
        UP,
        LEFT,
        RIGHT,
        DOWN,
        UP_RIGHT,
        UP_LEFT,
        DOWN_RIGHT,
        DOWN_LEFT,
        NONE
    }

    /**
     * The type that will determine how many directions the control will be able to return.
     */
    enum class DirectionType {
        /**
         * Determines that the joystick will be able to return all the entries of [Direction] enum.
         */
        COMPLETE,

        /**
         * Determines that the joystick will only be able to return 5 directions.
         *
         * [Direction.RIGHT], [Direction.DOWN], [Direction.LEFT], [Direction.UP] and [Direction.NONE]
         */
        SIMPLE;

        companion object {
            /**
             * @param id The id for the enum value
             * @return The enum value for the given id. If not found, returns the value [COMPLETE].
             */
            @JvmStatic
            fun fromId(id: Int): DirectionType {
                return entries.firstOrdinal(id, COMPLETE)
            }
        }
    }

    enum class DrawerType {
        CIRCLE,
        ARC,
        CIRCLE_ARC;

        companion object {
            /**
             * @param id The id for the enum value
             * @return The enum value for the given id. If not found, returns the value [CIRCLE].
             */
            @JvmStatic
            fun fromId(id: Int): DrawerType {
                return entries.firstOrdinal(id, CIRCLE)
            }
        }
    }

    /**
     * A builder class to build a drawer from some of the defaults for the control.
     *
     * @see [ArcControlDrawer]
     * @see [RatioCircleControlDrawer]
     * @see [RadiusCircleControlDrawer]
     * @see [RatioCircleArcControlDrawer]
     */
    class DrawerBuilder {
        private val colors: ColorsScheme = ColorsScheme(Color.RED, Color.WHITE)
        private var type: DrawerType = DrawerType.CIRCLE
        private var isBounded: Boolean = true

        // for arc type
        private var arcStrokeWidth: Float = 13f
        private var arcSweepAngle: Float = 90f

        // for circle type
        private var circleRadius: Float? = null
        private var circleRadiusRatio: Float? = 0.25f

        companion object {
            @JvmStatic
            fun from(drawer: ControlDrawer): DrawerBuilder {
                return DrawerBuilder().apply {
                    if (drawer is ColorfulControlDrawer)
                        colors.set(drawer.colors)
                    when (drawer) {
                        is ArcControlDrawer -> {
                            arcStrokeWidth = drawer.strokeWidth
                            arcSweepAngle = drawer.sweepAngle
                        }

                        is RatioCircleControlDrawer -> {
                            circleRadiusRatio = drawer.ratio
                            circleRadius = null
                        }

                        is RadiusCircleControlDrawer -> {
                            circleRadius = drawer.radius
                            circleRadiusRatio = null
                        }
                    }

                }
            }
        }


        fun primaryColor(@ColorInt color: Int): DrawerBuilder {
            colors.primary = color
            return this
        }

        fun accentColor(@ColorInt color: Int): DrawerBuilder {
            colors.accent = color
            return this
        }

        fun colors(@ColorInt primary: Int, @ColorInt accent: Int): DrawerBuilder {
            return primaryColor(primary)
                .accentColor(accent)
        }

        fun colors(scheme: ColorsScheme): DrawerBuilder {
            return colors(scheme.primary, scheme.accent)
        }

        fun arcStrokeWidth(width: Float): DrawerBuilder {
            arcStrokeWidth = ArcControlDrawer.getStrokeWidth(width)
            return this
        }

        fun arcStrokeWidth(width: Double) = arcStrokeWidth(width.toFloat())

        fun arcStrokeWidth(width: Int) = arcStrokeWidth(width.toFloat())

        fun arcSweepAngle(angle: Float): DrawerBuilder {
            arcSweepAngle = ArcControlDrawer.getSweepAngle(angle)
            return this
        }

        fun arcSweepAngle(angle: Double) = arcSweepAngle(angle.toFloat())

        fun arcSweepAngle(angle: Int) = arcSweepAngle(angle.toFloat())

        fun circleRadiusRatio(ratio: Float): DrawerBuilder {
            circleRadiusRatio = RatioCircleControlDrawer.getRadiusRatio(ratio)
            circleRadius = null
            return this
        }

        fun circleRadiusRatio(ratio: Double) = circleRadiusRatio(ratio.toFloat())

        fun circleRadius(
            @FloatRange(
                from = 0.0,
                fromInclusive = false
            ) radius: Float,
        ): DrawerBuilder {
            circleRadius = radius
            circleRadiusRatio = null
            return this
        }

        fun type(type: DrawerType): DrawerBuilder {
            this.type = type
            return this
        }

        fun bounded(bounded: Boolean): DrawerBuilder {
            isBounded = bounded
            return this
        }

        fun build(): ControlDrawer {
            val radiusOrRatio = if (circleRadiusRatio == null && circleRadius != null)
                circleRadius!! to true
            else (circleRadiusRatio ?: 0.25f) to false

            return when (type) {
                DrawerType.ARC -> ArcControlDrawer(
                    colors,
                    arcStrokeWidth,
                    arcSweepAngle,
                    isBounded
                )

                DrawerType.CIRCLE_ARC -> {
                    if (radiusOrRatio.second)
                        RadiusCircleArcControlDrawer(
                            colors,
                            arcStrokeWidth,
                            arcSweepAngle,
                            radiusOrRatio.first,
                            isBounded
                        )
                    else RatioCircleArcControlDrawer(
                        colors,
                        arcStrokeWidth,
                        arcSweepAngle,
                        radiusOrRatio.first,
                        isBounded
                    )
                }

                DrawerType.CIRCLE -> {
                    if (radiusOrRatio.second)
                        RadiusCircleControlDrawer(
                            colors,
                            radiusOrRatio.first,
                            isBounded
                        )
                    else RatioCircleControlDrawer(
                        colors,
                        radiusOrRatio.first,
                        isBounded
                    )
                }
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

        private var directionType: DirectionType = DirectionType.COMPLETE
        private var invalidRadius: Float = 70f
        var drawer = DrawerBuilder()
            private set

        companion object {
            @JvmStatic
            fun from(control: Control): Builder {
                return Builder().apply {
                    drawer = DrawerBuilder.from(control.drawer)
                    directionType = control.directionType
                    invalidRadius = control.invalidRadius
                }
            }
        }

        fun directionType(type: DirectionType): Builder {
            this.directionType = type
            return this
        }

        fun invalidRadius(radius: Float): Builder {
            invalidRadius = radius
            return this
        }

        fun invalidRadius(radius: Double): Builder = invalidRadius(radius.toFloat())

        fun build(): Control {
            return SimpleControl(
                drawer.build(),
                invalidRadius,
                directionType
            )
        }
    }

    /**
     * Gets the direction to which the control is pointing.
     * It is based on the [angle] value, but if [distance] is less than [invalidRadius], the
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
        get() = directionFrom(mPosition)

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

    open val centeredPosition: ImmutablePosition
        get() = FixedPosition(deltaX(), deltaY())

    open val ndcPosition: ImmutablePosition
        get() = FixedPosition(deltaX() / radius, deltaY() / radius)

    /**
     * Gets the parametric position of current position in the view circle.
     *
     * @return A new instance of the parametric position.
     */

    open val parametricPosition: ImmutablePosition
        get() = mViewCircle.parametricPositionOf(angle)

    /**
     * Calculates the distance between current position and center.
     * @return The calculated distance.
     */
    val distance: Float get() = mViewCircle.distanceTo(mPosition)

    /**
     * Calculates the angle (clockwise) formed from the current position and center.
     * @return A value in the range from 0 to 2PI radians.
     */
    val angle: Double get() = mViewCircle.angleTo(mPosition)

    /**
     * Gets the radius of the view where the control is used.
     */
    val radius: Double get() = mViewCircle.radius

    /**
     * Validates the control position values.
     *
     * @throws LowerNumberException If any of the position coordinates is negative.
     */
    @Throws(LowerNumberException::class)
    protected fun validatePositionValues() {
        mPosition.x.requirePositive()
        mPosition.y.requirePositive()
    }

    /**
     * Validates the [invalidRadius] value.
     *
     * @throws IllegalArgumentException If [invalidRadius] value is negative.
     */
    @Throws(IllegalArgumentException::class)
    private fun validateInvalidRadius() {
        if (invalidRadius < 0)
            throw IllegalArgumentException("Invalid radius value must be positive.")
    }

    /**
     * Checks if [distance] is greater than the [radius].
     * If so, changes the position to the [parametricPosition].
     */
    private fun validatePositionLimits() {
        if (distance > radius)
            mPosition.set(parametricPosition)
    }

    /**
     * Called (or call it) when the size of the view changes.
     *
     * It updates the drawer position and center.
     * @param size The size of the view.
     * @throws LowerNumberException If any of the position coordinates is negative.
     */
    @Throws(LowerNumberException::class)
    open fun onSizeChanged(size: ImmutableSize) {
        onSizeChanged(
            Rect(
                0, 0,
                size.width,
                size.height
            )
        )
    }

    open fun onSizeChanged(rect: Rect) {
        rect.apply {
            (min(width(), height()) / 2f).also {
                mCenter.set(centerX().toFloat(), centerY().toFloat())
                mViewCircle.radius = it.toDouble()
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
     * @throws LowerNumberException If any of the position coordinates is negative.
     */
    @Throws(LowerNumberException::class)
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
     * @throws LowerNumberException If any of the position coordinates is negative.
     */
    @Throws(LowerNumberException::class)
    fun setPosition(x: Float, y: Float) {
        mPosition.set(x, y)
        validatePositionLimits()
        validatePositionValues()
    }

    /**
     * Calculates the direction from the given position to the control center.
     *
     * * The directions available depend on the [directionType] and [invalidRadius].
     *
     * @param position The position from which the direction will be calculated.
     */
    fun directionFrom(position: ImmutablePosition): Direction {
        val distance = mViewCircle.distanceTo(position)

        if (distance <= invalidRadius)
            return Direction.NONE

        val angleDegrees = Math.toDegrees(angle)

        if (directionType == DirectionType.COMPLETE)
            return when (Plane.quadrantOf(angleDegrees, Plane.MaxQuadrants.EIGHT, true)) {
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

        return when (Plane.quadrantOf(angleDegrees, true)) {
            1 -> Direction.RIGHT
            2 -> Direction.DOWN
            3 -> Direction.LEFT
            4 -> Direction.UP
            else -> Direction.NONE
        }
    }

    /**
     * Calculates the position from the given direction.
     *
     * @param direction The direction from which the position will be calculated.
     */
    fun positionFrom(direction: Direction): ImmutablePosition {
        val center = center
        var radius = radius

        return when (direction) {
            Direction.NONE -> center
            Direction.UP -> FixedPosition(center.x, center.y - radius)
            Direction.DOWN -> FixedPosition(center.x, center.y + radius)
            Direction.LEFT -> FixedPosition(center.x - radius, center.y)
            Direction.RIGHT -> FixedPosition(center.x + radius, center.y)
            else -> {
                radius /= kotlin.math.sqrt(2.0f)
                when (direction) {
                    Direction.UP_LEFT -> FixedPosition(
                        center.x - radius,
                        center.y - radius
                    )

                    Direction.UP_RIGHT -> FixedPosition(
                        center.x + radius,
                        center.y - radius
                    )

                    Direction.DOWN_LEFT -> FixedPosition(
                        center.x - radius,
                        center.y + radius
                    )

                    Direction.DOWN_RIGHT -> FixedPosition(
                        center.x + radius,
                        center.y + radius
                    )

                    else -> center
                }
            }
        }
    }

    /**
     * Sets the current position to center.
     * @throws LowerNumberException If any of the position coordinates is negative.
     */
    @Throws(LowerNumberException::class)
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
    fun deltaX(): Float = mPosition.deltaX(mCenter)

    /**
     * Calculates the difference in the y-coordinate between the current position and the center.
     * @return The calculated difference.
     */
    fun deltaY(): Float = mPosition.deltaY(mCenter)
}