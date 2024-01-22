package com.yoimerdr.android.virtualjoystick.exceptions

class InvalidControlPositionException(
    message: String?
) : Exception(message) {

    constructor() : this("")
}