package com.yoimerdr.android.virtualjoystick.geometry.size

import com.yoimerdr.android.virtualjoystick.exceptions.LowerNumberException
import com.yoimerdr.android.virtualjoystick.utils.extensions.requirePositive


/**
 * Represents the dimensions of an object with width and height.
 *
 * It will not accept negative values.
 */
class Size(
    width: Int,
    height: Int,
) : MutableSize {
    override var width: Int = width
        @Throws(LowerNumberException::class)
        set(width) {
            field = width.requirePositive()
        }

    override var height: Int = height
        @Throws(LowerNumberException::class)
        set(height) {
            field = height.requirePositive()
        }

    init {
        validateNonNegativeValues()
    }

    /**
     * Initializes an empty size.
     *
     * @see [Size.isEmpty]
     */
    constructor() : this(0, 0)
    constructor(size: Size) : this(size.width, size.height)

    @Throws(LowerNumberException::class)
    private fun validateNonNegativeValues() {
        height.requirePositive()
        width.requirePositive()
    }

    override fun set(size: ImmutableSize) {
        set(size.width, size.height)
    }

    override fun set(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    override fun setWidth(size: ImmutableSize) {
        this.width = size.width
    }

    override fun setHeight(size: ImmutableSize) {
        this.height = size.height
    }

    override fun equals(other: Any?): Boolean {
        if (other is ImmutableSize)
            return other.width == width && other.height == height

        return super.equals(other)
    }

    override fun isEmpty(): Boolean = width == 0 && height == 0

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        return result
    }
}