package com.yoimerdr.android.virtualjoystick.control

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme
import com.yoimerdr.android.virtualjoystick.geometry.Position
import com.yoimerdr.android.virtualjoystick.geometry.Size

open class CircleControlDrawer(
    protected open val colors: ColorsScheme,
    position: Position,
    invalidRadius: Int
) : ControlDrawer(Paint(Paint.ANTI_ALIAS_FLAG), position, invalidRadius) {

    /**
     * Set radius restrictions based on the view size.
     * @param size The size of the view.
     */
    override fun setRadiusRestriction(size: Size) {
        size.apply {
            (width.coerceAtMost(height) / 2)
                .also {
                    outCircle.radius = it * 0.75f
                    inCircle.radius = it * 0.25f
                }
        }
    }

    override fun onDraw(canvas: Canvas, size: Size) {
        paint.shader = paintShader
        position.apply {
            canvas.drawCircle(x, y, inCircle.radius, paint)
        }
    }

    protected open val paintShader: Shader get() = RadialGradient(
        position.x , position.y, inCircle.radius,
        intArrayOf(colors.accent, colors.primary),
        null,
        Shader.TileMode.CLAMP
    )


}