package com.yoimerdr.android.virtualjoystick.control.drawer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.enums.DirectionType
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.Plane
import com.yoimerdr.android.virtualjoystick.geometry.factory.RectFFactory
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 *
 */
open class HighlightControlDrawer(
    private val properties: HighlightProperties
) : ColorfulControlDrawer(properties) {

    override var primaryColor: Int
        get() = super.primaryColor
        set(@ColorInt color) {
            super.primaryColor = if(isStrictColor) color else getAlphaRangeColor(color)
        }

    /**
     * Verify that the drawer color should be taken as it was set.
     */
    var isStrictColor: Boolean
        get() = properties.strictColor
        set(value) {
            if(isStrictColor != value) {
                properties.strictColor = value
                primaryColor = primaryColor
            }
        }

    /**
     * The inner radius proportion.
     *
     * Must be a value in the range from [MIN_INNER_RADIUS_PROPORTION] to [MAX_INNER_RADIUS_PROPORTION]
     */
    var innerProportion: Float
        get() = properties.innerProportion
        set(value) {
            properties.innerProportion = HighlightControlDrawer.getInnerRadiusProportion(value)
        }

    /**
     * The last current quadrant where the control was.
     */
    protected var lastQuadrant: Int = 0
        private set

    /**
     * The [Path] to draw the filled shape of the arcs.
     */
    protected val arcsPath = Path()

    init {
        if(!isStrictColor) {
            this.colors.apply {
                primary = getAlphaRangeColor(primary)
            }
            paint.apply {
                shader = null
                style = Paint.Style.FILL
                color = primaryColor
            }
        }
    }

    constructor(@ColorInt color: Int, strictColor: Boolean, innerRadiusProportion: Float) : this(HighlightProperties(color, strictColor, innerRadiusProportion))

    constructor(@ColorInt color: Int, innerRadiusProportion: Float) : this(color, false, innerRadiusProportion)

    class HighlightProperties(
        @ColorInt color: Int,
        var strictColor: Boolean,
        var innerProportion: Float
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

        const val MIN_INNER_RADIUS_PROPORTION = 0.1f
        const val MAX_INNER_RADIUS_PROPORTION = 0.8f

        /**
         * Checks if the [alpha] value meets the valid range.
         *
         * @param alpha The alpha channel value.
         *
         * @return A valid radius proportion in the range [MIN_ALPHA] to [MAX_ALPHA]
         */
        @JvmStatic
        @IntRange(from = MIN_ALPHA.toLong(), to = MAX_ALPHA.toLong())
        fun getAlphaValue(alpha: Int): Int {
            return alpha.coerceIn(MIN_ALPHA, MAX_ALPHA)
        }

        /**
         * Changes the alpha channel value of the given color to the one returned by [getAlphaValue].
         * @param color The color to change the alpha channel value.
         * @return A new [ColorInt] with the new alpha channel value.
         */
        @JvmStatic
        @ColorInt
        fun getAlphaRangeColor(@ColorInt color: Int): Int {
            val alpha = getAlphaValue(Color.alpha(color))
            return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
        }

        /**
         * Checks if the [proportion] value meets the valid range.
         *
         * @param proportion The proportion value.
         *
         * @return A valid radius proportion in the range [MIN_INNER_RADIUS_PROPORTION] to [MAX_INNER_RADIUS_PROPORTION]
         */
        @JvmStatic
        @FloatRange(from = MIN_INNER_RADIUS_PROPORTION.toDouble(), to = MIN_INNER_RADIUS_PROPORTION.toDouble())
        fun getInnerRadiusProportion(proportion: Float): Float {
            return proportion.coerceIn(MIN_INNER_RADIUS_PROPORTION, MAX_INNER_RADIUS_PROPORTION)
        }
    }

    override fun draw(canvas: Canvas, control: Control) {
        if(control.distanceFromCenter < control.invalidRadius)
            return

        drawFillArc(canvas, control)
    }

    /**
     * Draws the current [arcsPath].
     * @param canvas The view canvas.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun drawFillArc(canvas: Canvas, control: Control) {
        getCurrentQuadrant(control).also {
            if(it != lastQuadrant) {
                lastQuadrant = it
                fillPath(control, it, getSweepAngleOf(control.directionType))
            }
        }
        canvas.drawPath(arcsPath, paint)
    }

    /**
     * Gets the distance value between the outer arc position and the control center.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getOuterArcDistance(control: Control): Double = control.viewRadius

    /**
     * Gets the distance value between the inner arc position and the control center.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getInnerArcDistance(control: Control): Double = control.viewRadius * innerProportion

    /**
     * Method for reset and fill the current [arcsPath]
     *
     * @param control The [Control] from where the drawer is used.
     * @param quadrant The current quadrant where the control is.
     * @param sweepAngle The sweep angle (degrees) for the control direction type.
     */
    protected open fun fillPath(control: Control, quadrant: Int, sweepAngle: Float) {
        if(quadrant == 0)
            return

        arcsPath.reset()

        val outerCircle = Circle.fromImmutableCenter(getOuterArcDistance(control), control.center)
        val innerCircle = Circle(getInnerArcDistance(control), outerCircle.center)

        val outerOval = RectFFactory.fromCircle(outerCircle)
        val innerOval = RectFFactory.fromCircle(innerCircle)

        val startAngle = (quadrant - 1) * sweepAngle - sweepAngle / 2

        var posAngle = Math.toRadians(startAngle.toDouble())

        innerCircle.parametricPositionOf(posAngle).apply {
            arcsPath.moveTo(x, y)
        }

        outerCircle.parametricPositionOf(posAngle).apply {
            arcsPath.lineTo(x, y)
        }

        arcsPath.apply {
            arcTo(outerOval, startAngle, sweepAngle)
            posAngle = Math.toRadians((startAngle + sweepAngle).toDouble())
            outerCircle.parametricPositionOf(posAngle).apply {
                lineTo(x, y)
            }
            arcTo(innerOval, startAngle + sweepAngle, -sweepAngle)
            close()
        }
    }

    /**
     * Gets the sweep angle for give direction type.
     * @param directionType The control direction type.
     * @return 90 if [directionType] is [DirectionType.FOUR]; Otherwise, 45
     */
    protected fun getSweepAngleOf(directionType: DirectionType): Float {
        return if(directionType == DirectionType.FOUR) 90f
        else 45f
    }

    /**
     * Gets the current quadrant for the control position.
     *
     * @param control The [Control] from where the drawer is used.
     * @return If control direction type is [DirectionType.FOUR], a value in the range 1 to 4;
     * otherwise, a value in the range 1 to 8.
     */
    protected open fun getCurrentQuadrant(control: Control): Int {
        val angle = Math.toDegrees(control.anglePosition)
        return if(control.directionType == DirectionType.FOUR)
            Plane.quadrantOf(angle, true)
        else Plane.quadrantOf(angle, Plane.MaxQuadrants.EIGHT, true)
    }
}