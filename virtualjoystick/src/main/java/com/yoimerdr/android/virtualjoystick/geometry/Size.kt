package com.yoimerdr.android.virtualjoystick.geometry


/**
 * Represents the dimensions of an object with width and height.
 *
 * It will not accept negative values.
 */
class Size(
    width: Int,
    height: Int
) {
    /**
     * The width dimension.
     */
    var width: Int = width
        /**
         * Changes the height dimension.
         * @param width The new height dimension.
         * @throws IllegalArgumentException if the given height value is negative.
         */
        @Throws(IllegalArgumentException::class)
        set(width) {
            if(width < 0)
                throw IllegalArgumentException("The width value cannot be negative.")
            field = width
        }

    /**
     * The height dimension.
     */
    var height: Int = height
        /**
         * Changes the height dimension.
         * @param height The new height dimension.
         * @throws IllegalArgumentException if the given height value is negative.
         */
        @Throws(IllegalArgumentException::class)
        set(height) {
            if(height < 0)
                throw IllegalArgumentException("The height value cannot be negative.")
            field = height
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

    @Throws(IllegalArgumentException::class)
    private fun validateNonNegativeValues() {
        if(width < 0 || height < 0)
            throw IllegalArgumentException("The width or height value cannot be negative.")
    }

    /**
     * Sets the width and height dimensions base on another size.
     * @param size The size from which the dimensions are to be set.
     *
     * @throws IllegalArgumentException If any of the dimensions of [size] is negative.
     */
    @Throws(IllegalArgumentException::class)
    fun set(size: Size) {
        set(size.width, size.height)
    }

    /**
     * Sets the width and height coordinates of the size.
     *
     * @param width The new width dimension.
     * @param height The new height dimension.
     */
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

    /**
     * Checks if the size is empty.
     *
     * @return true if the [width] and [height] dimensions are zero, otherwise false
     */
    fun isEmpty(): Boolean = width == 0 && height == 0
    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        return result
    }
}