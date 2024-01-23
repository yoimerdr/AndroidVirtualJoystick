package com.yoimerdr.android.virtualjoystick.control.drawer

import android.graphics.Canvas
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * A [ControlDrawer] that draws a circle accompanied by an arc.
 */
open class CircleArcControlDrawer(
    colors: ColorsScheme,
    strokeWidth: Float,
    sweepAngle: Float,
    /**
     * The interface to call before drawing the circle or arc.
     */
    protected open val beforeDraw: BeforeDraw?
) : ArcControlDrawer(colors, strokeWidth, sweepAngle) {

    /**
     * The circle drawer.
     */
    protected open val circleDrawer: ControlDrawer = CircleControlDrawer(colors)

    constructor(colors: ColorsScheme, strokeWidth: Float, sweepAngle: Float) : this(colors, strokeWidth, sweepAngle, null)

    /**
     * Interface to call before drawing the circle or arc.
     */
    interface BeforeDraw {
        /**
         * Called before drawing the arc.
         * @param control The [Control] from where the drawer is used.
         */
        fun beforeArc(control: Control)
        /**
         * Called before drawing the circle.
         * @param control The [Control] from where the drawer is used.
         */
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