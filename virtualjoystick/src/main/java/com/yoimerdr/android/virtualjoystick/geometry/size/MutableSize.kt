package com.yoimerdr.android.virtualjoystick.geometry.size

import com.yoimerdr.android.virtualjoystick.exceptions.LowerNumberException

interface MutableSize : ImmutableSize {
    override var width: Int
    override var height: Int

    /**
     * Sets the width and height dimensions base on another size.
     * @param size The size from which the dimensions are to be set.
     *
     * @throws LowerNumberException If any of the dimensions of [size] is negative.
     */
    @Throws(LowerNumberException::class)
    fun set(size: ImmutableSize)

    /**
     * Sets the width and height coordinates of the size.
     *
     * @param width The new width dimension.
     * @param height The new height dimension.
     */
    @Throws(LowerNumberException::class)
    fun set(width: Int, height: Int)

    fun setWidth(size: ImmutableSize)

    fun setHeight(size: ImmutableSize)
}