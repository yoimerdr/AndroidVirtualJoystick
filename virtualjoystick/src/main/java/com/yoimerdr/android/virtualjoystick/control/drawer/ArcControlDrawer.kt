package com.yoimerdr.android.virtualjoystick.control.drawer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.Position
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

open class ArcControlDrawer(
    /**
     * Scheme with the circle colors.
     *
     * Used for the [RadialGradient] shader for the [paint]
     */
    protected open val colors: ColorsScheme,
    strokeWidth: Float,
    sweepAngle: Float,
) : ControlDrawer {

    protected open val arcCircle: Circle = Circle(1f, Position())

    protected val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * Paint stroke width.
     *
     * Used for the stroke of arc arrow.
     */
    protected val strokeWidth: Float

    /**
     * Arc sweep angle.
     *
     * A sexagesimal degree in the range [MIN_SWEEP_ANGLE] to [MAX_SWEEP_ANGLE]
     */
    protected val sweepAngle: Float

    init {
        this.sweepAngle = getValidSweepAngle(sweepAngle)
        this.strokeWidth = getValidStrokeWidth(strokeWidth)
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
         * Checks if the [sweepAngle] value meets the valid range.
         *
         * @param sweepAngle The angle value.
         *
         * @return A valid sexagesimal degree value in the range [MIN_SWEEP_ANGLE] to [MAX_SWEEP_ANGLE]
         */
        fun getValidSweepAngle(sweepAngle: Float): Float {
            return if(sweepAngle > MAX_SWEEP_ANGLE)
                MAX_SWEEP_ANGLE
            else if(sweepAngle < MIN_SWEEP_ANGLE)
                MIN_SWEEP_ANGLE
            else sweepAngle
        }

        /**
         * Checks if the [strokeWidth] value meets the valid range.
         *
         * @param strokeWidth The angle value.
         *
         * @return A valid radius proportion in the range [MIN_STROKE_WIDTH] to [strokeWidth]
         */
        fun getValidStrokeWidth(strokeWidth: Float): Float {
            return if(strokeWidth < MIN_STROKE_WIDTH)
                MIN_STROKE_WIDTH
            else strokeWidth
        }

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
     * Gets the control innerRadius.
     */
    protected open fun getInnerRadius(control: Control): Float = control.innerRadius

    /**
     * The bounds of oval used to define the shape and size of the arc.
     * @param control The control where drawer is used.
     * @return A new [RectF] instance.
     */
    protected open fun getOval(control: Control): RectF {
        val radius = getInnerRadius(control)
        return arcCircle.center.let {
            RectF(it.x - radius, it.y - radius,
                it.x + radius, it.y + radius)
        }
    }

    override fun draw(canvas: Canvas, control: Control) {
        if(control.distanceFromCenter < control.invalidRadius)
            return

        drawArc(canvas, control)
    }

    /**
     * Draw the arc at the [getOval] position.
     *
     * @param canvas The view canvas.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun drawArc(canvas: Canvas, control: Control) {
        arcCircle.apply {
            setCenter(control.immutableCenter)
            radius = getInnerRadius(control)
        }

        val angle: Double = control.anglePosition
        val arcAngle: Double = Math.toDegrees(angle) - sweepAngle / 2

        paint.apply {
            shader = getPaintShader(control, angle, arcAngle)
            style = Paint.Style.STROKE
        }

        val oval = getOval(control)
        canvas.drawArc(oval, arcAngle.toFloat(), sweepAngle, false, paint)
        drawArcArrow(canvas, control, angle)
    }

    /**
     * Draw the arc arrow in the center of the arc pointing outward.
     *
     * @param canvas The view canvas.
     * @param control The [Control] from where the drawer is used.
     * @param angle The angle formed between the current position and the center measured in the range of 0 to 2PI radians clockwise.
     */
    protected open fun drawArcArrow(canvas: Canvas, control: Control, angle: Double) {
        arcCircle.apply {
            radius += strokeWidth
            val position = parametricPositionFrom(angle)
            paint.apply {
                color = colors.accent
                style = Paint.Style.FILL_AND_STROKE
            }

            val arrowSweepAngle = Math.toDegrees(angle) - 90
            position.apply {
                canvas.drawArrow(x, y, arrowSweepAngle.toFloat(), strokeWidth, paint)
            }
        }
    }


    /**
     * The [RadialGradient] for the arc paint.
     *
     * @param angle The angle formed between the current position and the center measured in the range of 0 to 2PI radians clockwise.
     * @param arcAngle The start arc sweep angle measured in the range 0 to 360 degrees clockwise
     */
    protected open fun getPaintShader(control: Control, angle: Double, arcAngle: Double): Shader {
        val position = arcCircle.parametricPositionFrom(control.anglePosition)
        return RadialGradient(
            position.x, position.y,
            getInnerRadius(control),
            intArrayOf(colors.accent, colors.primary),
            null,
            Shader.TileMode.CLAMP
        )
    }
}