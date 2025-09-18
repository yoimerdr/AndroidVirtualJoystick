package com.yoimerdr.android.virtualjoystick.drawer.shapes.arc

import android.graphics.Canvas
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.drawer.core.ControlDrawer
import com.yoimerdr.android.virtualjoystick.drawer.shapes.circle.CircleDrawer.CircleProperties
import com.yoimerdr.android.virtualjoystick.drawer.core.DrawerRadius
import com.yoimerdr.android.virtualjoystick.drawer.shapes.circle.CircleDrawer
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * A [ControlDrawer] that draws a circle accompanied by an arc.
 */
open class CircleArcDrawer protected constructor(
    /**
     * The companion drawer
     * */
    circleDrawer: ControlDrawer?,
    /**
     * The drawer properties.
     */
    override val properties: CircleArcProperties,
) : ArcDrawer(properties) {

    /**
     * The drawer properties.
     * */
    constructor(
        properties: CircleArcProperties,
    ) : this(
        null,
        properties
    )

    /**
     * @param colors The colors for the drawer.
     * @param strokeWidth The stroke width of the paint.
     * @param sweepAngle The arc sweep angle.
     * @param radius The circle radius.
     * @param isBounded Indicates whether the maximum distance is bounded.
     */
    @JvmOverloads
    constructor(
        colors: ColorsScheme,
        strokeWidth: Float,
        sweepAngle: Float,
        radius: DrawerRadius,
        isBounded: Boolean = true,
    ) : this(
        CircleArcProperties(
            colors,
            strokeWidth,
            sweepAngle,
            CircleProperties(colors, radius),
            isBounded
        )
    )

    /**
     * @param color The color for the drawer.
     * @param strokeWidth The stroke width of the paint.
     * @param sweepAngle The arc sweep angle.
     * @param radius The circle radius.
     * @param isBounded Indicates whether the maximum distance is bounded.
     */
    @JvmOverloads
    constructor(
        color: Int,
        strokeWidth: Float,
        sweepAngle: Float,
        radius: DrawerRadius,
        isBounded: Boolean = true,
    ) : this(
        ColorsScheme(color),
        strokeWidth,
        sweepAngle,
        radius,
        isBounded
    )

    /**
     * The custom circle drawer.
     *
     * @param rootProperties The root properties.
     * */
    protected open class CircleDrawer(
        protected val rootProperties: CircleArcProperties,
    ) : com.yoimerdr.android.virtualjoystick.drawer.shapes.circle.CircleDrawer(
        rootProperties.circleProperties
    ) {
        override fun getMaxDistance(control: Control): Double {
            val distance = super.getMaxDistance(control)
            return if (rootProperties.isBounded)
                distance - rootProperties.strokeWidth * 2
            else distance
        }
    }

    /**
     * @param colors The colors for the drawer.
     * @param strokeWidth The stroke width of the paint.
     * @param sweepAngle The arc sweep angle.
     * @param circleProperties The circle properties.
     * @param isBounded Indicates whether the maximum distance is bounded.
     */
    open class CircleArcProperties @JvmOverloads constructor(
        colors: ColorsScheme,
        strokeWidth: Float,
        sweepAngle: Float,
        val circleProperties: CircleProperties,
        isBounded: Boolean = true,
    ) : ArcProperties(colors, strokeWidth, sweepAngle, isBounded)

    companion object {

        /**
         * Creates a [CircleArcDrawer] with a radius based on a ratio.
         * @param colors The colors for the drawer.
         * @param strokeWidth The stroke width of the paint.
         * @param sweepAngle The arc sweep angle.
         * @param ratio The ratio value.
         * @param isBounded Indicates whether the maximum distance is bounded.
         *
         * @see [DrawerRadius.Ratio]
         * */
        @JvmOverloads
        @JvmStatic
        fun withRatio(
            colors: ColorsScheme,
            strokeWidth: Float,
            sweepAngle: Float,
            ratio: Float,
            isBounded: Boolean = true,
        ) = CircleArcDrawer(
            colors,
            strokeWidth,
            sweepAngle,
            DrawerRadius.Ratio(ratio),
            isBounded
        )
    }

    private var mCircleDrawer: ControlDrawer? = circleDrawer

    /**
     * The companion drawer.
     *
     * It should be a [CircleDrawer], but it can be any other [ControlDrawer].
     * */
    protected open var circleDrawer: ControlDrawer
        get() {
            if (mCircleDrawer == null)
                mCircleDrawer = CircleDrawer(properties)
            return mCircleDrawer!!
        }
        set(value) {
            mCircleDrawer = value
        }

    /**
     * Gets the circle radius.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getCircleRadius(control: Control): Double =
        properties.circleProperties.radius.getValue(control)

    override fun getDistance(control: Control): Double {
        val max = super.getDistance(control)

        return (control.distance + getCircleRadius(control))
            .coerceAtMost(max)
    }

    override fun onDraw(canvas: Canvas, control: Control) {
        if (!control.isInCenter())
            drawShapes(canvas, control)

        circleDrawer.draw(canvas, control)
    }
}