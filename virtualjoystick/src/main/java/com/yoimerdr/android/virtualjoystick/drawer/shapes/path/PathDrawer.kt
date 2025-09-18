package com.yoimerdr.android.virtualjoystick.drawer.shapes.path

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.control.Control.Direction.Companion.quadrant
import com.yoimerdr.android.virtualjoystick.drawer.core.SimpleDrawer
import com.yoimerdr.android.virtualjoystick.drawer.core.ColorfulProperties
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * Abstract class for creating path-based drawers.
 * */
abstract class PathDrawer(
    override val properties: PathProperties,
) : SimpleDrawer() {

    protected val path = Path()

    override fun configure() {
        properties.apply {
            if (!isStrictColor) {
                paint.apply {
                    shader = null
                    strokeWidth = 0f
                    style = Paint.Style.FILL
                }
            }
        }
    }

    /**
     * @param color The color of the path.
     * @param isStrictColor Indicates if the color can be modified.
     *
     * @see [clampAlphaColor]
     * */
    open class PathProperties(
        @ColorInt color: Int,
        var isStrictColor: Boolean,
    ) : ColorfulProperties(
        ColorsScheme(
            if (isStrictColor) color else clampAlphaColor(
                color
            ),
            Color.TRANSPARENT
        )
    ) {

        override var primaryColor: Int
            get() = super.primaryColor
            set(value) {
                super.primaryColor = if (isStrictColor)
                    value
                else clampAlphaColor(
                    value
                )
            }

        /**
         * Unused in this class.
         * */
        override var accentColor: Int
            get() = super.accentColor
            set(value) {
                super.accentColor = value
                hasChanged = false
            }
    }

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
         * Clamps the [alpha] value in the valid range.
         *
         * @param alpha The alpha channel value.
         *
         * @return A valid alpha value in the range [MIN_ALPHA] to [MAX_ALPHA]
         */
        @JvmStatic
        @IntRange(from = MIN_ALPHA.toLong(), to = MAX_ALPHA.toLong())
        fun clampAlpha(alpha: Int): Int {
            return alpha.coerceIn(
                MIN_ALPHA,
                MAX_ALPHA
            )
        }

        /**
         * Clamps the alpha value of the given color.
         *
         * @param color The color to change the alpha channel value.
         * @return A new [ColorInt] with the new alpha channel value.
         * @see [clampAlpha]
         */
        @JvmStatic
        @ColorInt
        fun clampAlphaColor(@ColorInt color: Int): Int {
            val alpha =
                clampAlpha(
                    Color.alpha(color)
                )
            return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
        }
    }

    override fun onDraw(canvas: Canvas, control: Control) {
        drawPath(canvas, control)
    }

    override fun onPrepare(canvas: Canvas, control: Control) {
        updatePath(control, lastDirection!!, lastType!!)
    }

    /**
     * Calculate the maximum distance from the center that can be reached.
     *
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getOuterDistance(control: Control): Double = control.radius

    /**
     * Calculates the distance value between the inner position and the center.
     *
     * @param control The [Control] from where the drawer is used.
     */
    protected abstract fun getInnerDistance(control: Control): Double

    /**
     * Called to update the path of the drawer.
     *
     * @param control The [Control] from where the drawer is used.
     * @param direction The current [Control.Direction] of the control.
     * @param directionType The current [Control.DirectionType] of the control.
     * */
    abstract fun updatePath(
        control: Control,
        direction: Control.Direction,
        directionType: Control.DirectionType,
    )

    /**
     * Draws the path.
     * */
    open fun drawPath(canvas: Canvas, control: Control) {
        canvas.drawPath(path, properties.paint)
    }

    protected open fun getQuadrantOf(
        direction: Control.Direction,
        directionType: Control.DirectionType,
    ): Int = direction quadrant directionType
}