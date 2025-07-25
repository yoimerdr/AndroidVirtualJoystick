package com.yoimerdr.android.virtualjoystick.control.drawer

import android.graphics.Canvas
import androidx.annotation.ColorInt
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.control.drawer.CircleControlDrawer.CircleProperties
import com.yoimerdr.android.virtualjoystick.control.drawer.CircleControlDrawer.Companion.MAX_RADIUS_RATIO
import com.yoimerdr.android.virtualjoystick.control.drawer.CircleControlDrawer.Companion.MIN_RADIUS_RATIO
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * A [ControlDrawer] that draws a circle accompanied by an arc.
 */
open class CircleArcControlDrawer(
    /**
     * The drawer properties.
     */
    private val properties: CircleArcProperties,
) : ArcControlDrawer(properties) {

    /**
     * @param colors The colors for the drawer.
     * @param strokeWidth The stroke width of the paint.
     * @param sweepAngle The arc sweep angle.
     * @param ratio The ratio value for the circle radius length.
     */
    @JvmOverloads
    constructor(
        colors: ColorsScheme,
        strokeWidth: Float,
        sweepAngle: Float,
        ratio: Float,
        isBounded: Boolean = true,
    ) : this(
        CircleArcProperties(
            colors,
            strokeWidth,
            sweepAngle,
            CircleProperties(colors, ratio),
            isBounded
        )
    )

    /**
     * @param color The unique initial color for the drawer.
     * @param strokeWidth The stroke width of the paint.
     * @param sweepAngle The arc sweep angle.
     * @param ratio The ratio value for the circle radius length.
     */
    constructor(
        @ColorInt color: Int,
        strokeWidth: Float,
        sweepAngle: Float,
        ratio: Float,
    ) : this(ColorsScheme(color), strokeWidth, sweepAngle, ratio)

    var ratio: Float
        /**
         * Gets the circle radius ratio.
         */
        get() = properties.circleProperties.ratio
        /**
         * Sets the circle radius ratio.
         * @param ratio The new circle radius ratio. Must be a value in the range from [MIN_RADIUS_RATIO] to [MAX_RADIUS_RATIO]
         */
        set(ratio) {
            properties.circleProperties.ratio = CircleControlDrawer.getRadiusRatio(ratio)
        }

    protected open class CircleDrawer(private val properties: CircleArcProperties) :
        CircleControlDrawer(properties.circleProperties) {
        override fun getMaxDistance(control: Control): Double {
            val distance = super.getMaxDistance(control)
            return if (properties.isBounded)
                distance - properties.strokeWidth * 2
            else distance
        }
    }

    /**
     * The circle drawer.
     */
    protected open val circleDrawer: ControlDrawer = CircleDrawer(properties)

    open class CircleArcProperties @JvmOverloads constructor(
        colors: ColorsScheme,
        strokeWidth: Float,
        sweepAngle: Float,
        val circleProperties: CircleProperties,
        isBounded: Boolean = true,
    ) : ArcProperties(colors, strokeWidth, sweepAngle, isBounded)


    override fun getDistance(control: Control): Double {
        val max = super.getDistance(control)

        return (control.distance + (control.radius * ratio))
            .coerceAtMost(max)
    }

    override fun draw(canvas: Canvas, control: Control) {
        if (!control.isInCenter())
            drawShapes(canvas, control)

        circleDrawer.draw(canvas, control)
    }
}