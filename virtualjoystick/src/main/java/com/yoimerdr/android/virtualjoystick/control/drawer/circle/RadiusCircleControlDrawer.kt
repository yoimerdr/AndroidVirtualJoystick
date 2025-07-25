package com.yoimerdr.android.virtualjoystick.control.drawer.circle

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.control.drawer.ControlDrawer
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * A [ControlDrawer] that draws a given circle based on a radius value.
 */
open class RadiusCircleControlDrawer(
    private val properties: RadiusCircleProperties,
) : BaseCircleControlDrawer(properties) {

    /**
     * @param colors The colors for the drawer.
     * @param radius The the circle radius length.
     */
    @JvmOverloads
    constructor(
        colors: ColorsScheme,
        radius: Float,
        isBounded: Boolean = true,
    ) : this(RadiusCircleProperties(colors, radius, isBounded))

    /**
     * @param color The unique initial color for the drawer.
     * @param radius The the circle radius length.
     */
    @JvmOverloads
    constructor(
        @ColorInt color: Int,
        radius: Float,
        isBounded: Boolean = true,
    ) : this(ColorsScheme(color), radius, isBounded)

    var radius: Float
        @FloatRange(
            from = 0.0,
            fromInclusive = false
        )
        /**
         * Gets the circle radius.
         */
        get() = properties.radius
        /**
         * Sets the circle radius.
         * @param radius The new circle radius.
         */
        set(
            @FloatRange(
                from = 0.0,
                fromInclusive = false
            ) radius,
        ) {
            properties.radius = radius
        }

    open class RadiusCircleProperties @JvmOverloads constructor(
        colors: ColorsScheme,
        @FloatRange(
            from = 0.0, fromInclusive = false
        ) var radius: Float,
        isBounded: Boolean = true,
    ) : BasicCircleProperties(colors, isBounded)

    override fun getCircleRadius(control: Control): Double = radius.toDouble()
        .coerceAtMost(control.radius)
}