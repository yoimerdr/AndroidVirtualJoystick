package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.control.drawer.ArcControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.CircleArcControlDrawer
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.Position
import com.yoimerdr.android.virtualjoystick.geometry.Size
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

class CircleArcControl(
    private val viewSize: Size,
    colors: ColorsScheme,
    position: Position,
    invalidRadius: Int,
    strokeWidth: Float,
    sweepAngle: Float,
) : CircleControl(colors, position, invalidRadius), CircleArcControlDrawer.BeforeDraw {

    /**
     * Paint stroke width.
     *
     * Used for the stroke of arc arrow.
     */
    protected val strokeWidth: Float

    protected val viewRadius: Float get() = viewSize.width.coerceAtMost(viewSize.height) / 2f

    init {
        this.strokeWidth = ArcControlDrawer.getValidStrokeWidth(strokeWidth)
        drawer = CircleArcControlDrawer(position, inCircle, outCircle, colors, strokeWidth, sweepAngle, this)
    }

    override fun setRadiusRestriction(size: Size) {
        viewRadius.also {
            outCircle.radius = it * 0.75f - strokeWidth * 2f
            inCircle.radius = it * 0.25f
        }
    }


    override fun onSizeChanged(size: Size) {
        super.onSizeChanged(size)
        viewSize.set(size)
    }

    override fun beforeArc(control: Control, arrowCircle: Circle): Boolean {
        (distanceFromCenter() + inCircle.radius).also {
            inCircle.radius = it
            arrowCircle.radius = it + strokeWidth * 2
        }
        outCircle.radius = viewRadius
        return !isInCenter()
    }

    override fun beforeCircle(control: Control) {
        setRadiusRestriction(viewSize)
    }
}