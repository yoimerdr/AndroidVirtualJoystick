package com.yoimerdr.android.virtualjoystick.geometry

/**
 * Represents an immutable position.
 */
interface ImmutablePosition {
    /**
     * The x coordinate.
     */
    val x: Float

    /**
     * The y coordinate.
     */
    val y: Float

    /**
     * Calculates the difference between the current x-coordinate
     * and the target x-coordinate.
     *
     * @param x The target x-coordinate.
     * @return The calculated value.
     */
    fun deltaX(x: Float): Float

    /**
     * Calculates the vertical difference between the current y-coordinate
     * and the target y-coordinate.
     *
     * @param y The target y-coordinate.
     * @return The calculated value.
     */
    fun deltaY(y: Float): Float

    /**
     * Calculates the difference between the current [x]
     * and the target position's x-coordinate.
     *
     * @param position The target position.
     * @return The calculated value.
     */
    fun deltaX(position: ImmutablePosition): Float

    /**
     * Calculates the difference between the current [y]
     * and the target position's y-coordinate.
     *
     * @param position The target position.
     * @return The calculated value.
     */
    fun deltaY(position: ImmutablePosition): Float
}