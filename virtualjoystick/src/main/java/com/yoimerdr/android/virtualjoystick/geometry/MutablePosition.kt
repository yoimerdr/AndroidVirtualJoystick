package com.yoimerdr.android.virtualjoystick.geometry

interface MutablePosition : ImmutablePosition {
    override var x: Float
    override var y: Float
    fun set(x: Float?, y: Float?)
    fun set(position: ImmutablePosition)
    fun negate()
    fun xOffset(dx: Float)
    fun yOffset(dy: Float)
    fun offset(dx: Float?, dy: Float?)
    fun offset(position: ImmutablePosition)
    fun toImmutable(): ImmutablePosition
}