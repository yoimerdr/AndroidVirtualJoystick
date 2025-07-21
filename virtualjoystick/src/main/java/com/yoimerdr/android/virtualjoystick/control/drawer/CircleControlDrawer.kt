package com.yoimerdr.android.virtualjoystick.control.drawer

import android.graphics.Canvas
import android.graphics.RadialGradient
import android.graphics.Shader
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.position.ImmutablePosition
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * A [ControlDrawer] that draws the given circle.
 */
open class CircleControlDrawer(
    /**
     * The circle drawer properties.
     */
    private val properties: CircleProperties
) : ColorfulControlDrawer(properties) {

    /**
     * @param colors The colors for the drawer.
     * @param ratio The ratio value for the circle radius length.
     */
    constructor(colors: ColorsScheme, ratio: Float) : this(CircleProperties(colors, ratio))

    /**
     * @param color The unique initial color for the drawer.
     * @param ratio The ratio value for the circle radius length.
     */
    constructor(@ColorInt color: Int, ratio: Float) : this(ColorsScheme(color), ratio)

    init {
        properties.apply {
            ratio = getRadiusRatio(ratio)
        }
    }

    var ratio: Float
        /**
         * Gets the circle radius ratio.
         */
        get() = properties.ratio
        /**
         * Sets the circle radius ratio.
         * @param ratio The new circle radius ratio. Must be a value in the range from [MIN_RADIUS_RATIO] to [MAX_RADIUS_RATIO]
         */
        set(ratio) {
            properties.ratio = getRadiusRatio(ratio)
        }

    open class CircleProperties(colors: ColorsScheme, var ratio: Float) : ColorfulProperties(colors)

    companion object {
        /**
         * The minimum valid radius ratio value.
         */
        const val MIN_RADIUS_RATIO = 0.1f

        /**
         * The maximum valid radius ratio value.
         */
        const val MAX_RADIUS_RATIO = 0.80f

        /**
         * Checks if the [ratio] value meets the valid range.
         *
         * @param ratio The ratio value.
         *
         * @return A valid radius ratio in the range [MIN_RADIUS_RATIO] to [MAX_RADIUS_RATIO]
         */
        @JvmStatic
        @FloatRange(from = MIN_RADIUS_RATIO.toDouble(), to = MAX_RADIUS_RATIO.toDouble())
        fun getRadiusRatio(ratio: Float): Float {
            return ratio.coerceIn(MIN_RADIUS_RATIO, MAX_RADIUS_RATIO)
        }
    }

    /**
     * The [Shader] for the drawer paint.
     * @param control The [Control] from where the drawer is used.
     * @param position The position where the center of the circle is.
     */
    protected open fun getPaintShader(control: Control, position: ImmutablePosition): Shader {
        return position.let {
            RadialGradient(
                it.x, it.y, getCircleRadius(control).toFloat(),
                intArrayOf(accentColor, primaryColor),
                null,
                Shader.TileMode.CLAMP
            )
        }
    }

    /**
     * Gets the circle radius.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getCircleRadius(control: Control): Double = control.radius * ratio

    /**
     * Gets the maximum distance to where the center position of the circle can be.
     */
    protected open fun getMaxDistance(control: Control): Double = control.radius - getCircleRadius(control)

    /**
     * Gets the current position where the control is located
     * and that the drawer will take as center to draw the circle.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getPosition(control: Control): ImmutablePosition {
        val maxRadius = getMaxDistance(control)
        return if(control.distance > maxRadius) {
            Circle.fromImmutableCenter(maxRadius, control.center)
                .parametricPositionOf(control.angle)
        }
        else control.position
    }

    override fun draw(canvas: Canvas, control: Control) {
        val position: ImmutablePosition = getPosition(control)
        paint.shader = getPaintShader(control, position)
        position.apply {
            canvas.drawCircle(x, y, getCircleRadius(control).toFloat(), paint)
        }
    }
}