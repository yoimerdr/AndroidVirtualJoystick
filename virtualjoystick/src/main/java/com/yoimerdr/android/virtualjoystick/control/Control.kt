package com.yoimerdr.android.virtualjoystick.control

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.yoimerdr.android.virtualjoystick.drawer.shapes.arc.ArcDrawer
import com.yoimerdr.android.virtualjoystick.drawer.core.ControlDrawer
import com.yoimerdr.android.virtualjoystick.drawer.core.ConfigurableDrawer
import com.yoimerdr.android.virtualjoystick.drawer.drawable.DrawableDrawer
import com.yoimerdr.android.virtualjoystick.drawer.shapes.arc.CircleArcDrawer
import com.yoimerdr.android.virtualjoystick.drawer.shapes.path.WedgeDrawer
import com.yoimerdr.android.virtualjoystick.drawer.shapes.circle.CircleDrawer
import com.yoimerdr.android.virtualjoystick.drawer.core.DrawerRadius
import com.yoimerdr.android.virtualjoystick.drawer.core.ColorfulProperties
import com.yoimerdr.android.virtualjoystick.drawer.core.DrawerProperties
import com.yoimerdr.android.virtualjoystick.drawer.shapes.path.PathDrawer
import com.yoimerdr.android.virtualjoystick.exceptions.LowerNumberException
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.position.FixedPosition
import com.yoimerdr.android.virtualjoystick.geometry.position.ImmutablePosition
import com.yoimerdr.android.virtualjoystick.geometry.position.MutablePosition
import com.yoimerdr.android.virtualjoystick.geometry.Plane
import com.yoimerdr.android.virtualjoystick.geometry.Plane.SQRT_2
import com.yoimerdr.android.virtualjoystick.geometry.position.Position
import com.yoimerdr.android.virtualjoystick.geometry.size.ImmutableSize
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme
import com.yoimerdr.android.virtualjoystick.extensions.firstOrdinal
import com.yoimerdr.android.virtualjoystick.extensions.requirePositive
import kotlin.math.min

/**
 * Represents a virtual joystick control.
 *
 * Custom control must inherit from this class.
 *
 * @param invalidRadius The radius within which the control is considered inactive.
 * @param directionType The type of direction the control supports.
 */
