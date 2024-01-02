package com.yoimerdr.android.virtualjoystick.geometry

class Position(
    var x: Float = 0f,
    var y: Float = 0f
) {

    companion object {
        private inline fun <Ty> Ty?.ifNotNull(consumer: (Ty) -> Unit) {
            if(this != null)
                consumer(this)
        }
    }

    constructor(x: Int, y: Int) : this(x.toFloat(), y.toFloat())
    constructor(x: Double, y: Double) : this(x.toFloat(), y.toFloat())


    fun set(x: Float?, y: Float?) {
        x.ifNotNull {
            this.x = it
        }
        y.ifNotNull {
            this.y = it
        }
    }

    fun set(position: Position) = set(position.x, position.y)
    fun negate() {
        x = -x
        y = -y
    }
    fun offset(dx: Float?, dy: Float?) {
        dx.ifNotNull {
            x += it
        }

        dy.ifNotNull {
            y += it
        }
    }
    fun offset(position: Position) = offset(position.x, position.y)
    fun deltaX(x: Float) = this.x - x
    fun deltaY(y: Float) = this.y - y
    fun deltaX(position: Position) = deltaX(position.x)
    fun deltaY(position: Position) = deltaY(position.y)

    override fun equals(other: Any?): Boolean {
        if(other is Position) {
            return other.x == x && other.y == y
        }
        return super.equals(other)
    }
    override fun toString(): String = "Position(x=%.2f,y=%.2f)".format(x, y)
    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }
}