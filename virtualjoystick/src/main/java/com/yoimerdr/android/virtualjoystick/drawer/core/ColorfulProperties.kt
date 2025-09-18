package com.yoimerdr.android.virtualjoystick.drawer.core

import android.graphics.Paint
import androidx.annotation.ColorInt
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * Provides color properties for a drawer.
 * */
open class ColorfulProperties @JvmOverloads constructor(
    /**
     * The color scheme.
     * */
    val colors: ColorsScheme,
    /**
     * The paint.
     * */
    val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG),
) : SimpleDrawer.SimpleProperties() {

    init {
        paint.color = colors.primary
    }

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
            hasChanged = primaryColor != color
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
            hasChanged = accentColor != color
            colors.accent = color
        }

    /**
     * Sets the primary and accent colors of drawer.
     *
     * @param primary The new primary color.
     * @param accent The new accent color.
     */
    open fun setColors(@ColorInt primary: Int, @ColorInt accent: Int) {
        val hasChanged = primaryColor != primary || accent != accentColor
        primaryColor = primary
        accentColor = accent

        this.hasChanged = hasChanged
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