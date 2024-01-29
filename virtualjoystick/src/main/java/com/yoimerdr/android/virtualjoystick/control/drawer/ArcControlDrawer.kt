package com.yoimerdr.android.virtualjoystick.control.drawer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.Position
import com.yoimerdr.android.virtualjoystick.geometry.factory.RectFFactory
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * A [ControlDrawer] that draws an arc.
 *
 * By default, it draws the arc positioned at the value of the inner radius of [Control].
 */
open class ArcControlDrawer(
   private val properties: ArcProperties
) : ColorfulControlDrawer(properties) {

    constructor(colors: ColorsScheme, strokeWidth: Float, sweepAngle: Float) : this(ArcProperties(colors, strokeWidth, sweepAngle))
    constructor(@ColorInt color: Int, strokeWidth: Float, sweepAngle: Float) : this(ColorsScheme(color), strokeWidth, sweepAngle)
    /**
     * A [Circle] representation for the arc position.
     */
    protected open val arcCircle: Circle = Circle(1f, Position())

    /**
     * Paint stroke width.
     *
     * A value where minimum value can be [MIN_STROKE_WIDTH].
     */
    var strokeWidth: Float
        get() = properties.strokeWidth
        set(value) {
            properties.strokeWidth = getValidStrokeWidth(value)
        }

    /**
     * Arc sweep angle.
     *
     * A sexagesimal degree in the range [MIN_SWEEP_ANGLE] to [MAX_SWEEP_ANGLE].
     */
    var sweepAngle: Float
        get() = properties.sweepAngle
        set(value) {
            properties.sweepAngle = getValidSweepAngle(value)
        }

    init {
        this.sweepAngle = getValidSweepAngle(sweepAngle)
        this.strokeWidth = getValidStrokeWidth(strokeWidth)
        paint.apply {
            style = Paint.Style.STROKE
            color = colors.primary
            this.strokeWidth = this@ArcControlDrawer.strokeWidth
        }
    }

    open class ArcProperties(colors: ColorsScheme, var strokeWidth: Float, var sweepAngle: Float) : ColorfulProperties(colors)

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
         * @return A valid sexagesimal degree value in the range [MIN_SWEEP_ANGLE] to [MAX_SWEEP_ANGLE].
         */
        @JvmStatic
        @FloatRange(from = MIN_SWEEP_ANGLE.toDouble(), to = MAX_SWEEP_ANGLE.toDouble())
        fun getValidSweepAngle(sweepAngle: Float): Float {
            return sweepAngle.coerceIn(MIN_SWEEP_ANGLE, MAX_SWEEP_ANGLE)
        }

        /**
         * Checks if the [strokeWidth] value meets the valid range.
         *
         * @param strokeWidth The angle value.
         *
         * @return A valid stroke width in the range [MIN_STROKE_WIDTH] to [strokeWidth].
         */
        @JvmStatic
        @FloatRange(from = MIN_SWEEP_ANGLE.toDouble())
        fun getValidStrokeWidth(strokeWidth: Float): Float {
            return strokeWidth.coerceAtLeast(MIN_STROKE_WIDTH)
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
     * Gets the distance value between the arc position and the control center.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getArcDistance(control: Control): Double = control.viewRadius - strokeWidth * 2

    /**
     * The bounds of oval used to define the shape and size of the arc.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getOval(control: Control): RectF {
        return RectFFactory.fromCircle(arcCircle)
    }

    override fun draw(canvas: Canvas, control: Control) {
        if(control.distanceFromCenter < control.invalidRadius)
            return

        drawControl(canvas, control)
    }

    /**
     * Draw the arc and arc arrow shapes.
     *
     * @param canvas The view canvas.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun drawControl(canvas: Canvas, control: Control) {
        arcCircle.apply {
            setCenter(control.center)
            radius = getArcDistance(control)
        }

        val angle: Double = control.anglePosition
        val startAngle: Double = Math.toDegrees(angle) - sweepAngle / 2

        paint.apply {
            shader = getPaintShader(control, angle, startAngle)
            style = Paint.Style.STROKE
        }

        drawArc(canvas, control, startAngle.toFloat())
        drawArcArrow(canvas, control, angle)
    }

    /**
     * Draw the arc at the [getOval] position.
     *
     * @param canvas The view canvas.
     * @param control The [Control] from where the drawer is used.
     * @param startArcAngle The starting angle (degrees) where the arc begins
     */
    protected open fun drawArc(canvas: Canvas, control: Control, startArcAngle: Float) {
        val oval = getOval(control)
        canvas.drawArc(oval, startArcAngle, sweepAngle, false, paint)
    }

    /**
     * Draw the arc arrow in the center of the arc pointing outward.
     *
     * @param canvas The view canvas.
     * @param control The [Control] from where the drawer is used.
     * @param angle The angle formed between the current position and the center in the range of 0 to 2PI radians clockwise.
     */
    protected open fun drawArcArrow(canvas: Canvas, control: Control, angle: Double) {
        arcCircle.apply {
            radius += strokeWidth
            val position = parametricPositionOf(angle)
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
     * The [Shader] for the drawer paint.
     *
     * @param control The [Control] from where the drawer is used.
     * @param angle The angle formed between the current position and the center in the range of 0 to 2PI radians clockwise.
     * @param startAngle The starting angle of the arc sweep angle in the range of 0 to 360 degrees clockwise.
     */
    protected open fun getPaintShader(control: Control, angle: Double, startAngle: Double): Shader {
        val position = arcCircle.parametricPositionOf(control.anglePosition)
        return RadialGradient(
            position.x, position.y,
            getArcDistance(control).toFloat(),
            colorsArray,
            null,
            Shader.TileMode.CLAMP
        )
    }
}