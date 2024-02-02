package com.yoimerdr.android.virtualjoystick.exceptions

/**
 * Represents an invalid control position exception.
 * It is usually thrown when one of the x or y coordinates is negative.
 */
class InvalidControlPositionException(
    message: String?
) : Exception(message) {

    constructor() : this("None of the coordinates of the control position can be negative.")
}