package com.yoimerdr.android.virtualjoystick.control.drawer

import android.graphics.Canvas
import android.graphics.RadialGradient
import android.graphics.Shader
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.ImmutablePosition
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

    constructor(colors: ColorsScheme, proportion: Float) : this(CircleProperties(colors, proportion))
    constructor(@ColorInt color: Int, proportion: Float) : this(ColorsScheme(color), proportion)

    /**
     * The circle radius proportion.
     *
     * Must be a value in the range from [MIN_RADIUS_PROPORTION] to [MAX_RADIUS_PROPORTION]
     */
    var proportion: Float
        get() = properties.proportion
        set(value) {
            properties.proportion = getRadiusProportion(value)
        }

    init {
        this.proportion = properties.proportion
    }

    open class CircleProperties(colors: ColorsScheme, var proportion: Float) : ColorfulProperties(colors)

    companion object {
        /**
         * The minimum valid radius proportion.
         */
        const val MIN_RADIUS_PROPORTION = 0.1f

        /**
         * The maximum valid radius proportion.
         */
        const val MAX_RADIUS_PROPORTION = 0.80f

        /**
         * Checks if the [proportion] value meets the valid range.
         *
         * @param proportion The proportion value.
         *
         * @return A valid radius proportion in the range [MIN_RADIUS_PROPORTION] to [MAX_RADIUS_PROPORTION]
         */
        @JvmStatic
        @FloatRange(from = MIN_RADIUS_PROPORTION.toDouble(), to = MAX_RADIUS_PROPORTION.toDouble())
        fun getRadiusProportion(proportion: Float): Float {
            return proportion.coerceIn(MIN_RADIUS_PROPORTION, MAX_RADIUS_PROPORTION)
        }
    }

    /**
     * The [Shader] for the drawer paint.
     * @param control The [Control] from where the drawer is used.
     * @param position The current (or parametric) position where the control is.
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
    protected open fun getCircleRadius(control: Control): Double = control.viewRadius * proportion

    /**
     * Gets the maximum radius to where the drawer center position can be.
     */
    protected open fun getMaxCircleRadius(control: Control): Double = control.viewRadius - getCircleRadius(control)

    /**
     * Gets the current position where the control is located
     * and that the drawer will take as center to draw the circle.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getPosition(control: Control): ImmutablePosition {
        val maxRadius = getMaxCircleRadius(control)
        return if(control.distanceFromCenter > maxRadius) {
            Circle.fromImmutableCenter(maxRadius, control.center)
                .parametricPositionOf(control.anglePosition)
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