abstract class Control(
    @FloatRange(
        from = 0.0,
        fromInclusive = false
    )
    invalidRadius: Float,
    /**
     * The control directions type.
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
     * Cache for the direction value.
     * */
    private var mDirection: Direction? = null

    /**
     * Cache for the distance value.
     * */
    private var mDistance: Float? = null

    /**
     * Cache for the angle value.
     * */
    private var mAngle: Double? = null

    /**
     * Distance from the center at which control is not active.
     *
     * This means that the control is in a dead zone, so the [direction] will always be
     * [Direction.NONE] and the [magnitude] will be 0.
     * @see [Control.isActive]
     */
    @FloatRange(
        from = 0.0,
        fromInclusive = false
    )
    var invalidRadius: Float = invalidRadius.requirePositive()
        set(
            @FloatRange(
                from = 0.0,
                fromInclusive = false
            )
            value,
        ) {
            field = value.requirePositive()
        }

    /**
     * The control drawer.
     *
     * Must be initialized in classes that inherit from [Control].
     */
    abstract var drawer: ControlDrawer

    /**
     * The possibles directions of the control.
     */
    enum class Direction(val quadrant: Int) {
        RIGHT(1),
        DOWN_RIGHT(2),
        DOWN(3),
        DOWN_LEFT(4),
        LEFT(5),
        UP_LEFT(6),
        UP(7),
        UP_RIGHT(8),
        NONE(0);

        companion object {
            @JvmStatic
            infix fun Direction.quadrant(quadrantType: Plane.MaxQuadrants): Int {
                if (quadrantType == Plane.MaxQuadrants.EIGHT)
                    return quadrant

                return when (this) {
                    RIGHT -> 1
                    DOWN, DOWN_RIGHT, DOWN_LEFT -> 2
                    LEFT -> 3
                    UP, UP_RIGHT, UP_LEFT -> 4
                    else -> 0
                }
            }

            @JvmStatic
            infix fun Direction.quadrant(directionType: DirectionType): Int {
                val type = if (directionType == DirectionType.COMPLETE)
                    Plane.MaxQuadrants.EIGHT
                else Plane.MaxQuadrants.FOUR

                return quadrant(type)
            }

            @JvmStatic
            infix fun Direction.direction(type: DirectionType): Direction {
                if (type == DirectionType.SIMPLE)
                    return when (this) {
                        UP, UP_RIGHT, UP_LEFT -> UP
                        DOWN, DOWN_RIGHT, DOWN_LEFT -> DOWN
                        else -> this
                    }
                return this
            }

            @JvmStatic
            @JvmOverloads
            fun fromQuadrant(
                quadrant: Int,
                quadrantType: Plane.MaxQuadrants = Plane.MaxQuadrants.EIGHT,
            ): Direction {
                if (quadrantType == Plane.MaxQuadrants.EIGHT)
                    return when (quadrant) {
                        1 -> RIGHT
                        2 -> DOWN_RIGHT
                        3 -> DOWN
                        4 -> DOWN_LEFT
                        5 -> LEFT
                        6 -> UP_LEFT
                        7 -> UP
                        8 -> UP_RIGHT
                        else -> NONE
                    }
                return when (quadrant) {
                    1 -> RIGHT
                    2 -> DOWN
                    3 -> LEFT
                    4 -> UP
                    else -> NONE
                }
            }
        }
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

    /**
     * The possible types of drawers that can be built using [DrawerBuilder]
     * */
    enum class DrawerType {
        /**
         * Mark the control drawer is for a circle shape.
         *
         * @see [CircleDrawer]
         * */
        CIRCLE,

        /**
         * Mark the control drawer is for an arc shape.
         *
         * @see [ArcDrawer]
         * */
        ARC,

        /**
         * Mark the control drawer is for a circle arc shape.
         *
         * @see [CircleArcDrawer]
         * */
        CIRCLE_ARC,

        /**
         * Mark the control drawer is for a wedge shape.
         *
         * @see [WedgeDrawer]
         * */
        WEDGE;

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
     * @see [ArcDrawer]
     * @see [CircleDrawer]
     * @see [CircleArcDrawer]
     * @see [WedgeDrawer]
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
        private var circleRadiusRatio: Float? = null

        // for path types
        private var pathStrictColor = false

        // shared
        private var radius: DrawerRadius = DrawerRadius.Ratio(0.25f)

        companion object {

            /**
             * Creates a [DrawerBuilder] from an existing [DrawerProperties].
             *
             * The properties supported are:
             * - [ColorfulProperties.colors]
             * - [ArcDrawer.ArcProperties.strokeWidth]
             * - [ArcDrawer.ArcProperties.sweepAngle]
             * - [CircleArcDrawer.CircleArcProperties.isBounded]
             * - [CircleDrawer.CircleProperties.isBounded]
             * - [CircleDrawer.CircleProperties.radius]
             * - [DrawableDrawer.DrawableProperties.isBounded]
             * - [PathDrawer.PathProperties.isStrictColor]
             * - [WedgeDrawer.WedgeProperties.radius]
             * */
            @JvmStatic
            fun from(properties: DrawerProperties): DrawerBuilder {
                return DrawerBuilder().apply {
                    if (properties is ColorfulProperties)
                        colors.set(properties.colors)

                    isBounded = when (properties) {
                        is CircleArcDrawer.CircleArcProperties -> properties.isBounded
                        is CircleDrawer.CircleProperties -> properties.isBounded
                        is DrawableDrawer.DrawableProperties -> properties.isBounded
                        else -> isBounded
                    }

                    if (properties is PathDrawer.PathProperties)
                        pathStrictColor = properties.isStrictColor

                    when (properties) {
                        is CircleArcDrawer.CircleArcProperties -> {
                            radius = properties.circleProperties.radius
                        }

                        is ArcDrawer.ArcProperties -> {
                            arcStrokeWidth = properties.strokeWidth
                            arcSweepAngle = properties.sweepAngle
                        }

                        is CircleDrawer.CircleProperties -> {
                            radius = properties.radius
                        }

                        is WedgeDrawer.WedgeProperties -> {
                            radius = properties.radius
                        }
                    }
                }
            }

            /**
             * Creates a [DrawerBuilder] from an existing [ControlDrawer].
             * */
            @JvmStatic
            fun from(drawer: ControlDrawer): DrawerBuilder {
                return if (drawer is ConfigurableDrawer)
                    from(drawer.properties)
                else DrawerBuilder()
            }

            /**
             * Creates a [DrawerBuilder] from an existing [Control].
             * */
            @JvmStatic
            fun from(control: Control): DrawerBuilder {
                return from(control.drawer)
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
            arcStrokeWidth = ArcDrawer.getStrokeWidth(width)
            return this
        }

        fun arcStrokeWidth(width: Double) = arcStrokeWidth(width.toFloat())

        fun arcStrokeWidth(width: Int) = arcStrokeWidth(width.toFloat())

        fun arcSweepAngle(angle: Float): DrawerBuilder {
            arcSweepAngle = ArcDrawer.getSweepAngle(angle)
            return this
        }

        fun arcSweepAngle(angle: Double) = arcSweepAngle(angle.toFloat())

        fun arcSweepAngle(angle: Int) = arcSweepAngle(angle.toFloat())

        fun circleRadiusRatio(ratio: Float): DrawerBuilder {
            circleRadiusRatio = DrawerRadius.Ratio.getValidRatio(ratio)
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

        fun circleRadius(
            radius: DrawerRadius,
        ): DrawerBuilder {
            circleRadius = null
            circleRadiusRatio = null
            this.radius = radius
            return this
        }

        /**
         * Marks whether the path drawer can change the color
         *
         * @see [PathDrawer.PathProperties.isStrictColor]
         * @see [WedgeDrawer.WedgeProperties.isStrictColor]
         * */
        fun pathStrictColor(isStrict: Boolean): DrawerBuilder {
            pathStrictColor = isStrict
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
            val radius = if (circleRadius != null) {
                if (circleRadius!! > 0f)
                    DrawerRadius.Fixed(circleRadius!!)
                else DrawerRadius.Zero
            } else if (circleRadiusRatio != null) {
                if (circleRadiusRatio!! > 0f)
                    DrawerRadius.Ratio(circleRadiusRatio!!)
                else DrawerRadius.Zero
            } else this.radius

            return when (type) {
                DrawerType.ARC -> ArcDrawer(
                    colors,
                    arcStrokeWidth,
                    arcSweepAngle,
                    isBounded
                )

                DrawerType.CIRCLE_ARC -> CircleArcDrawer(
                    colors,
                    arcStrokeWidth,
                    arcSweepAngle,
                    radius,
                    isBounded
                )

                DrawerType.CIRCLE -> CircleDrawer(
                    colors,
                    radius,
                    isBounded
                )

                DrawerType.WEDGE -> WedgeDrawer(
                    colors.primary,
                    pathStrictColor,
                    radius
                )
            }
        }
    }


    /**
     * A builder class to build a simple control.
     *
     * @see [SimpleControl]
     * @see [DrawerBuilder]
     */
    class Builder {

        private var directionType: DirectionType = DirectionType.COMPLETE
        private var invalidRadius: Float = 70f
        var drawer = DrawerBuilder()
            private set

        companion object {

            /**
             * Creates a [Builder] from an existing [ControlDrawer].
             *
             * @see [DrawerBuilder.from]
             * */
            @JvmStatic
            fun from(drawer: ControlDrawer): Builder {
                return Builder().apply {
                    this.drawer = DrawerBuilder.from(drawer)
                }
            }

            /**
             * Creates a [Builder] from an existing [Control].
             *
             * @see [DrawerBuilder.from]
             * */
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
     * Calculates the direction to which the control is pointing.
     *
     * * It is based on the [angle] value, but if [distance] is less than [invalidRadius], the
     * direction is considered as [Direction.NONE].
     *
     * * The directions available depend on the [directionType] and [invalidRadius].
     */
    open val direction: Direction
        get() {
            if (mDirection == null)
                mDirection = directionFrom(mPosition)

            return mDirection!!
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
     * Calculates the centered position between the control position and center.
     */
    open val centeredPosition: ImmutablePosition
        get() = FixedPosition(deltaX(), deltaY())

    /**
     * Calculates the normalized device coordinates (NDC) position of the control.
     */
    open val ndcPosition: ImmutablePosition
        get() = FixedPosition(deltaX() / radius, deltaY() / radius)

    /**
     * Calculates the parametric position of current position in the view circle.
     */
    open val parametricPosition: ImmutablePosition
        get() = mViewCircle.parametricPositionOf(angle)

    /**
     * Calculates the distance between current position and center.
     */
    val distance: Float
        get() {
            if (mDistance == null)
                mDistance = mViewCircle.distanceTo(mPosition)
            return mDistance!!
        }

    /**
     * Calculates the squared distance between current position and center.
     * */
    val squaredDistance: Float
        get() = mViewCircle.squaredDistanceTo(mPosition)

    /**
     * Calculates the angle (clockwise) formed from the current position and center.
     *
     * @return A value in the range from 0 to 2PI radians.
     */
    val angle: Double
        @FloatRange(from = 0.0, to = Circle.RADIAN_SPIN)
        get() {
            if (mAngle == null)
                mAngle = mViewCircle.angleTo(mPosition)
            return mAngle!!
        }

    /**
     * Gets the radius of the view where the control is used.
     */
    val radius: Double get() = mViewCircle.radius

    /**
     * Calculates the magnitude of the control position [position] relative to the [center].
     *
     * The calculated magnitude ignores the [invalidRadius] value.
     *
     * @return A value in the range from 0.0 to 1.0
     * */
    val magnitude: Double
        @FloatRange(
            from = 0.0,
            to = 1.0
        )
        get() {
            val distance = this.distance

            return (distance / radius).coerceIn(0.0, 1.0)
        }

    /**
     * Checks whether the control is in the active zone
     * */
    open val isActive: Boolean
        get() = distance > invalidRadius

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
     * Checks if [distance] is greater than the [radius].
     * If so, changes the position to the [parametricPosition].
     */
    private fun validatePositionLimits() {
        if (distance > radius)
            mPosition.set(parametricPosition)
    }

    /**
     * Invalidates the control cached values
     * */
    @CallSuper
    protected open fun invalidate() {
        mDistance = null
        mAngle = null
        mDirection = null
    }

    /**
     * Called (or call it) when the size of the view changes.
     *
     * It updates the drawer position and center.
     * @param size The size of the view.
     * @throws LowerNumberException If any of the position coordinates is negative.
     *
     */
    @Deprecated(
        "This method is deprecated and never used by JoystickView. Override the protected onBoundsChange instead.",
    )
    @Throws(LowerNumberException::class)
    open fun onSizeChanged(size: ImmutableSize) {
    }

    /**
     * Bounds the control center and the radius.
     *
     * @param bounds The bounds of the view.
     */
    @CallSuper
    @Throws(LowerNumberException::class)
    protected open fun onBoundsChange(bounds: Rect) {
        bounds.apply {
            (min(width(), height()) * 0.5).also {
                mViewCircle.radius = it

                val x = exactCenterX()
                val y = exactCenterY()
                if (mCenter.x != x || mCenter.y != y) {
                    mCenter.set(x, y)
                    invalidate()
                    toCenter()
                }
            }
        }
    }

    /**
     * Internal method to set the bounds of the control.
     * */
    internal fun setBounds(bounds: Rect) {
        onBoundsChange(bounds)
    }


    /**
     * Called when the control is ready to draw.
     *
     * @param canvas The canvas on which the control will be drawn
     */
    open fun onDraw(canvas: Canvas) {
        if (canDraw())
            drawer.draw(canvas, this)
    }

    open fun canDraw(): Boolean {
        return (drawer as? ConfigurableDrawer)?.canDraw(this) ?: true
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
        if (mPosition.x != x || mPosition.y != y) {
            mPosition.set(x, y)
            invalidate()
            validatePositionLimits()
            validatePositionValues()
        }
    }

    /**
     * Calculates the direction from the given position to the control center.
     *
     * The directions available depend on the [directionType] and [invalidRadius].
     *
     * @param position The position from which the direction will be calculated.
     */
    fun directionFrom(position: ImmutablePosition): Direction {
        val distance: Float = if (position == mPosition)
            this.distance
        else mViewCircle.distanceTo(position)

        if (distance <= invalidRadius)
            return Direction.NONE

        val angleDegrees = Math.toDegrees(
            if (position == mPosition)
                this.angle
            else mViewCircle.angleTo(position)
        )

        val quadrantType = if (directionType == DirectionType.COMPLETE) Plane.MaxQuadrants.EIGHT
        else Plane.MaxQuadrants.FOUR

        val quadrant = Plane.quadrantOf(angleDegrees, quadrantType, true)

        return Direction.fromQuadrant(quadrant, quadrantType)
    }

    /**
     * Calculates the position from the given direction.
     *
     * @param direction The direction from which the position will be calculated.
     * @param magnitude The magnitude for the position.
     */
    @JvmOverloads
    fun positionFrom(direction: Direction, magnitude: Float = 1f): ImmutablePosition {
        val center = center
        var radius = radius
        val force = magnitude.coerceIn(0f, 1f)

        if (force <= 0f)
            return center

        radius *= force

        return when (direction) {
            Direction.NONE -> center
            Direction.UP -> FixedPosition(center.x, center.y - radius)
            Direction.DOWN -> FixedPosition(center.x, center.y + radius)
            Direction.LEFT -> FixedPosition(center.x - radius, center.y)
            Direction.RIGHT -> FixedPosition(center.x + radius, center.y)
            else -> {
                radius /= SQRT_2
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

    /**
     * Release cache properties of a drawer, or of a custom control.
     *
     * @see [ConfigurableDrawer.release]
     * */
    @CallSuper
    open fun release() {
        (drawer as? ConfigurableDrawer)?.release()
    }
}