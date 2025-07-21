package com.yoimerdr.android.virtualjoystick.geometry.size

interface ImmutableSize {
    /**
     * The width dimension.
     */
    val width: Int

    /**
     * The height dimension.
     */
    val height: Int

    /**
     * Checks if the size is empty.
     *
     * This means that both the [width] and [height] dimensions are zero.
     */
    fun isEmpty(): Boolean

    override fun equals(other: Any?): Boolean
}