package com.yoimerdr.android.virtualjoystick.theme

import androidx.annotation.ColorInt

/**
 * Represents a basic color schema.
 */
class ColorsScheme(
    /**
     * The primary color of the schema.
     */
    @ColorInt
    var primary: Int,

    /**
     * The accent color of the schema.
     */
    @ColorInt
    var accent: Int
) {
    constructor(@ColorInt color: Int) : this(color, color)
    constructor(colors: ColorsScheme) : this(colors.primary, colors.accent)

    fun set(@ColorInt primary: Int, @ColorInt accent: Int) {
        this.primary = primary
        this.accent = accent
    }

    fun set(colors: ColorsScheme) {
        this.set(colors.primary, colors.accent)
    }

    fun setPrimary(colors: ColorsScheme) {
        this.primary = colors.primary
    }

    fun setAccent(colors: ColorsScheme) {
        this.accent = colors.accent
    }
}
