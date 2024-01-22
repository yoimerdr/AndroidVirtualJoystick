package com.yoimerdr.android.virtualjoystick.geometry

class Size(
    var width: Int,
    var height: Int
) {

    constructor() : this(0, 0)
    constructor(size: Size) : this(size.width, size.height)

    fun set(size: Size) {
        width = size.width
        height = size.height
    }

    override fun equals(other: Any?): Boolean {
        if(other is Size) {
            return other.width == width && other.height == height
        }

        return super.equals(other)
    }

    fun isEmpty(): Boolean = width == 0 && height == 0
    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        return result
    }
}