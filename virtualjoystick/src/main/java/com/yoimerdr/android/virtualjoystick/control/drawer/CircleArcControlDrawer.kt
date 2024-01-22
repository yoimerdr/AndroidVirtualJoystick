package com.yoimerdr.android.virtualjoystick.control.drawer

import android.graphics.Canvas
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.Position
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

open class CircleArcControlDrawer(
    colors: ColorsScheme,
    strokeWidth: Float,
    sweepAngle: Float,
    protected open val beforeDraw: BeforeDraw?
) : ArcControlDrawer(colors, strokeWidth, sweepAngle) {

    protected open val circleDrawer: ControlDrawer = CircleControlDrawer(colors)
    protected open val cCircle: Circle = Circle(1f, Position())

    constructor(colors: ColorsScheme, strokeWidth: Float, sweepAngle: Float) : this(colors, strokeWidth, sweepAngle, null)
    override fun getArcCircle(control: Control): Circle {
        return control.immutableCenter.let {
            cCircle.apply {
                if(center != it)
                    setCenter(it)
            }
        }
    }

    interface BeforeDraw {
        fun beforeArc(control: Control)
        fun beforeCircle(control: Control)
    }


    override fun draw(canvas: Canvas, control: Control) {
        if(!control.isInCenter()) {
            beforeDraw?.beforeArc(control)
            cCircle.radius = control.innerRadius + strokeWidth * 2
            drawArc(canvas, control)
        }
        beforeDraw?.beforeCircle(control)
        circleDrawer.draw(canvas, control)
    }
}