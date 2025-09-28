package com.yoimerdr.android.virtualjoystick.api.log

/**
 * A simple logging interface
 * */
interface Logger {
    /**
     * Log a message with info level.
     * */
    fun log(message: Any, vararg others: Any)

    /**
     * Log a message with warn level.
     * */
    fun warn(message: Any, vararg others: Any)
    /**
     * Log a message with error level.
     * */
    fun error(message: Any, vararg others: Any)
}