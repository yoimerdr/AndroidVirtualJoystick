package com.yoimerdr.android.virtualjoystick.theme

import androidx.annotation.ColorInt

class ColorsScheme(
    @ColorInt
    var primary: Int,
    @ColorInt
    var accent: Int
) {
    constructor(@ColorInt color: Int) : this(color, color)
}
