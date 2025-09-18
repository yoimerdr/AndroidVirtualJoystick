package com.yoimerdr.android.virtualjoystick.api.log

interface Logger {
    fun log(message: Any, vararg others: Any)
    fun warn(message: Any, vararg others: Any)
    fun error(message: Any, vararg others: Any)
}