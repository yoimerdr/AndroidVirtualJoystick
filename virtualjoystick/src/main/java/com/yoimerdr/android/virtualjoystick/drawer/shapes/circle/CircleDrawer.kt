package com.yoimerdr.android.virtualjoystick.drawer.shapes.circle

import android.graphics.Canvas
import android.graphics.RadialGradient
import android.graphics.Shader
import androidx.annotation.ColorInt
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.drawer.core.ControlDrawer
import com.yoimerdr.android.virtualjoystick.drawer.core.DrawerRadius
import com.yoimerdr.android.virtualjoystick.drawer.core.EmptyDrawer
import com.yoimerdr.android.virtualjoystick.drawer.core.ColorfulProperties
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.position.ImmutablePosition
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * A [ControlDrawer] that draws a circle.
 * */
open class CircleDrawer(
    override val properties: CircleProperties,
) : EmptyDrawer() {

    /**
     * @param colors The colors for the drawer.
     * @param radius The the circle radius.
     * @param isBounded Indicates whether the maximum distance is bounded.
     */
    @JvmOverloads
    constructor(
        colors: ColorsScheme,
        radius: DrawerRadius,
        isBounded: Boolean = true,
    ) : this(CircleProperties(colors, radius, isBounded))

    /**
     * @param colors The colors for the drawer.
     * @param radius The the circle radius.
     * @param isBounded Indicates whether the maximum distance is bounded.
     */
    open class CircleProperties @JvmOverloads constructor(
        colors: ColorsScheme,
        /**
         * The circle radius.
         * */
        var radius: DrawerRadius,
        /**
         * Indicates whether the maximum distance is bounded.
         * */
        var isBounded: Boolean = true,
    ) : ColorfulProperties(colors)

    companion object {

        /**
         * Creates a [CircleDrawer] with a radius based on a ratio.
         * @param colors The colors for the drawer.
         * @param ratio The ratio value.
         * @param isBounded Indicates whether the maximum distance is bounded.
         *
         * @see [DrawerRadius.Ratio]
         * */
        @JvmStatic
        @JvmOverloads
        fun withRatio(
            colors: ColorsScheme,
            ratio: Float,
            isBounded: Boolean = true,
        ) = CircleDrawer(
            colors,
            DrawerRadius.Ratio(ratio),
            isBounded
        )

        /**
         * Creates a [CircleDrawer] with a radius based on a ratio.
         * @param color The color for the drawer.
         * @param ratio The ratio value.
         * @param isBounded Indicates whether the maximum distance is bounded.
         *
         * @see [DrawerRadius.Ratio]
         * */
        @JvmStatic
        @JvmOverloads
        fun withRatio(
            @ColorInt
            color: Int,
            ratio: Float,
            isBounded: Boolean = true,
        ) = withRatio(
            ColorsScheme(color),
            ratio,
            isBounded
        )

        /**
         * Creates a [CircleDrawer] with a radius based on a radius.
         * @param colors The colors for the drawer.
         * @param radius The radius value.
         * @param isBounded Indicates whether the maximum distance is bounded.
         *
         * @see [DrawerRadius.Fixed]
         * */
        @JvmStatic
        @JvmOverloads
        fun withRadius(
            colors: ColorsScheme,
            radius: Float,
            isBounded: Boolean = true,
        ) = CircleDrawer(
            colors,
            DrawerRadius.Fixed(radius),
            isBounded
        )

        /**
         * Creates a [CircleDrawer] with a radius based on a radius.
         * @param color The color for the drawer.
         * @param radius The radius value.
         * @param isBounded Indicates whether the maximum distance is bounded.
         *
         * @see [DrawerRadius.Fixed]
         * */
        @JvmStatic
        @JvmOverloads
        fun withRadius(
            @ColorInt
            color: Int,
            radius: Float,
            isBounded: Boolean = true,
        ) = withRadius(
            ColorsScheme(color),
            radius,
            isBounded
        )
    }

    /**
     * The [Shader] for the drawer paint.
     * @param control The [Control] from where the drawer is used.
     * @param position The position where the center of the circle is.
     */
    protected open fun getPaintShader(control: Control, position: ImmutablePosition): Shader {
        return position.let {
            val (primaryColor, accentColor) = properties.colors

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
    protected open fun getCircleRadius(control: Control): Double = properties.radius.getValue(control)

    override fun getMaxDistance(control: Control): Double = control.radius.let {
        if (properties.isBounded)
            it - getCircleRadius(control)
        else it
    }

    /**
     * Gets the current position where the control is located
     * and that the drawer will take as center to draw the circle.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getPosition(control: Control): ImmutablePosition {
        val maxRadius = getMaxDistance(control)
        return if (control.distance > maxRadius) {
            Circle.fromImmutableCenter(maxRadius, control.center)
                .parametricPositionOf(control.angle)
        } else control.position
    }

    override fun onDraw(canvas: Canvas, control: Control) {
        val position: ImmutablePosition = getPosition(control)
        val paint = properties.paint

        paint.shader = getPaintShader(control, position)
        position.apply {
            canvas.drawCircle(x, y, getCircleRadius(control).toFloat(), paint)
        }
    }
}