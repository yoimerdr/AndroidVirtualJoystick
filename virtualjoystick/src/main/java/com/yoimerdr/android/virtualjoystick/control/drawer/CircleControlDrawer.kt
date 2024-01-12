package com.yoimerdr.android.virtualjoystick.control.drawer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.Position
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

open class CircleControlDrawer(
    protected open val position: Position,
    /**
     * Scheme with the circle colors.
     *
     * Used for the [RadialGradient] shader for the [paint]
     */
    protected open val colors: ColorsScheme,
    protected open val circle: Circle
) : ControlDrawer {
    /**
     * A shader for the paint.
     *
     * The default is a [RadialGradient] that takes the current [position] of the control and the [colors] scheme.
     */
    protected open val paintShader: Shader
        get() = RadialGradient(
        position.x , position.y, circle.radius,
        intArrayOf(colors.accent, colors.primary),
        null,
        Shader.TileMode.CLAMP
    )

    protected open val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * Draw a circle at the current [position] with the radius of the inner circle.
     */
    override fun draw(canvas: Canvas, control: Control) {
        paint.shader = paintShader
        position.apply {
            canvas.drawCircle(x, y, circle.radius, paint)
        }
    }
}