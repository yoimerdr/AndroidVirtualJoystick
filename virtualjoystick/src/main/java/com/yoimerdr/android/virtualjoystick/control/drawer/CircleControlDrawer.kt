package com.yoimerdr.android.virtualjoystick.control.drawer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * A [ControlDrawer] that draws a circle.
 *
 * By default, takes the inner radius of [Control] as circle radius.
 */
open class CircleControlDrawer(
    /**
     * The circle colors.
     */
    protected open val colors: ColorsScheme
) : ControlDrawer {

    /**
     * The drawer paint.
     */
    protected open val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * The [Shader] for the drawer paint.
     * @param control The [Control] from where the drawer is used.
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

    /**
     * Gets the circle radius.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getRadius(control: Control): Float = control.innerRadius

    override fun draw(canvas: Canvas, control: Control) {
        paint.shader = getPaintShader(control)
        control.immutablePosition.apply {
            canvas.drawCircle(x, y, getRadius(control), paint)
        }
    }
}