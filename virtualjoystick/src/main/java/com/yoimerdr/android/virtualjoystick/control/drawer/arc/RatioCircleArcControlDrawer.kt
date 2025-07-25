package com.yoimerdr.android.virtualjoystick.control.drawer.arc

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.control.drawer.ControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.circle.RatioCircleControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.circle.RatioCircleControlDrawer.RatioCircleProperties
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * A [ControlDrawer] that draws a circle accompanied by an arc.
 *
 * The circle radius is based on the ratio of the control radius.
 */
open class RatioCircleArcControlDrawer(
    /**
     * The drawer properties.
     */
    private val properties: RatioCircleArcProperties,
) : BaseCircleArcControlDrawer(properties) {

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
        RatioCircleArcProperties(
            colors,
            strokeWidth,
            sweepAngle,
            RatioCircleProperties(colors, ratio),
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
        @FloatRange(
            from = RatioCircleControlDrawer.MIN_RADIUS_RATIO.toDouble(),
            to = RatioCircleControlDrawer.MAX_RADIUS_RATIO.toDouble()
        )
        get() = properties.circleProperties.ratio
        /**
         * Sets the circle radius ratio.
         * @param ratio The new circle radius ratio.
         */
        set(
            @FloatRange(
                from = RatioCircleControlDrawer.MIN_RADIUS_RATIO.toDouble(),
                to = RatioCircleControlDrawer.MAX_RADIUS_RATIO.toDouble()
            )
            ratio,
        ) {
            properties.circleProperties.ratio = RatioCircleControlDrawer.getRadiusRatio(ratio)
        }

    protected open class CircleDrawer(private val properties: RatioCircleArcProperties) :
        RatioCircleControlDrawer(properties.circleProperties) {
        override fun getMaxDistance(control: Control): Double {
            val distance = super.getMaxDistance(control)
            return if (properties.isBounded)
                distance - properties.strokeWidth * 2
            else distance
        }
    }

    override val circleDrawer: ControlDrawer = CircleDrawer(properties)

    open class RatioCircleArcProperties @JvmOverloads constructor(
        colors: ColorsScheme,
        strokeWidth: Float,
        sweepAngle: Float,
        val circleProperties: RatioCircleProperties,
        isBounded: Boolean = true,
    ) : ArcProperties(colors, strokeWidth, sweepAngle, isBounded)

    override fun getCircleRadius(control: Control): Double = control.radius * ratio
}