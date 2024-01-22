package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.control.drawer.ArcControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.CircleArcControlDrawer
import com.yoimerdr.android.virtualjoystick.geometry.Size
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * [Control] that defines the methods to use a [drawer] that draws something similar to an circle with an arc.
 *
 * By default, the [drawer] is [CircleArcControlDrawer].
 */
open class CircleArcControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    strokeWidth: Float,
    sweepAngle: Float,
    radiusProportion: Float
) : CircleControl(colors, invalidRadius, radiusProportion) {

    /**
     * The paint stroke width.
     *
     * Used for the stroke of arc arrow.
     */
    private val strokeWidth: Float

    /**
     * The size of the view where the control is used.
     */
    private val viewSize: Size = Size()

    init {
        this.strokeWidth = ArcControlDrawer.getValidStrokeWidth(strokeWidth)
        drawer = CircleArcControlDrawer(colors, strokeWidth, sweepAngle)
    }

    /**
     * Sets radius restrictions of the control.
     *
     * The restrictions are set according to the circle radius [proportion] and arrow [strokeWidth]
     *
     * @param size The size of the view.
     */
    override fun setRadiusRestriction(size: Size) {
        super.setRadiusRestriction(size)
        outCircle.radius -= strokeWidth * 2
    }


    override fun onSizeChanged(size: Size) {
        super.onSizeChanged(size)
        viewSize.set(size)
    }

}