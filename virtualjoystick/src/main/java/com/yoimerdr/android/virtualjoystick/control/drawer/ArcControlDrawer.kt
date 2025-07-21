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
import com.yoimerdr.android.virtualjoystick.geometry.position.ImmutablePosition
import com.yoimerdr.android.virtualjoystick.geometry.position.Position
import com.yoimerdr.android.virtualjoystick.geometry.factory.RectFFactory
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * A [ControlDrawer] that draws an arc.
 * Draws the arc positioned almost in [Control.parametricPosition].
 */
open class ArcControlDrawer(
    /**
     * The arc drawer properties.
     */
   private val properties: ArcProperties
) : ColorfulControlDrawer(properties) {

    /**
     * @param colors The colors for the drawer.
     * @param strokeWidth The stroke width of the paint.
     * @param sweepAngle The arc sweep angle.
     */
    constructor(colors: ColorsScheme, strokeWidth: Float, sweepAngle: Float) : this(ArcProperties(colors, strokeWidth, sweepAngle))

    /**
     * @param color The unique initial color for the drawer.
     * @param strokeWidth The stroke width of the paint.
     * @param sweepAngle The arc sweep angle.
     */
    constructor(@ColorInt color: Int, strokeWidth: Float, sweepAngle: Float) : this(ColorsScheme(color), strokeWidth, sweepAngle)

    /**
     * A [Circle] representation for the arc position.
     */
    protected open val arcCircle: Circle = Circle(1f, Position())

    init {
        properties.apply {
            this.sweepAngle = getSweepAngle(sweepAngle)
            this.strokeWidth = getStrokeWidth(strokeWidth)
        }

        paint.apply {
            style = Paint.Style.STROKE
            color = colors.primary
            this.strokeWidth = properties.strokeWidth
        }
    }

    var strokeWidth: Float
        /**
         * Gets the stroke width of the paint.
         */
        get() = properties.strokeWidth
        /**
         * Sets the stroke width of the paint.
         *
         * @param strokeWidth The new stroke width. The minimum value must be [MIN_STROKE_WIDTH].
         */
        set(strokeWidth) {
            properties.strokeWidth = getStrokeWidth(strokeWidth)
        }

    var sweepAngle: Float
        /**
         * Gets the arc sweep angle.
         */
        get() = properties.sweepAngle
        /**
         * Sets the arc sweep angle.
         *
         * @param angle The new sweep angle. Must be a sexagesimal degree in the range [MIN_SWEEP_ANGLE] to [MAX_SWEEP_ANGLE].
         */
        set(angle) {
            properties.sweepAngle = getSweepAngle(angle)
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
         * @param sweepAngle The angle (degrees) value.
         *
         * @return A valid sexagesimal degree value in the range [MIN_SWEEP_ANGLE] to [MAX_SWEEP_ANGLE].
         */
        @JvmStatic
        @FloatRange(from = MIN_SWEEP_ANGLE.toDouble(), to = MAX_SWEEP_ANGLE.toDouble())
        fun getSweepAngle(sweepAngle: Float): Float {
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
        fun getStrokeWidth(strokeWidth: Float): Float {
            return strokeWidth.coerceAtLeast(MIN_STROKE_WIDTH)
        }

        /**
         * Creates a path for an simple arrow.
         *
         * @param x The x-coordinate where the arrow will be.
         * @param y The y-coordinate where the arrow will be.
         * @param length The arrow length.
         *
         * @return The path of the arrow. Already closed.
         */
        @JvmStatic
        fun pathArrow(x: Float, y: Float, length: Float): Path {
            return Path().apply {
                moveTo(x, y)
                lineTo(x + length, y - length)
                lineTo(x, y - length / 2)
                lineTo(x - length, y - length)
                close()
            }
        }

        /**
         * Creates a path for an simple arrow.
         *
         * @param position The position where the arrow will be.
         * @param length The arrow length.
         *
         * @return The path of the arrow. Already closed.
         */
        @JvmStatic
        fun pathArrow(position: ImmutablePosition, length: Float): Path {
            return position.let {
                pathArrow(it.x, it.y, length)
            }
        }

        /**
         * Draws an arrow path with the give canvas.
         * @param canvas The canvas on which to draw the path.
         * @param x The x-coordinate where the arrow will be.
         * @param y The y-coordinate where the arrow will be.
         * @param arrowLength The arrow length.
         * @param rotateAngle The angle to rotate the canvas.
         * @param paint The paint to be used on the canvas.
         * @see [pathArrow]
         * @see [Canvas.rotate]
         */
        @JvmStatic
        fun drawArrow(canvas: Canvas, x: Float, y: Float, arrowLength: Float, rotateAngle: Float, paint: Paint) {
            val arrowPath = pathArrow(x, y, arrowLength)
            canvas.apply {
                save()
                rotate(rotateAngle, x, y)
                drawPath(arrowPath, paint)
                restore()
            }
        }

        /**
         * Draws an arrow path with the give canvas.
         * @param canvas The canvas on which to draw the path.
         * @param x The x-coordinate where the arrow will be.
         * @param y The y-coordinate where the arrow will be.
         * @param arrowLength The arrow length.
         * @param paint The paint to be used on the canvas.
         * @see [pathArrow]
         * @see [Canvas.rotate]
         */
        @JvmStatic
        fun drawArrow(canvas: Canvas, x: Float, y: Float, arrowLength: Float, paint: Paint) {
            drawArrow(canvas, x, y, arrowLength, 0f, paint)
        }

        /**
         * Draws an arrow path with the give canvas.
         * @param canvas The canvas on which to draw the path.
         * @param position The position where the arrow will be.
         * @param arrowLength The arrow length.
         * @param rotateAngle The angle to rotate the canvas.
         * @param paint The paint to be used on the canvas.
         * @see [pathArrow]
         * @see [Canvas.rotate]
         */
        @JvmStatic
        fun drawArrow(canvas: Canvas, position: ImmutablePosition, arrowLength: Float, rotateAngle: Float, paint: Paint) {
            position.apply {
                drawArrow(canvas, x, y, arrowLength, rotateAngle, paint)
            }
        }

        /**
         * Draws an arrow path with the give canvas.
         * @param canvas The canvas on which to draw the path.
         * @param position The position where the arrow will be.
         * @param arrowLength The arrow length.
         * @param paint The paint to be used on the canvas.
         * @see [pathArrow]
         * @see [Canvas.rotate]
         */
        @JvmStatic
        fun drawArrow(canvas: Canvas, position: ImmutablePosition, arrowLength: Float, paint: Paint) {
            drawArrow(canvas, position, arrowLength, 0f, paint)
        }
    }

    /**
     * Gets the distance value between the arc position and the control center.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getDistance(control: Control): Double = control.radius - strokeWidth * 2

    /**
     * The bounds of oval used to define the shape and size of the arc.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getOval(control: Control): RectF {
        return RectFFactory.fromCircle(arcCircle)
    }

    override fun draw(canvas: Canvas, control: Control) {
        if(control.distance < control.invalidRadius)
            return

        drawShapes(canvas, control)
    }

    /**
     * Draws the arc and arc arrow shapes.
     *
     * @param canvas The view canvas.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun drawShapes(canvas: Canvas, control: Control) {
        arcCircle.apply {
            setCenter(control.center)
            radius = getDistance(control)
        }

        val angle: Double = control.angle
        val startAngle: Double = Math.toDegrees(angle) - sweepAngle / 2

        paint.apply {
            shader = getPaintShader(control, angle, startAngle)
            style = Paint.Style.STROKE
        }

        drawArc(canvas, control, startAngle.toFloat())
        drawArrow(canvas, control, angle)
    }

    /**
     * Draws the arc shape.
     *
     * @param canvas The view canvas.
     * @param control The [Control] from where the drawer is used.
     * @param startArcAngle The starting angle (degrees) where the arc begins.
     *
     * @see [getOval]
     */
    protected open fun drawArc(canvas: Canvas, control: Control, startArcAngle: Float) {
        val oval = getOval(control)
        canvas.drawArc(oval, startArcAngle, sweepAngle, false, paint)
    }

    /**
     * Draws the arc arrow in the center of the arc pointing outward.
     *
     * @param canvas The view canvas.
     * @param control The [Control] from where the drawer is used.
     * @param angle The angle formed between the current position and the center in the range of 0 to 2PI radians clockwise.
     */
    protected open fun drawArrow(canvas: Canvas, control: Control, angle: Double) {
        arcCircle.apply {
            radius += strokeWidth
            val position = parametricPositionOf(angle)
            paint.apply {
                color = colors.accent
                style = Paint.Style.FILL_AND_STROKE
            }

            val arrowSweepAngle = Math.toDegrees(angle) - 90
            drawArrow(canvas, position, strokeWidth, arrowSweepAngle.toFloat(), paint)
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
        val position = arcCircle.parametricPositionOf(control.angle)
        return RadialGradient(
            position.x, position.y,
            getDistance(control).toFloat(),
            colorsArray,
            null,
            Shader.TileMode.CLAMP
        )
    }
}