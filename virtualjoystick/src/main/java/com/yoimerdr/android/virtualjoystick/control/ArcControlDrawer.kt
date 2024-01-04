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

/**
 * [ControlDrawer] that draws an arc on the perimeter of the outer circle as a Joystick Control.
 */
open class ArcControlDrawer(
    /**
     * Scheme with the circle colors.
     *
     * Used for the [RadialGradient] shader for the [paint]
     */
    protected open val colors: ColorsScheme,
    position: Position,
    invalidRadius: Int,
    strokeWidth: Float,
    sweepAngle: Float,
) : ControlDrawer(Paint(Paint.ANTI_ALIAS_FLAG), position, invalidRadius) {

    /**
     * View radius.
     *
     * Short getter for outer circle radius.
     */
    protected open val viewRadius: Float get() = outCircle.radius

    /**
     * Paint stroke width.
     *
     * Used for the stroke of arc arrow.
     */
    protected val strokeWidth: Float

    /**
     * Arc sweep angle.
     */
    protected val sweepAngle: Float
    init {
        this.sweepAngle = if(sweepAngle > MAX_SWEEP_ANGLE)
            MAX_SWEEP_ANGLE
        else if(sweepAngle < MIN_SWEEP_ANGLE)
            MIN_SWEEP_ANGLE
        else sweepAngle

        this.strokeWidth = if(strokeWidth < MIN_STROKE_WIDTH)
            MIN_STROKE_WIDTH
        else strokeWidth

        paint.apply {
            style = Paint.Style.STROKE
            color = colors.primary
            this.strokeWidth = this@ArcControlDrawer.strokeWidth
        }
    }

    companion object {
        /**
         * The minimum arc sweep angle.
         */
        const val MIN_SWEEP_ANGLE: Float = 30.0f

        /**
         * The maximum arc sweep angle.
         */
        const val MAX_SWEEP_ANGLE: Float = 180.0f

        /**
         * The minimum stroke width of arc arrow.
         */
        const val MIN_STROKE_WIDTH: Float = 5f

        /**
         * Extension method for [Canvas] that draw an arrow.
         *
         * @param x The x coordinate of arrow.
         * @param y The y coordinate of arrow.
         * @param angle The angle for rotate the arrow.
         * @param size The arrow size.
         * @param paint The draw pain.
         */
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

    /**
     * Set radius restrictions based on the view size.
     *
     * The inner circle occupies almost half of the maximum width of the view, while the outer circle occupies the entire half.
     * @param size The size of the view.
     */
    override fun setRadiusRestriction(size: Size) {
        size.apply {
            (width.coerceAtMost(height) / 2f)
                .also {
                    outCircle.radius = it
                    inCircle.radius = it - strokeWidth * 2
                }
        }
    }

    /**
     * Draw the control if the distance from the current position to the center is greater than the invalid radius.
     */
    override fun onDraw(canvas: Canvas, size: Size) {
        if(distanceFromCenter() < invalidRadius)
            return
        drawArc(canvas)
    }

    /**
     * Draw the arc at the perimetric position of the inner circle.
     *
     * @param canvas The view canvas.
     */
    protected open fun drawArc(canvas: Canvas) {
        val angle: Double = getRadianAngle()
        val arcAngle: Double = Math.toDegrees(angle) - sweepAngle / 2

        inCircle.apply {
            paint.apply {
                shader = paintShader(angle, arcAngle)
                style = Paint.Style.STROKE
            }
            val oval = RectF(viewRadius - radius, viewRadius - radius, viewRadius + radius, viewRadius + radius)
            canvas.drawArc(oval, arcAngle.toFloat(), sweepAngle, false, paint)
            drawArcArrow(canvas, angle)
        }
    }

    /**
     * Draw the arc arrow in the center of the arc pointing outward.
     *
     * @param canvas The view canvas
     * @param angle The angle formed between the current position and the center measured in the range of 0 to 2PI radians clockwise.
     */
    protected open fun drawArcArrow(canvas: Canvas, angle: Double) {

        outCircle.apply {
            val outRadius = radius
            radius = outRadius - strokeWidth

            val position = parametricPositionFrom(angle)
            paint.apply {
                color = colors.accent
                style = Paint.Style.FILL_AND_STROKE
            }

            val arrowSweepAngle = (Math.toDegrees(angle) - angleFrom(position)) - 90
            position.apply {
                canvas.drawArrow(x, y, arrowSweepAngle.toFloat(), strokeWidth, paint)
            }
            radius = outRadius
        }
    }


    /**
     * The [RadialGradient] for the arc paint.
     *
     * @param angle The angle formed between the current position and the center measured in the range of 0 to 2PI radians clockwise.
     * @param arcAngle The start arc sweep angle measured in the range 0 to 360 degrees clockwise
     */
    protected open fun paintShader(angle: Double, arcAngle: Double): Shader {
        val position = inCircle.parametricPositionFrom(angle)
        return RadialGradient(
            position.x, position.y,
            inCircle.radius,
            intArrayOf(colors.accent, colors.primary),
            null,
            Shader.TileMode.CLAMP
        )
    }

}