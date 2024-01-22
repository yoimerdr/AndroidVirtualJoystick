package com.yoimerdr.android.virtualjoystick.geometry

interface ImmutablePosition {
    val x: Float
    val y: Float
    fun deltaX(x: Float): Float
    fun deltaY(y: Float): Float
    fun deltaX(position: ImmutablePosition): Float
    fun deltaY(position: ImmutablePosition): Float
}