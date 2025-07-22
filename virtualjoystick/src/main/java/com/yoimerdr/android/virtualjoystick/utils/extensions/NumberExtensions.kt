package com.yoimerdr.android.virtualjoystick.utils.extensions

import com.yoimerdr.android.virtualjoystick.exceptions.LargeNumberException
import com.yoimerdr.android.virtualjoystick.exceptions.LowerNumberException
import kotlin.jvm.Throws

@Throws(LowerNumberException::class)
fun <N : Number, T : Comparable<N>> T.greaterThan(minimum: N): T {
    if (this <= minimum)
        throw LowerNumberException.withEquals(minimum)
    return this

}

@Throws(LargeNumberException::class)
fun <N : Number, T : Comparable<N>> T.lowerThan(maximum: N): T {
    if (this >= maximum)
        throw LargeNumberException.withEquals(maximum)
    return this
}

@Throws(LowerNumberException::class)
fun Int.requirePositive(): Int = greaterThan(-1)

@Throws(LargeNumberException::class)
fun Int.requireNegative(): Int = lowerThan(0)

@Throws(LowerNumberException::class)
fun Long.requirePositive(): Long = greaterThan(-1)

@Throws(LargeNumberException::class)
fun Long.requireNegative(): Long = lowerThan(0)

@Throws(LowerNumberException::class)
fun Float.requirePositive(): Float = greaterThan(-1.0f)

@Throws(LargeNumberException::class)
fun Float.requireNegative(): Float = lowerThan(0.0f)

@Throws(LowerNumberException::class)
fun Double.requirePositive(): Double = greaterThan(-1.0)

@Throws(LargeNumberException::class)
fun Double.requireNegative(): Double = lowerThan(0.0)
