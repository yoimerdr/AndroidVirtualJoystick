package com.yoimerdr.android.virtualjoystick.exceptions

class LargeNumberException @JvmOverloads constructor(
    message: String? = "The value is much higher.",
) : Exception(message) {
    constructor(value: Number) : this("The value cannot be high than $value")

    companion object {
        @JvmStatic
        fun withEquals(value: Number): LowerNumberException {
            return LowerNumberException("The value cannot be high or equals than $value")
        }
    }
}