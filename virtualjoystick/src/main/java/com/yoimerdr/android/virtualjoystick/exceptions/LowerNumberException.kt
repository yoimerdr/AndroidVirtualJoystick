package com.yoimerdr.android.virtualjoystick.exceptions

class LowerNumberException @JvmOverloads constructor(
    message: String? = "The value is much lower.",
) : Exception(message) {
    constructor(value: Number) : this("The value cannot be less than $value")

    companion object {
        @JvmStatic
        fun withEquals(value: Number): LowerNumberException {
            return LowerNumberException("The value cannot be less or equals than $value")
        }
    }
}