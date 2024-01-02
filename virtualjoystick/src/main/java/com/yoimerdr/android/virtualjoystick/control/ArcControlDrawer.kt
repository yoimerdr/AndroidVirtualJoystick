package com.yoimerdr.android.virtualjoystick.control

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.SweepGradient
import com.yoimerdr.android.virtualjoystick.models.ColorsScheme
import com.yoimerdr.android.virtualjoystick.models.Position
import com.yoimerdr.android.virtualjoystick.models.Size
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin


open class ArcControlDrawer(
    protected open val colors: ColorsScheme,
    position: Position,
    innerRadius: Float,
    protected open val strokeWidth: Float,
    protected open val sweepAngle: Float,
) : ControlDrawer(Paint(Paint.ANTI_ALIAS_FLAG), position, innerRadius) {


    protected open val viewRadius: Float get() = properties.maximumRadius

    init {
        paint.apply {
            style = Paint.Style.STROKE
            this.color = colors.primary
            strokeWidth = this@ArcControlDrawer.strokeWidth
            shader = SweepGradient(
                center.x, center.y,
                intArrayOf(colors.accent, colors.primary), // Colores del gradiente
                floatArrayOf(0.0f, 1.0f)
            ).apply {
                val rotate = 270f
                val gradientMatrix = Matrix()
                gradientMatrix.preRotate(rotate, center.x, center.y)
                setLocalMatrix(gradientMatrix)
            }
        }
    }

    companion object {
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
                    properties.apply {
                        maximumRadius = it
                        radius = it - strokeWidth * 2
                    }
                }
        }
    }

    override fun onDraw(canvas: Canvas, size: Size) {
        (size.width / 2f).also {
            center.set(it, it)
        }

        if(distanceFromCenter() <= properties.innerRadius)
            return

        val radAngle: Double = getRadianAngle()
        val angle: Float = abs(Math.toDegrees(radAngle) - 360).toFloat() - sweepAngle / 2


        properties.apply {
            // oval for arc
            val oval = RectF(viewRadius - radius, viewRadius - radius, viewRadius + radius, viewRadius + radius)

            // parameters for arrow
            val arrowRadius = radius + strokeWidth * 2
            val arrowX = arrowRadius * cos(radAngle) + center.x
            val arrowY = abs(arrowRadius * sin(radAngle) - center.y)

            canvas.apply {
                paint.style = Paint.Style.STROKE
                drawArc(oval, angle, sweepAngle, false, paint)
                paint.style = Paint.Style.FILL_AND_STROKE
                drawArrow(arrowX.toFloat(), arrowY.toFloat(), abs(angle - 45), strokeWidth * 2, paint)
            }
        }
    }


}