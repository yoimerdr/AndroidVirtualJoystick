package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.control.drawer.CircleControlDrawer
import com.yoimerdr.android.virtualjoystick.geometry.Size
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * [Control] that draws a circle as a Joystick Control.
 */
open class CircleControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    radiusProportion: Float
) : Control(invalidRadius) {
    protected val proportion: Float

    init {
        drawer = CircleControlDrawer(colors)
        proportion = getValidRadiusProportion(radiusProportion)
    }

    companion object {
        const val MIN_RADIUS_PROPORTION = 0.1f
        const val MAX_RADIUS_PROPORTION = 0.80f

        fun getValidRadiusProportion(proportion: Float): Float {
            return if(proportion > MAX_RADIUS_PROPORTION)
                MAX_RADIUS_PROPORTION
            else if(proportion < MIN_RADIUS_PROPORTION)
                MIN_RADIUS_PROPORTION
            else proportion
        }
    }

    /**
     * Set radius restrictions based on the view size.
     *
     * The inner circle occupies 25% of half of the maximum view width, while the outer circle occupies 75%.
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
     * Check if the distance at the current position and the center is greater than the maximum view radius.
     * If so, change the position to the extreme maximum at that position.
     */
    override fun validatePositionLimits() {
        if (distanceFromCenter > outerRadius) {
            position.set(outParametricPosition)
        }
    }

}