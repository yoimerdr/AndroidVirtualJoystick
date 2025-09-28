package com.yoimerdr.android.virtualjoystick.geometry.position

abstract class PartialPosition : ImmutablePosition {
    override fun deltaX(x: Float): Float = this.x - x

    override fun deltaX(position: ImmutablePosition): Float = deltaX(position.x)

    override fun deltaY(y: Float): Float = this.y - y

    override fun deltaY(position: ImmutablePosition): Float = deltaY(position.y)

    override fun equals(other: Any?): Boolean {
        if (other is ImmutablePosition)
            return other.x == x && other.y == y
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }
}