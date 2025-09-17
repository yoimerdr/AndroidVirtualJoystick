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
         * Checks if the [alpha] value meets the valid range.
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
         * Changes the alpha channel value of the given color to the one returned by [clampAlpha].
         * @param color The color to change the alpha channel value.
         * @return A new [ColorInt] with the new alpha channel value.
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
     * Gets the distance value between the outer position and the control center.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getOuterDistance(control: Control): Double = control.radius

    /**
     * Gets the distance value between the inner position and the control center.
     * @param control The [Control] from where the drawer is used.
     */
    protected abstract fun getInnerDistance(control: Control): Double

    abstract fun updatePath(
        control: Control,
        direction: Control.Direction,
        directionType: Control.DirectionType,
    )

    open fun drawPath(canvas: Canvas, control: Control) {
        canvas.drawPath(path, properties.paint)
    }

    protected open fun getQuadrantOf(
        direction: Control.Direction,
        directionType: Control.DirectionType,
    ): Int = direction quadrant directionType
}