package com.yoimerdr.android.virtualjoystick.control.drawer.arc

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.control.drawer.ControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.circle.RadiusCircleControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.circle.RadiusCircleControlDrawer.RadiusCircleProperties
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * A [ControlDrawer] that draws a circle accompanied by an arc.
 *
 * The circle radius is defined by a specific value
 */
open class RadiusCircleArcControlDrawer(
    /**
     * The drawer properties.
     */
    private val properties: RadiusCircleArcProperties,
) : BaseCircleArcControlDrawer(properties) {

    /**
     * @param colors The colors for the drawer.
     * @param strokeWidth The stroke width of the paint.
     * @param sweepAngle The arc sweep angle.
     * @param radius The the circle radius length.
     */
    @JvmOverloads
    constructor(
        colors: ColorsScheme,
        strokeWidth: Float,
        sweepAngle: Float,
        radius: Float,
        isBounded: Boolean = true,
    ) : this(
        RadiusCircleArcProperties(
            colors,
            strokeWidth,
            sweepAngle,
            RadiusCircleProperties(colors, radius),
            isBounded
        )
    )

    /**
     * @param color The unique initial color for the drawer.
     * @param strokeWidth The stroke width of the paint.
     * @param sweepAngle The arc sweep angle.
     * @param radius The the circle radius length.
     */
    constructor(
        @ColorInt color: Int,
        strokeWidth: Float,
        sweepAngle: Float,
        radius: Float,
    ) : this(ColorsScheme(color), strokeWidth, sweepAngle, radius)

    var radius: Float
        @FloatRange(
            from = 0.0,
            fromInclusive = false
        )
        /**
         * Gets the circle radius.
         */
        get() = properties.circleProperties.radius
        /**
         * Sets the circle radius.
         * @param radius The new circle radius.
         */
        set(
            @FloatRange(
                from = 0.0,
                fromInclusive = false
            )
            radius,
        ) {
            properties.circleProperties.radius = radius
        }

    protected open class CircleDrawer(private val properties: RadiusCircleArcProperties) :
        RadiusCircleControlDrawer(properties.circleProperties) {
        override fun getMaxDistance(control: Control): Double {
            val distance = super.getMaxDistance(control)
            return if (properties.isBounded)
                distance - properties.strokeWidth * 2
            else distance
        }
    }

    override val circleDrawer: ControlDrawer = CircleDrawer(properties)

    open class RadiusCircleArcProperties @JvmOverloads constructor(
        colors: ColorsScheme,
        strokeWidth: Float,
        sweepAngle: Float,
        val circleProperties: RadiusCircleProperties,
        isBounded: Boolean = true,
    ) : ArcProperties(colors, strokeWidth, sweepAngle, isBounded)

    override fun getCircleRadius(control: Control): Double = radius.toDouble()
}