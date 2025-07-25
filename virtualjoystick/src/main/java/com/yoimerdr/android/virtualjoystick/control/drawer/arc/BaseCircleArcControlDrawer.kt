package com.yoimerdr.android.virtualjoystick.control.drawer.arc

import android.graphics.Canvas
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.control.drawer.ControlDrawer

/**
 * A [ControlDrawer] that draws a circle accompanied by an arc.
 */
abstract class BaseCircleArcControlDrawer protected constructor(
    /**
     * The drawer properties.
     */
    properties: ArcProperties,
) : ArcControlDrawer(properties) {

    /**
     * The circle drawer.
     */
    protected abstract val circleDrawer: ControlDrawer

    /**
     * Gets the circle radius.
     * @param control The [Control] from where the drawer is used.
     */
    protected abstract fun getCircleRadius(control: Control): Double

    override fun getDistance(control: Control): Double {
        val max = super.getDistance(control)

        return (control.distance + getCircleRadius(control))
            .coerceAtMost(max)
    }

    override fun draw(canvas: Canvas, control: Control) {
        if (!control.isInCenter())
            drawShapes(canvas, control)

        circleDrawer.draw(canvas, control)
    }
}