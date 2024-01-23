package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.control.drawer.CircleControlDrawer
import com.yoimerdr.android.virtualjoystick.geometry.Size
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * [Control] that defines the methods to use a [drawer] that draws something similar to a circle.
 *
 * By default, the [drawer] is [CircleControlDrawer].
 */
open class CircleControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    radiusProportion: Float
) : Control(invalidRadius) {

    /**
     * The circle radius proportion.
     *
     * Must be a value in the range from [MIN_RADIUS_PROPORTION] to [MAX_RADIUS_PROPORTION]
     */
    protected val proportion: Float

    init {
        drawer = CircleControlDrawer(colors)
        proportion = getValidRadiusProportion(radiusProportion)
    }

    companion object {
        /**
         * The minimum valid radius proportion.
         */
        const val MIN_RADIUS_PROPORTION = 0.1f

        /**
         * The maximum valid radius proportion.
         */
        const val MAX_RADIUS_PROPORTION = 0.80f

        /**
         * Checks if the [proportion] value meets the valid range.
         *
         * @param proportion The proportion value.
         *
         * @return A valid radius proportion in the range [MIN_RADIUS_PROPORTION] to [MAX_RADIUS_PROPORTION]
         */
        @JvmStatic
        fun getValidRadiusProportion(proportion: Float): Float {
            return if(proportion > MAX_RADIUS_PROPORTION)
                MAX_RADIUS_PROPORTION
            else if(proportion < MIN_RADIUS_PROPORTION)
                MIN_RADIUS_PROPORTION
            else proportion
        }
    }

    /**
     * Sets radius restrictions of the control.
     *
     * The restrictions are set according to the circle radius [proportion]
     *
     * @param size The size of the view.
     */
    override fun setRadiusRestriction(size: Size) {
        size.apply {
            (width.coerceAtMost(height) / 2f)
                .also {
                    outCircle.radius = it * (1 - proportion)
                    inCircle.radius = it * proportion
                }
        }
    }

    /**
     * Checks if [distanceFromCenter] is greater than the [outerRadius].
     * If so, changes the position to the [outParametricPosition].
     */
    override fun validatePositionLimits() {
        if (distanceFromCenter > outerRadius)
            position.set(outParametricPosition)
    }

}