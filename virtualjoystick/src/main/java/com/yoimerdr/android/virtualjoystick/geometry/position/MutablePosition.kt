package com.yoimerdr.android.virtualjoystick.geometry.position

/**
 * Represents a mutable position.
 */
interface MutablePosition : ImmutablePosition {
    override var x: Float
    override var y: Float

    /**
     * Sets the x and y coordinates of the mutable position.
     *
     * @param x The new x-coordinate.
     * @param y The new y-coordinate.
     */
    fun set(x: Float, y: Float)

    /**
     * Sets the x and y coordinates of the mutable position based on another position.
     *
     * @param position The position from which the coordinates are to be set.
     */
    fun set(position: ImmutablePosition)

    /**
     * Negates both the x and y coordinates of the mutable position.
     */
    fun negate()

    /**
     * Offsets the x-coordinate of the mutable position by the specified amount.
     *
     * @param dx The amount by which to offset the x-coordinate.
     */
    fun xOffset(dx: Float)

    /**
     * Offsets the y-coordinate of the mutable position by the specified amount.
     *
     * @param dy The amount by which to offset the y-coordinate.
     */
    fun yOffset(dy: Float)

    /**
     * Offsets both the x and y coordinates of the mutable position by the specified amounts.
     *
     * @param dx The amount by which to offset the x-coordinate.
     * @param dy The amount by which to offset the y-coordinate.
     */
    fun offset(dx: Float, dy: Float)

    /**
     * Offsets both the x and y coordinates of the mutable position based on another position.
     *
     * @param position The position from which to offset the coordinates.
     */
    fun offset(position: ImmutablePosition)

    /**
     * Initializes a [ImmutablePosition] with the current [x] and [y] coordinates.
     *
     * @return A new [ImmutablePosition] instance.
     */
    fun toImmutable(): ImmutablePosition
}