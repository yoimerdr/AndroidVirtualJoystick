package com.yoimerdr.android.virtualjoystick.control

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import com.yoimerdr.android.virtualjoystick.models.ColorsScheme
import com.yoimerdr.android.virtualjoystick.models.Position
import com.yoimerdr.android.virtualjoystick.models.Size

open class CircleControlDrawer(
    protected open val colors: ColorsScheme,
    position: Position,
    innerRadius: Float
) : ControlDrawer(Paint(Paint.ANTI_ALIAS_FLAG), position, innerRadius) {

    /**
     * Set radius restrictions based on the view size.
     * @param size The size of the view.
     */
    override fun setRadiusRestriction(size: Size) {
        size.apply {
            (width.coerceAtMost(height) / 2)
                .also {
                    properties.apply {
                        maximumRadius = it * 0.75f
                        radius = it * 0.25f
                    }
                }
        }
    }

    override fun onDraw(canvas: Canvas, size: Size) {
        (size.width / 2f).also {
            center.set(it, it)
        }
        paint.shader = radialShader
        position.apply {
            canvas.drawCircle(x, y, properties.radius, paint)
        }
    }

    protected open val radialShader: Shader get() = RadialGradient(
        position.x , position.y, properties.radius,
        intArrayOf(colors.accent, colors.primary),
        null,
        Shader.TileMode.CLAMP
    )


}