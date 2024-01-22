package com.yoimerdr.android.virtualjoystick.control.drawer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

open class CircleControlDrawer(
    /**
     * Scheme with the circle colors.
     *
     * Used for the [RadialGradient] shader for the [paint]
     */
    protected open val colors: ColorsScheme
) : ControlDrawer {
    /**
     * A shader for the paint.
     *
     * The default is a [RadialGradient] that takes the current [control] position and the [colors] scheme.
     */
    protected open fun getPaintShader(control: Control): Shader {
        return control.immutablePosition.let {
            RadialGradient(
                it.x, it.y, getRadius(control),
                intArrayOf(colors.accent, colors.primary),
                null,
                Shader.TileMode.CLAMP
            )
        }
    }

    protected open fun getRadius(control: Control): Float = control.innerRadius

    protected open val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * Draw a circle at the current [control] position with the radius of the inner circle.
     */
    override fun draw(canvas: Canvas, control: Control) {
        paint.shader = getPaintShader(control)
        control.immutablePosition.apply {
            canvas.drawCircle(x, y, getRadius(control), paint)
        }
    }
}