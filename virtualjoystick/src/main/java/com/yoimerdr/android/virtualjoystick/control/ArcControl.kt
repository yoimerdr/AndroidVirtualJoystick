package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.control.drawer.ArcControlDrawer
import com.yoimerdr.android.virtualjoystick.geometry.Size
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * [Control] that defines the methods to use a [drawer] that draws something similar to an arc.
 *
 * By default, the [drawer] is [ArcControlDrawer].
 */
open class ArcControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    strokeWidth: Float,
    sweepAngle: Float,
) : Control(invalidRadius) {

    /**
     * The paint stroke width.
     *
     * Used for the stroke of arc arrow.
     */
    protected val strokeWidth: Float


    init {
        this.strokeWidth = ArcControlDrawer.getValidStrokeWidth(strokeWidth)
        drawer = ArcControlDrawer(colors, this.strokeWidth, sweepAngle)
    }

    /**
     * Sets radius restrictions of the control.
     *
     * The restrictions are set according to the arrow [strokeWidth].
     *
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

    /**
     * Checks if [distanceFromCenter] is greater than the [outerRadius].
     * If so, changes the position to the [outParametricPosition].
     */
    override fun validatePositionLimits() {
        if(distanceFromCenter > outerRadius)
            this.position.set(outParametricPosition)
    }

}