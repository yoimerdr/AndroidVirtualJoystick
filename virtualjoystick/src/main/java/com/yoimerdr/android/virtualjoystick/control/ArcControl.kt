package com.yoimerdr.android.virtualjoystick.control

import android.graphics.RadialGradient
import com.yoimerdr.android.virtualjoystick.control.drawer.ArcControlDrawer
import com.yoimerdr.android.virtualjoystick.geometry.Position
import com.yoimerdr.android.virtualjoystick.geometry.Size
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * [Control] that draws an arc on the perimeter of the outer circle as a Joystick Control.
 */
open class ArcControl(
    /**
     * Scheme with the circle colors.
     *
     * Used for the [RadialGradient] shader for the [paint]
     */
    colors: ColorsScheme,
    position: Position,
    invalidRadius: Int,
    strokeWidth: Float,
    sweepAngle: Float,
) : Control(position, invalidRadius) {

    /**
     * Paint stroke width.
     *
     * Used for the stroke of arc arrow.
     */
    protected val strokeWidth: Float


    init {
        this.strokeWidth = ArcControlDrawer.getValidStrokeWidth(strokeWidth)
        drawer = ArcControlDrawer(inCircle, outCircle, colors, this.strokeWidth, sweepAngle)
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

    }

}