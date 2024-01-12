package com.yoimerdr.android.virtualjoystick.geometry

class Size(
    var width: Int,
    var height: Int
) {
    fun set(size: Size) {
        width = size.width
        height = size.height
    }
}