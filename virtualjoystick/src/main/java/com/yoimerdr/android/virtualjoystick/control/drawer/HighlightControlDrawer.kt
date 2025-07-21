package com.yoimerdr.android.virtualjoystick.control.drawer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.Plane
import com.yoimerdr.android.virtualjoystick.geometry.factory.RectFFactory
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * A [ControlDrawer] that draws a highlighted circular trapezoid.
 */
open class HighlightControlDrawer(
    /**
     * The highlight drawer properties.
     */
    private val properties: HighlightProperties
) : ColorfulControlDrawer(properties) {

    /**
     * @param color The highlight color. It will be consider as the [ColorfulControlDrawer.primaryColor].
     * @param strictColor If true, the [color] will be used as such, without modifying its alpha channel.
     * @param innerRadiusRatio A ratio value to calculate the inner distance from the center to the inner arc.
     */
    constructor(@ColorInt color: Int, strictColor: Boolean, innerRadiusRatio: Float) : this(HighlightProperties(color, strictColor, innerRadiusRatio))

    /**
     * @param color The highlight color. It will be consider as the [ColorfulControlDrawer.primaryColor]. Also, its alpha channel will be changed.
     * @param innerRadiusRatio A ratio value to calculate the inner distance from the center to the inner arc.
     */
    constructor(@ColorInt color: Int, innerRadiusRatio: Float) : this(color, false, innerRadiusRatio)


    /**
     * The last current quadrant where the control was.
     */
    protected var lastQuadrant: Int = 0
        private set

    /**
     * The [Path] to draw the filled shape of the circular trapezoid.
     */
    protected val trapezoidPath = Path()

    init {
        if(!isStrictColor) {
            this.colors.apply {
                primary = getAlphaRangedColor(primary)
            }
            paint.apply {
                shader = null
                strokeWidth = 0f
                style = Paint.Style.FILL
                color = primaryColor
            }
        }
    }

    override var primaryColor: Int
        get() = super.primaryColor
        /**
         * Sets the primary color of [colors] and paint.
         *
         * If [isStrictColor] is true, the color will be used as such, without modifying its alpha channel.
         * Otherwise, its will be modified with [getAlphaRangedColor]
         *
         * @param color The new primary color.
         */
        set(@ColorInt color) {
            super.primaryColor = if(isStrictColor) color else getAlphaRangedColor(color)
        }


    var isStrictColor: Boolean
        /**
         * Gets the value that determines that the primary color should be taken as set or not.
         */
        get() = properties.strictColor

        /**
         * Sets the value that determines that the (new) primary color should be taken as set or not.
         */
        set(isStrictColor) {
            if(this.isStrictColor != isStrictColor) {
                properties.strictColor = isStrictColor
                primaryColor = primaryColor
            }
        }

    var innerRatio: Float
        /**
         * Gets the inner ratio value to calculate the inner distance from the control center to the inner arc of the trapezoid.
         */
        get() = properties.innerRatio
        /**
         * Sets the inner ratio value.
         * @param ratio The new ratio value. Must be a value in the range from [MIN_INNER_RADIUS_RATIO] to [MAX_INNER_RADIUS_RATIO].
         */
        set(ratio) {
            properties.innerRatio = getInnerRadiusRatio(ratio)
        }

    class HighlightProperties(
        @ColorInt color: Int,
        var strictColor: Boolean,
        var innerRatio: Float
    ) : ColorfulProperties(ColorsScheme(color, Color.TRANSPARENT))

    companion object {
        /**
         * The minimum valid value of the alpha channel.
         */
        const val MIN_ALPHA = 50

        /**
         * The maximum valid value of the alpha channel.
         */
        const val MAX_ALPHA = 102

        /**
         * The minimum ratio value to calculating the inner distance from the center.
         */
        const val MIN_INNER_RADIUS_RATIO = 0.1f

        /**
         * The maximum ratio value to calculating the inner distance from the center.
         */
        const val MAX_INNER_RADIUS_RATIO = 0.8f

        /**
         * Checks if the [alpha] value meets the valid range.
         *
         * @param alpha The alpha channel value.
         *
         * @return A valid alpha value in the range [MIN_ALPHA] to [MAX_ALPHA]
         */
        @JvmStatic
        @IntRange(from = MIN_ALPHA.toLong(), to = MAX_ALPHA.toLong())
        fun getAlphaRanged(alpha: Int): Int {
            return alpha.coerceIn(MIN_ALPHA, MAX_ALPHA)
        }

        /**
         * Changes the alpha channel value of the given color to the one returned by [getAlphaRanged].
         * @param color The color to change the alpha channel value.
         * @return A new [ColorInt] with the new alpha channel value.
         */
        @JvmStatic
        @ColorInt
        fun getAlphaRangedColor(@ColorInt color: Int): Int {
            val alpha = getAlphaRanged(Color.alpha(color))
            return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
        }

        /**
         * Checks if the [ratio] value meets the valid range.
         *
         * @param ratio The ratio value.
         *
         * @return A valid radius ratio value in the range [MIN_INNER_RADIUS_RATIO] to [MAX_INNER_RADIUS_RATIO]
         */
        @JvmStatic
        @FloatRange(from = MIN_INNER_RADIUS_RATIO.toDouble(), to = MIN_INNER_RADIUS_RATIO.toDouble())
        fun getInnerRadiusRatio(ratio: Float): Float {
            return ratio.coerceIn(MIN_INNER_RADIUS_RATIO, MAX_INNER_RADIUS_RATIO)
        }
    }

    override fun draw(canvas: Canvas, control: Control) {
        if(control.distance < control.invalidRadius)
            return

        drawTrapezoid(canvas, control)
    }

    /**
     * Draws the current [trapezoidPath].
     * @param canvas The view canvas.
     * @param control The [Control] from where the drawer is used.
     * @see [fillTrapezoid]
     */
    protected open fun drawTrapezoid(canvas: Canvas, control: Control) {
        getCurrentQuadrant(control).also {
            if(it != lastQuadrant) {
                lastQuadrant = it
                fillTrapezoid(control, it, getSweepAngleOf(control.directionType))
            }
        }
        canvas.drawPath(trapezoidPath, paint)
    }

    /**
     * Gets the distance value between the outer arc of the trapezoid and the control center.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getOuterDistance(control: Control): Double = control.radius

    /**
     * Gets the distance value between the inner arc of the trapezoid and the control center.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getInnerDistance(control: Control): Double = (control.radius * innerRatio)

    /**
     * Resets and fills the path of the circular trapezoid.
     *
     * @param control The [Control] from where the drawer is used.
     * @param quadrant The current quadrant where the control is.
     * @param sweepAngle The sweep angle (degrees) for the circular trapezoid.
     */
    protected open fun fillTrapezoid(control: Control, quadrant: Int, sweepAngle: Float) {
        if(quadrant == 0)
            return

        trapezoidPath.reset()

        val outerCircle = Circle.fromImmutableCenter(getOuterDistance(control), control.center)
        val innerCircle = Circle(getInnerDistance(control), outerCircle.center)

        val outerOval = RectFFactory.fromCircle(outerCircle)
        val innerOval = RectFFactory.fromCircle(innerCircle)

        val startAngle = (quadrant - 1) * sweepAngle - sweepAngle / 2.0

        var posAngle = Math.toRadians(startAngle)

        innerCircle.parametricPositionOf(posAngle).apply {
            trapezoidPath.moveTo(x, y)
        }

        outerCircle.parametricPositionOf(posAngle).apply {
            trapezoidPath.lineTo(x, y)
        }

        trapezoidPath.apply {
            arcTo(outerOval, startAngle.toFloat(), sweepAngle)
            posAngle = Math.toRadians((startAngle + sweepAngle))
            outerCircle.parametricPositionOf(posAngle).apply {
                lineTo(x, y)
            }
            arcTo(innerOval, startAngle.toFloat() + sweepAngle, -sweepAngle)
            close()
        }
    }

    /**
     * Gets the sweep angle for give direction type.
     * @param directionType The control direction type.
     * @return 90 if [directionType] is [Control.DirectionType.SIMPLE]; Otherwise, 45
     */
    protected open fun getSweepAngleOf(directionType: Control.DirectionType): Float {
        return if(directionType == Control.DirectionType.SIMPLE) 90f
        else 45f
    }

    /**
     * Gets the current quadrant of the control position.
     *
     * @param control The [Control] from where the drawer is used.
     * @return If control direction type is [Control.DirectionType.SIMPLE], a value in the range 1 to 4;
     * otherwise, a value in the range 1 to 8.
     */
    protected open fun getCurrentQuadrant(control: Control): Int {
        val angle = Math.toDegrees(control.angle)
        return if(control.directionType == Control.DirectionType.SIMPLE)
            Plane.quadrantOf(angle, true)
        else Plane.quadrantOf(angle, Plane.MaxQuadrants.EIGHT, true)
    }
}