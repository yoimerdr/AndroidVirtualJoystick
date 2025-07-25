package com.yoimerdr.android.virtualjoystick.geometry.position

class Position(
    override var x: Float,
    override var y: Float,
) : PartialPosition(), MutablePosition {

    constructor() : this(0.0f, 0.0f)

    constructor(x: Int, y: Int) : this(x.toFloat(), y.toFloat())

    constructor(x: Double, y: Double) : this(x.toFloat(), y.toFloat())

    constructor(position: ImmutablePosition) : this(position.x, position.y)

    override fun set(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    override fun set(position: ImmutablePosition) = set(position.x, position.y)
    override fun negate() {
        x = -x
        y = -y
    }

    override fun xOffset(dx: Float) {
        this.x += dx
    }

    override fun yOffset(dy: Float) {
        this.y += dy
    }

    override fun offset(dx: Float, dy: Float) {
        xOffset(dx)
        yOffset(dy)
    }

    override fun offset(position: ImmutablePosition) = offset(position.x, position.y)

    override fun toImmutable(): ImmutablePosition = FixedPosition(this)

    override fun toString(): String = "Position(x=%.2f,y=%.2f)".format(x, y)
}