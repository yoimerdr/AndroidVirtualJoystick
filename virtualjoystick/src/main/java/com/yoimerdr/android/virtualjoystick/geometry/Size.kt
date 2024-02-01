package com.yoimerdr.android.virtualjoystick.geometry


class Size(
    width: Int,
    height: Int
) {

    @set:Throws(IllegalArgumentException::class)
    var width: Int = width
        set(value) {
            if(value < 0)
                throw IllegalArgumentException("The width value cannot be negative.")
            field = value
        }

    @set:Throws(IllegalArgumentException::class)
    var height: Int = height
        set(value) {
            if(value < 0)
                throw IllegalArgumentException("The height value cannot be negative.")
            field = value
        }

    init {
        validateNonNegativeValues()
    }

    constructor() : this(0, 0)
    constructor(size: Size) : this(size.width, size.height)

    @Throws(IllegalArgumentException::class)
    private fun validateNonNegativeValues() {
        if(width < 0 || height < 0)
            throw IllegalArgumentException("The width or height value cannot be negative.")
    }

    @Throws(IllegalArgumentException::class)
    fun set(size: Size) {
        set(size.width, size.height)
    }

    @Throws(IllegalArgumentException::class)
    fun set(width: Int, height: Int) {
        this.width = width
        this.height = height
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