package com.yoimerdr.android.virtualjoystick.geometry.size

import androidx.annotation.IntRange
import com.yoimerdr.android.virtualjoystick.exceptions.LowerNumberException
import com.yoimerdr.android.virtualjoystick.extensions.requirePositive


/**
 * Represents the dimensions of an object with width and height.
 *
 * It will not accept negative values.
 */
class Size(
    @IntRange(from = 0)
    width: Int,
    @IntRange(from = 0)
    height: Int,
) : MutableSize {
    override var width: Int = width.requirePositive()
        @Throws(LowerNumberException::class)
        set(width) {
            field = width.requirePositive()
        }

    override var height: Int = height.requirePositive()
        @Throws(LowerNumberException::class)
        set(height) {
            field = height.requirePositive()
        }

    /**
     * Initializes an empty size.
     *
     * @see [Size.isEmpty]
     */
    constructor() : this(0, 0)
    constructor(size: Size) : this(size.width, size.height)

    override fun set(size: ImmutableSize) {
        set(size.width, size.height)
    }

    override fun set(width: Int, height: Int,) {
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

    override fun toString(): String {
        return "Size(width=$width, height=$height)"
    }
}