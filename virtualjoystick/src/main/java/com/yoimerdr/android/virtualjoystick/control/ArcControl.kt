package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.control.drawer.ArcControlDrawer
import com.yoimerdr.android.virtualjoystick.geometry.Size
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * [Control] that draws an arc on the perimeter of the outer circle as a Joystick Control.
 */
open class ArcControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    strokeWidth: Float,
    sweepAngle: Float,
) : Control(invalidRadius) {

    /**
     * Paint stroke width.
     *
     * Used for the stroke of arc arrow.
     */
    protected val strokeWidth: Float


    init {
        this.strokeWidth = ArcControlDrawer.getValidStrokeWidth(strokeWidth)
        drawer = ArcControlDrawer(colors, this.strokeWidth, sweepAngle)
    }

    /**
     * Set radius restrictions based on the view size.
     *
     * The inner circle occupies almost half of the maximum width of the view, while the outer circle occupies the entire half.
     * @param size The size of the view.
     */
    override fun setRadiusRestriction(size: Size) {
        size.apply {
            (width.coerceAtMost(height) / 2f)
                .also {
                    outCircle.radius = it
                    inCircle.radius = it - strokeWidth * 2
                }
        }
    }

    override fun validatePositionLimits() {
        if(distanceFromCenter > outerRadius)
            this.position.set(outParametricPosition)
    }

}