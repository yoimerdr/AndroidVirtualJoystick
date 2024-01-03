package com.yoimerdr.android.virtualjoystick.control

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import com.yoimerdr.android.virtualjoystick.geometry.Position
import com.yoimerdr.android.virtualjoystick.geometry.Size
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme


open class ArcControlDrawer(
    protected open val colors: ColorsScheme,
    position: Position,
    invalidRadius: Int,
    protected open val strokeWidth: Float,
    protected open val sweepAngle: Float,
) : ControlDrawer(Paint(Paint.ANTI_ALIAS_FLAG), position, invalidRadius) {

    protected open val viewRadius: Float get() = outCircle.radius

    init {
        paint.apply {
            style = Paint.Style.STROKE
            this.color = colors.primary
            strokeWidth = this@ArcControlDrawer.strokeWidth
        }
    }

    companion object {
        val MIN_SWEEP_ANGLE: Float get() = 30.0f
        val MAX_SWEEP_ANGLE: Float get() = 180.0f

        val MIN_STROKE_WIDTH: Float get() = 5f

        private fun Canvas.drawArrow(x: Float, y: Float, angle: Float, size: Float, paint: Paint) {
            val arrowPath = Path()
            arrowPath.moveTo(x, y)
            arrowPath.lineTo(x + size, y - size)
            arrowPath.lineTo(x, y - size / 2)
            arrowPath.lineTo(x - size, y - size)
            arrowPath.close()

            save()
            rotate(angle, x, y)
            drawPath(arrowPath, paint)
            restore()
        }

    }

    override fun setRadiusRestriction(size: Size) {
        size.apply {
            (width.coerceAtMost(height) / 2f)
                .also {
                    outCircle.radius = it
                    inCircle.radius = it - strokeWidth * 2
                }
        }
    }
    override fun onDraw(canvas: Canvas, size: Size) {
        if(distanceFromCenter() < invalidRadius)
            return
        drawArc(canvas)
    }

    protected open fun drawArc(canvas: Canvas) {
        val angle: Double = getRadianAngle()
        val arcAngle: Double = Math.toDegrees(angle) - sweepAngle / 2

        inCircle.apply {
            paint.apply {
                shader = paintShader(angle)
                style = Paint.Style.STROKE
            }
            val oval = RectF(viewRadius - radius, viewRadius - radius, viewRadius + radius, viewRadius + radius)
            canvas.drawArc(oval, arcAngle.toFloat(), sweepAngle, false, paint)
            drawArcArrow(canvas, angle)
        }
    }

    protected open fun drawArcArrow(canvas: Canvas, angle: Double) {

        outCircle.apply {
            val outRadius = radius
            radius = outRadius - strokeWidth

            val position = parametricPositionFrom(angle)
            paint.apply {
                style = Paint.Style.FILL_AND_STROKE
            }

            val arrowSweepAngle = (Math.toDegrees(angle) - angleFrom(position)) - 90
            position.apply {
                canvas.drawArrow(x, y, arrowSweepAngle.toFloat(), strokeWidth, paint)
            }
            radius = outRadius
        }
    }


    protected open fun paintShader(angle: Double): Shader {
        val position = inCircle.parametricPositionFrom(angle)
        return RadialGradient(
            position.x, position.y,
            inCircle.radius,
            intArrayOf(colors.primary, colors.accent),
            null,
            Shader.TileMode.CLAMP
        )
    }

}