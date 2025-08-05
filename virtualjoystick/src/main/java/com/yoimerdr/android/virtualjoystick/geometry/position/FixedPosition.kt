package com.yoimerdr.android.virtualjoystick.geometry.position

class FixedPosition(
    override val x: Float,
    override val y: Float,
) : PartialPosition() {

    constructor() : this(0.0f, 0.0f)

    constructor(x: Number, y: Number) : this(x.toFloat(), y.toFloat())

    constructor(position: ImmutablePosition) : this(position.x, position.y)

    override fun toString(): String = "FixedPosition(x=%.2f,y=%.2f)".format(x, y)

}