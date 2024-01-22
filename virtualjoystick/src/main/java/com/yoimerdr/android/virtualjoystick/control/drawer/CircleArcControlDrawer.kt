package com.yoimerdr.android.virtualjoystick.control.drawer

import android.graphics.Canvas
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

open class CircleArcControlDrawer(
    colors: ColorsScheme,
    strokeWidth: Float,
    sweepAngle: Float,
    protected open val beforeDraw: BeforeDraw?
) : ArcControlDrawer(colors, strokeWidth, sweepAngle) {

    protected open val circleDrawer: ControlDrawer = CircleControlDrawer(colors)

    constructor(colors: ColorsScheme, strokeWidth: Float, sweepAngle: Float) : this(colors, strokeWidth, sweepAngle, null)

    interface BeforeDraw {
        fun beforeArc(control: Control)
        fun beforeCircle(control: Control)
    }

    override fun getInnerRadius(control: Control): Float {
        return control.distanceFromCenter + control.innerRadius
    }

    override fun draw(canvas: Canvas, control: Control) {
        if(!control.isInCenter()) {
            beforeDraw?.beforeArc(control)
            drawArc(canvas, control)
        }

        beforeDraw?.beforeCircle(control)
        circleDrawer.draw(canvas, control)
    }
}