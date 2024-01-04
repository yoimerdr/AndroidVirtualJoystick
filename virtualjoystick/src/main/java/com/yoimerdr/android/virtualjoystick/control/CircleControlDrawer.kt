package com.yoimerdr.android.virtualjoystick.control

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme
import com.yoimerdr.android.virtualjoystick.geometry.Position
import com.yoimerdr.android.virtualjoystick.geometry.Size

/**
 * [ControlDrawer] that draws a circle as a Joystick Control.
 */
open class CircleControlDrawer(
    /**
     * Scheme with the circle colors.
     *
     * Used for the [RadialGradient] shader for the [paint]
     */
    protected open val colors: ColorsScheme,
    position: Position,
    invalidRadius: Int
) : ControlDrawer(Paint(Paint.ANTI_ALIAS_FLAG), position, invalidRadius) {

    /**
     * Set radius restrictions based on the view size.
     *
     * The inner circle occupies 25% of half of the maximum view width, while the outer circle occupies 75%.
     * @param size The size of the view.
     */
    override fun setRadiusRestriction(size: Size) {
        size.apply {
            (width.coerceAtMost(height) / 2f)
                .also {
                    outCircle.radius = it * 0.75f
                    inCircle.radius = it * 0.25f
                }
        }
    }

    /**
     * Draw a circle at the current [position] with the radius of the inner circle.
     */
    override fun onDraw(canvas: Canvas, size: Size) {
        paint.shader = paintShader
        position.apply {
            canvas.drawCircle(x, y, inCircle.radius, paint)
        }
    }

    /**
     * A shader for the paint.
     *
     * The default is a [RadialGradient] that takes the current [position] of the control and the [colors] scheme.
     */
    protected open val paintShader: Shader get() = RadialGradient(
        position.x , position.y, inCircle.radius,
        intArrayOf(colors.accent, colors.primary),
        null,
        Shader.TileMode.CLAMP
    )


}