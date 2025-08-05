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

    open class ColorfulProperties(open val colors: ColorsScheme) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    /**
     * The drawer colors.
     */
    val colors: ColorsScheme get() = properties.colors

    /**
     * The drawer paint.
     */
    val paint: Paint get() = properties.paint

    /**
     * An [IntArray] of the current [colors] properties.
     *
     * @return A new intArrayOf([primaryColor], [accentColor])
     */
    val colorsArray: IntArray
        @ColorInt
        get() = intArrayOf(primaryColor, accentColor)


    open var primaryColor: Int
        /**
         * Gets the primary color of [colors].
         */
        @ColorInt
        get() = colors.primary
        /**
         * Sets the primary color of [colors] and paint.
         * @param color The new primary color.
         */
        set(@ColorInt color) {
            colors.primary = color
            paint.color = color
        }

    open var accentColor: Int
        /**
         * Gets the accent color of [colors].
         */
        @ColorInt
        get() = colors.accent
        /**
         * Sets the accent color of [colors].
         * @param color The new accent color.
         */
        set(@ColorInt color) {
            colors.accent = color
        }

    /**
     * Sets the primary and accent colors of drawer.
     *
     * @param primary The new primary color.
     * @param accent The new accent color.
     */
    open fun setColors(@ColorInt primary: Int, @ColorInt accent: Int) {
        primaryColor = primary
        accentColor = accent
    }

    /**
     * Sets the primary and accent colors of drawer based on a schema.
     *
     * @param colors The schema from which the colors are to be set.
     */
    open fun setColors(colors: ColorsScheme) {
        colors.apply {
            setColors(primary, accent)
        }
    }

    /**
     * Sets the primary color of [colors] and paint.
     *
     * @param colors The schema from which the primary color is to be set.
     */
    open fun setPrimaryColor(colors: ColorsScheme) {
        primaryColor = colors.primary
    }

    /**
     * Sets the accent color of [colors] and paint.
     *
     * @param colors The schema from which the primary color is to be set.
     */
    open fun setAccentColor(colors: ColorsScheme) {
        accentColor = colors.accent
    }
}