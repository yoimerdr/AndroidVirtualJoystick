package com.yoimerdr.android.virtualjoystick.drawer.shapes.arc

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
import androidx.core.graphics.withSave
import com.yoimerdr.android.virtualjoystick.drawer.core.ControlDrawer
import com.yoimerdr.android.virtualjoystick.drawer.core.ColorfulProperties
import com.yoimerdr.android.virtualjoystick.drawer.core.EmptyDrawer

/**
 * A [ControlDrawer] that draws an arc.
 */
open class ArcDrawer(
    /**
     * The arc drawer properties.
     */
    override val properties: ArcProperties,
) : EmptyDrawer() {

    /**
     * @param colors The colors for the drawer.
     * @param strokeWidth The stroke width of the paint.
     * @param sweepAngle The arc sweep angle.
     * @param isBounded Indicates whether the maximum distance is bounded.
     */
    @JvmOverloads
    constructor(
        colors: ColorsScheme,
        @FloatRange(
            from = MIN_STROKE_WIDTH.toDouble()
        )
        strokeWidth: Float,
        @FloatRange(
            from = MIN_SWEEP_ANGLE.toDouble(),
            to = MAX_SWEEP_ANGLE.toDouble()
        )
        sweepAngle: Float,
        isBounded: Boolean = true,
    ) : this(
        ArcProperties(
            colors,
            strokeWidth,
            sweepAngle,
            isBounded
        )
    )

    /**
     * @param color The unique initial color for the drawer.
     * @param strokeWidth The stroke width of the paint.
     * @param sweepAngle The arc sweep angle.
     * @param isBounded Indicates whether the maximum distance is bounded.
     */
    @JvmOverloads
    constructor(
        @ColorInt color: Int,
        @FloatRange(
            from = MIN_STROKE_WIDTH.toDouble()
        )
        strokeWidth: Float,
        @FloatRange(
            from = MIN_SWEEP_ANGLE.toDouble(),
            to = MAX_SWEEP_ANGLE.toDouble()
        )
        sweepAngle: Float,
        isBounded: Boolean = true,
    ) : this(
        ColorsScheme(
            color
        ),
        strokeWidth,
        sweepAngle,
        isBounded
    )

    /**
     * A [Circle] representation for the arc position.
     */
    protected open val arcCircle: Circle = Circle(1f, Position())

    /**
     * @param colors The colors for the drawer.
     * @param strokeWidth The stroke width of the paint.
     * @param sweepAngle The arc sweep angle.
     * @param isBounded Indicates whether the maximum distance is bounded.
     */
    open class ArcProperties @JvmOverloads constructor(
        colors: ColorsScheme,
        @FloatRange(
            from = MIN_STROKE_WIDTH.toDouble()
        )
        strokeWidth: Float,
        @FloatRange(
            from = MIN_SWEEP_ANGLE.toDouble(),
            to = MAX_SWEEP_ANGLE.toDouble()
        )
        sweepAngle: Float,
        var isBounded: Boolean = true,
    ) : ColorfulProperties(colors) {

        init {
            paint.apply {
                style = Paint.Style.STROKE
                this.strokeWidth = clampStrokeWidth(strokeWidth)
            }
        }

        /**
         * The sweep angle of the arc.
         * */
        var sweepAngle = clampSweepAngle(sweepAngle)
            /**
             * Sets the sweep angle of the arc.
             *
             * @see [clampSweepAngle]
             * */
            set(value) {
                field = clampSweepAngle(value)
            }

        /**
         * The width of the arc line
         * */
        var strokeWidth = clampStrokeWidth(strokeWidth)
            /**
             * Sets the width of the arc line
             *
             * @see [clampStrokeWidth]
             * */
            set(value) {
                field = clampStrokeWidth(value)
                paint.strokeWidth = field
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
         * Clamps the [sweepAngle] value in the valid range.
         *
         * @param sweepAngle The angle (degrees) value.
         *
         * @return A valid sexagesimal degree value in the range [MIN_SWEEP_ANGLE] to [MAX_SWEEP_ANGLE].
         */
        @JvmStatic
        @FloatRange(from = MIN_SWEEP_ANGLE.toDouble(), to = MAX_SWEEP_ANGLE.toDouble())
        fun clampSweepAngle(sweepAngle: Float): Float {
            return sweepAngle.coerceIn(MIN_SWEEP_ANGLE, MAX_SWEEP_ANGLE)
        }

        /**
         * Clamps the [strokeWidth] value in the valid range.
         *
         * @param strokeWidth The angle value.
         *
         * @return A valid stroke width in the range [MIN_STROKE_WIDTH] to [strokeWidth].
         */
        @JvmStatic
        @FloatRange(from = MIN_SWEEP_ANGLE.toDouble())
        fun clampStrokeWidth(strokeWidth: Float): Float {
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
        fun drawArrow(
            canvas: Canvas,
            x: Float,
            y: Float,
            arrowLength: Float,
            rotateAngle: Float,
            paint: Paint,
        ) {
            val arrowPath = pathArrow(x, y, arrowLength)
            canvas.apply {
                withSave {
                    rotate(rotateAngle, x, y)
                    drawPath(arrowPath, paint)
                }
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
        fun drawArrow(
            canvas: Canvas,
            position: ImmutablePosition,
            arrowLength: Float,
            rotateAngle: Float,
            paint: Paint,
        ) {
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
        fun drawArrow(
            canvas: Canvas,
            position: ImmutablePosition,
            arrowLength: Float,
            paint: Paint,
        ) {
            drawArrow(canvas, position, arrowLength, 0f, paint)
        }
    }


    override fun getMaxDistance(control: Control): Double = control.radius.let {
        if (properties.isBounded)
            it - properties.strokeWidth * 2
        else it
    }

    /**
     * The bounds oval used to define the shape and size of the arc.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getOval(control: Control): RectF {
        return RectFFactory.fromCircle(arcCircle)
    }

    override fun onDraw(canvas: Canvas, control: Control) {
        if (!control.isActive)
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
            radius = getMaxDistance(control)
        }

        val angle: Double = control.angle
        val startAngle: Double = Math.toDegrees(angle) - properties.sweepAngle / 2

        properties
            .paint.apply {
                shader = getPaintShader(control, angle, startAngle)
                color = properties.colors.primary
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
        canvas.drawArc(oval, startArcAngle, properties.sweepAngle, false, properties.paint)
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
            radius += properties.strokeWidth
            val position = parametricPositionOf(angle)

            val paint = properties.paint
            paint.apply {
                color = properties.colors.accent
                style = Paint.Style.FILL_AND_STROKE
            }

            val arrowSweepAngle = Math.toDegrees(angle) - 90
            drawArrow(canvas, position, properties.strokeWidth, arrowSweepAngle.toFloat(), paint)
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
        val (primary, accent) = properties.colors

        return RadialGradient(
            position.x, position.y,
            getMaxDistance(control).toFloat(),
            intArrayOf(accent, primary),
            null,
            Shader.TileMode.CLAMP
        )
    }
}