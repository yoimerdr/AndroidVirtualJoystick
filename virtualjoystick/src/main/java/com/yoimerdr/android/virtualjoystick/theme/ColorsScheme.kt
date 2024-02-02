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
}
