package com.yoimerdr.android.virtualjoystick.drawer.shapes.path

import android.graphics.Canvas
import androidx.annotation.ColorInt
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.drawer.core.DrawerRadius
import com.yoimerdr.android.virtualjoystick.exceptions.LowerNumberException
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.factory.RectFFactory

/**
 * A drawer that draws a wedge.
 */
open class WedgeDrawer(
    /**
     * The wedge drawer properties.
     */
    override val properties: WedgeProperties,
) : PathDrawer(properties) {

    /**
     * @param color The primary color.
     * @param isStrictColor Indicates if the color can be modified.
     * @param radius The drawer radius.
     * @param mode The wedge drawing mode.
     */
    @JvmOverloads
    constructor(
        @ColorInt color: Int,
        isStrictColor: Boolean,
        radius: DrawerRadius,
        mode: Mode = Mode.CURVE,
    ) : this(
        WedgeProperties(
            color, isStrictColor,
            radius, mode
        )
    )

    /**
     * @param color The primary color.
     * @param radius The drawer radius.
     * @param mode The wedge drawing mode.
     */
    @JvmOverloads
    constructor(
        @ColorInt color: Int,
        radius: DrawerRadius,
        mode: Mode = Mode.CURVE,
    ) : this(
        color,
        false,
        radius,
        mode
    )

    /**
     * The wedge drawing mode.
     * */
    enum class Mode {
        /**
         * Indicates that the inner side of the wedge is drawn with a curve.
         * */
        CURVE,

        /**
         * Indicates that the inner side of the wedge is drawn with a straight line.
         * */
        STRAIGHT
    }

    /**
     * @param color The primary color.
     * @param isStrictColor Indicates if the color can be modified.
     * @param radius The drawer radius.
     * @param mode The wedge drawing mode.
     */
    open class WedgeProperties(
        @ColorInt color: Int,
        isStrictColor: Boolean,
        /**
         * The drawer radius.
         * */
        var radius: DrawerRadius,
        /**
         * The drawer mode.
         * */
        var mode: Mode = Mode.CURVE,
    ) : PathProperties(color, isStrictColor)

    companion object {

        /**
         * Creates a [WedgeDrawer] with a ratio-based radius.
         *
         * @param color The primary color.
         * @param isStrictColor Indicates if the color can be modified.
         * @param ratio The ratio value.
         * @param mode The wedge drawing mode.
         *
         * @see [DrawerRadius.Ratio]
         */
        @JvmStatic
        @JvmOverloads
        fun withRatio(
            @ColorInt color: Int,
            isStrictColor: Boolean,
            ratio: Float,
            mode: Mode = Mode.CURVE,
        ) = WedgeDrawer(
            color,
            isStrictColor,
            DrawerRadius.Ratio(ratio),
            mode
        )

        /**
         * Creates a [WedgeDrawer] with a ratio-based radius.
         *
         * @param color The primary color.
         * @param ratio The ratio value.
         * @param mode The wedge drawing mode.
         *
         * @see [DrawerRadius.Ratio]
         */
        @JvmStatic
        @JvmOverloads
        fun withRatio(
            @ColorInt color: Int,
            ratio: Float,
            mode: Mode = Mode.CURVE,
        ) = withRatio(color, false, ratio, mode)


        /**
         * Creates a [WedgeDrawer] with a radius.
         *
         * @param color The primary color.
         * @param isStrictColor Indicates if the color can be modified.
         * @param radius The radius value.
         * @param mode The wedge drawing mode.
         *
         * @see [DrawerRadius.Fixed]
         */
        @JvmStatic
        @JvmOverloads
        fun withRadius(
            @ColorInt color: Int,
            isStrictColor: Boolean,
            radius: Float,
            mode: Mode = Mode.CURVE,
        ) = WedgeDrawer(
            color,
            isStrictColor,
            DrawerRadius.Fixed(radius),
            mode
        )

        /**
         * Creates a [WedgeDrawer] with a radius.
         *
         * @param color The primary color.
         * @param radius The radius value.
         * @param mode The wedge drawing mode.
         *
         * @see [DrawerRadius.Fixed]
         */
        @JvmStatic
        @JvmOverloads
        fun withRadius(
            @ColorInt color: Int,
            radius: Float,
            mode: Mode = Mode.CURVE,
        ) = withRadius(color, false, radius, mode)
    }

    override fun updatePath(
        control: Control,
        direction: Control.Direction,
        directionType: Control.DirectionType,
    ) {
        val quadrant = getQuadrantOf(direction, directionType)
        fillWedge(
            control,
            quadrant,
            getSweepAngleOf(directionType)
        )
    }

    override fun getInnerDistance(control: Control): Double = properties.radius.getValue(control)

    /**
     * Resets and fills the path of the wedge.
     *
     * @param control The [Control] from where the drawer is used.
     * @param quadrant The current quadrant where the control is.
     * @param sweepAngle The sweep angle (degrees) for the wedge.
     */
    protected open fun fillWedge(control: Control, quadrant: Int, sweepAngle: Float) {
        if (quadrant == 0)
            return

        path.reset()

        val center = control.center
        val mode = properties.mode

        // the outer circle is required, so no handle the exception
        val outerCircle = Circle.fromImmutableCenter(getOuterDistance(control), center)
        val innerCircle = try {
            // handle exception for non inner circle (radius <= 0)
            Circle(getInnerDistance(control), outerCircle.center)
        } catch (_: LowerNumberException) {
            null
        }

        val outerOval = RectFFactory.fromCircle(outerCircle)

        val startAngle = (quadrant - 1) * sweepAngle - sweepAngle / 2.0
        var posAngle = Math.toRadians(startAngle)

        if (mode == Mode.CURVE) {
            // if not exists a inner circle, the inner position is ever the center
            (innerCircle?.parametricPositionOf(posAngle) ?: center).apply {
                path.moveTo(x, y)
            }
            outerCircle.parametricPositionOf(posAngle).apply {
                path.lineTo(x, y)
            }
        } else {
            outerCircle.parametricPositionOf(posAngle).apply {
                path.moveTo(x, y)
            }
        }
        path.arcTo(outerOval, startAngle.toFloat(), sweepAngle)

        if (mode == Mode.CURVE) {
            val innerOval = innerCircle?.let { RectFFactory.fromCircle(it) }
                ?: RectFFactory.withCenterAt(center, 0f)

            posAngle = Math.toRadians((startAngle + sweepAngle))
            outerCircle.parametricPositionOf(posAngle).apply {
                path.lineTo(x, y)
            }
            path.arcTo(innerOval, startAngle.toFloat() + sweepAngle, -sweepAngle)
        } else {
            val position = innerCircle?.parametricPositionOf(posAngle) ?: center

            posAngle = Math.toRadians((startAngle + sweepAngle))
            (innerCircle?.parametricPositionOf(posAngle) ?: center).apply {
                path.lineTo(x, y)
            }
            position.apply {
                path.lineTo(x, y)
            }
        }

        path.close()
    }

    /**
     * Gets the sweep angle for give direction type.
     *
     * @param directionType The control direction type.
     * @return 90 if [directionType] is [Control.DirectionType.SIMPLE]; Otherwise, 45
     */
    protected open fun getSweepAngleOf(directionType: Control.DirectionType): Float {
        return if (directionType == Control.DirectionType.SIMPLE) 90f
        else 45f
    }

    override fun draw(canvas: Canvas, control: Control) {
        if (!control.isActive)
            return

        super.draw(canvas, control)
    }

    override fun canDraw(control: Control): Boolean {
        return !isValid(control)
    }
}