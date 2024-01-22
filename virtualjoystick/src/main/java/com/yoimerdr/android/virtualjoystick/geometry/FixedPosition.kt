package com.yoimerdr.android.virtualjoystick.geometry

open class FixedPosition(
    override val x: Float,
    override val y: Float
) : ImmutablePosition {

    constructor() : this(0.0f, 0.0f)

    constructor(x: Int, y: Int) : this(x.toFloat(), y.toFloat())

    constructor(x: Double, y: Double) : this(x.toFloat(), y.toFloat())

    constructor(position: ImmutablePosition) : this(position.x, position.y)

    override fun deltaX(x: Float): Float = this.x - x

    override fun deltaX(position: ImmutablePosition): Float = deltaX(position.x)

    override fun deltaY(y: Float): Float = this.y - y

    override fun deltaY(position: ImmutablePosition): Float = deltaY(position.y)

    override fun equals(other: Any?): Boolean {
        if(other is ImmutablePosition) {
            return other.x == x && other.y == y
        }
        return super.equals(other)
    }

    override fun toString(): String = "FixedPosition(x=%.2f,y=%.2f)".format(x, y)

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }
}