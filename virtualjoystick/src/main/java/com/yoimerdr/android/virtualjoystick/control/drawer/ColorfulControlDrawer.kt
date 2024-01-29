package com.yoimerdr.android.virtualjoystick.control.drawer

import android.graphics.Paint
import androidx.annotation.ColorInt
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * A drawer that use an [Paint] and [colors] for draw the control representation.
 */
abstract class ColorfulControlDrawer(
    private val properties: ColorfulProperties
) : ControlDrawer {

    open class ColorfulProperties(val colors: ColorsScheme) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    /**
     * The drawer colors.
     */
    protected val colors: ColorsScheme get() = properties.colors

    /**
     * The drawer paint.
     */
    protected val paint: Paint get() = properties.paint

    /**
     * An [IntArray] of the current [colors] properties.
     *
     * @return A new intArrayOf([primaryColor], [accentColor])
     */
    protected val colorsArray: IntArray
        @ColorInt
        get() = intArrayOf(primaryColor, accentColor)

    /**
     * Short getter and setter for [colors].primary
     */
    open var primaryColor: Int
        @ColorInt
        get() = colors.primary
        set(@ColorInt color) {
            colors.primary = color
            paint.color = color
        }

    /**
     * Short getter and setter for colors.accent
     */
    open var accentColor: Int
        @ColorInt
        get() = colors.accent
        set(@ColorInt color) {
            colors.accent = color
        }

    open fun setColors(@ColorInt primary: Int, @ColorInt accent: Int) {
        primaryColor = primary
        accentColor = accent
    }

    open fun setColors(colors: ColorsScheme) {
        colors.apply {
            setColors(primary, accent)
        }
    }
}