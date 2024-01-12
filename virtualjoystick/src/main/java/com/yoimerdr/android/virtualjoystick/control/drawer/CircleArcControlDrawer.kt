package com.yoimerdr.android.virtualjoystick.control.drawer

import android.graphics.Canvas
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.Position
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

open class CircleArcControlDrawer(
    protected val position: Position,
    inCircle: Circle,
    outCircle: Circle,
    colors: ColorsScheme,
    strokeWidth: Float,
    sweepAngle: Float,
    protected val beforeDraw: BeforeDraw? = null
) : ArcControlDrawer(inCircle, outCircle, colors, strokeWidth, sweepAngle) {

    protected open val circleDrawer: ControlDrawer = CircleControlDrawer(position, colors, inCircle)

    protected open val cCircle: Circle = Circle(inCircle.radius, inCircle.center)

    override val arrowCircle: Circle get() = cCircle

    interface BeforeDraw {
        fun beforeArc(control: Control, arrowCircle: Circle): Boolean
        fun beforeCircle(control: Control)
    }


    override fun draw(canvas: Canvas, control: Control) {
        if(beforeDraw?.beforeArc(control, cCircle) == true)
            drawArc(canvas, control)
        beforeDraw?.beforeCircle(control)
        circleDrawer.draw(canvas, control)

    }
}