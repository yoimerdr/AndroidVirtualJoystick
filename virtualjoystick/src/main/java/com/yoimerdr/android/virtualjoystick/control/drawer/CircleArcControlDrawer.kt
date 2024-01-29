package com.yoimerdr.android.virtualjoystick.control.drawer

import android.graphics.Canvas
import androidx.annotation.ColorInt
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.control.drawer.CircleControlDrawer.CircleProperties
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * A [ControlDrawer] that draws a circle accompanied by an arc.
 */
open class CircleArcControlDrawer(
    private val properties: CircleArcProperties,
    /**
     * The interface to call before drawing the circle or arc.
     */
    protected open val beforeDraw: BeforeDraw?
) : ArcControlDrawer(properties) {

    constructor(properties: CircleArcProperties) : this(properties, null)
    constructor(
        colors: ColorsScheme,
        strokeWidth: Float,
        sweepAngle: Float,
        radiusProportion: Float,
        beforeDraw: BeforeDraw?
    ) : this(
        CircleArcProperties(
            colors,
            strokeWidth,
            sweepAngle,
            CircleProperties(colors, radiusProportion)
        ), beforeDraw
    )

    constructor(
        @ColorInt color: Int,
        strokeWidth: Float,
        sweepAngle: Float,
        radiusProportion: Float,
        beforeDraw: BeforeDraw?
    ) : this(ColorsScheme(color), strokeWidth, sweepAngle, radiusProportion, beforeDraw)

    constructor(
        colors: ColorsScheme,
        strokeWidth: Float,
        sweepAngle: Float,
        radiusProportion: Float,
    ) : this(colors, strokeWidth, sweepAngle, radiusProportion, null)

    constructor(
        @ColorInt color: Int,
        strokeWidth: Float,
        sweepAngle: Float,
        radiusProportion: Float
    ) : this(color, strokeWidth, sweepAngle, radiusProportion, null)

    var radiusProportion: Float
        get() = properties.circleProperties.proportion
        set(value) {
            properties.circleProperties.proportion = CircleControlDrawer.getRadiusProportion(value)
        }

    private class CircleDrawer(val properties: CircleArcProperties) : CircleControlDrawer(properties.circleProperties) {
        override fun getMaxCircleRadius(control: Control): Double {
            return super.getMaxCircleRadius(control) - properties.strokeWidth * 2
        }
    }

    /**
     * The circle drawer.
     */
    protected open val circleDrawer: ControlDrawer = CircleDrawer(properties)

    open class CircleArcProperties(
        colors: ColorsScheme,
        strokeWidth: Float,
        sweepAngle: Float,
        val circleProperties: CircleProperties
    ) : ArcProperties(colors, strokeWidth, sweepAngle)


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

    override fun getArcDistance(control: Control): Double {
        val max = super.getArcDistance(control)

        return (control.distanceFromCenter + (control.viewRadius * radiusProportion))
            .coerceAtMost(max)
    }

    override fun draw(canvas: Canvas, control: Control) {
        if (!control.isInCenter()) {
            beforeDraw?.beforeArc(control)
            drawControl(canvas, control)
        }

        beforeDraw?.beforeCircle(control)
        circleDrawer.draw(canvas, control)
    }
}