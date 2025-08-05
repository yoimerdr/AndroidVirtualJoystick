package com.yoimerdr.android.virtualjoystick.control.drawer.circle

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.control.drawer.ControlDrawer
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * A [ControlDrawer] that draws a given circle based on the ratio of the control radius.
 */
open class RatioCircleControlDrawer(
    /**
     * The circle drawer properties.
     */
    private val properties: RatioCircleProperties,
) : BaseCircleControlDrawer(properties) {

    companion object {
        /**
         * The minimum valid radius ratio value.
         */
        const val MIN_RADIUS_RATIO = 0.1f

        /**
         * The maximum valid radius ratio value.
         */
        const val MAX_RADIUS_RATIO = 0.80f

        /**
         * Checks if the [ratio] value meets the valid range.
         *
         * @param ratio The ratio value.
         *
         * @return A valid radius ratio in the range [MIN_RADIUS_RATIO] to [MAX_RADIUS_RATIO]
         */
        @JvmStatic
        @FloatRange(from = MIN_RADIUS_RATIO.toDouble(), to = MAX_RADIUS_RATIO.toDouble())
        fun getRadiusRatio(ratio: Float): Float {
            return ratio.coerceIn(MIN_RADIUS_RATIO, MAX_RADIUS_RATIO)
        }
    }

    /**
     * @param colors The colors for the drawer.
     * @param ratio The ratio value for the circle radius length.
     */
    @JvmOverloads
    constructor(
        colors: ColorsScheme,
        ratio: Float,
        isBounded: Boolean = true,
    ) : this(RatioCircleProperties(colors, ratio, isBounded))

    /**
     * @param color The unique initial color for the drawer.
     * @param ratio The ratio value for the circle radius length.
     */
    @JvmOverloads
    constructor(
        @ColorInt color: Int,
        ratio: Float,
        isBounded: Boolean = true,
    ) : this(ColorsScheme(color), ratio, isBounded)


    var ratio: Float
        /**
         * Gets the circle radius ratio.
         */
        @FloatRange(
            from = MIN_RADIUS_RATIO.toDouble(),
            to = MAX_RADIUS_RATIO.toDouble(),
        )
        get() = properties.ratio
        /**
         * Sets the circle radius ratio.
         * @param ratio The new circle radius ratio. Must be a value in the range from [MIN_RADIUS_RATIO] to [MAX_RADIUS_RATIO]
         */
        set(
            @FloatRange(
                from = MIN_RADIUS_RATIO.toDouble(),
                to = MAX_RADIUS_RATIO.toDouble(),
            )
            ratio,
        ) {
            properties.ratio = ratio
        }

    open class RatioCircleProperties @JvmOverloads constructor(
        colors: ColorsScheme,
        @FloatRange(
            from = MIN_RADIUS_RATIO.toDouble(),
            to = MAX_RADIUS_RATIO.toDouble(),
        )
        ratio: Float,
        isBounded: Boolean = true,
    ) : BasicCircleProperties(
        colors, isBounded
    ) {
        var ratio = getRadiusRatio(ratio)
            set(value) {
                field = getRadiusRatio(value)
            }
    }

    override fun getCircleRadius(control: Control): Double = control.radius * ratio
}