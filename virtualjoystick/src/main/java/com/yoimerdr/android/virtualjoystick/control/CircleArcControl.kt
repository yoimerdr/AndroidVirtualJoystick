package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.control.drawer.ArcControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.CircleArcControlDrawer
import com.yoimerdr.android.virtualjoystick.geometry.Size
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

class CircleArcControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    strokeWidth: Float,
    sweepAngle: Float,
    radiusProportion: Float
) : CircleControl(colors, invalidRadius, radiusProportion), CircleArcControlDrawer.BeforeDraw {

    /**
     * Paint stroke width.
     *
     * Used for the stroke of arc arrow.
     */
    private val strokeWidth: Float

    private val viewSize: Size = Size()
    private val viewRadius: Float get() = viewSize.width.coerceAtMost(viewSize.height) / 2f

    init {
        this.strokeWidth = ArcControlDrawer.getValidStrokeWidth(strokeWidth)
        drawer = CircleArcControlDrawer(colors, strokeWidth, sweepAngle, this)
    }

    override fun setRadiusRestriction(size: Size) {
        super.setRadiusRestriction(size)
        outCircle.radius -= strokeWidth * 2
    }


    override fun onSizeChanged(size: Size) {
        super.onSizeChanged(size)
        viewSize.set(size)
    }

    override fun beforeArc(control: Control) {
        inCircle.radius = distanceFromCenter + inCircle.radius
        outCircle.radius = viewRadius
    }

    override fun beforeCircle(control: Control) {
        setRadiusRestriction(viewSize)
    }
}