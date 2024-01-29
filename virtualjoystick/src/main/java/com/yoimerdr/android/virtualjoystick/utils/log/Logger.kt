package com.yoimerdr.android.virtualjoystick.utils.log

import android.util.Log
import kotlin.reflect.KClass

object Logger {
    @JvmStatic
    fun <T : Any> warn(from: KClass<T>, message: String) = Log.w(from.simpleName, message)

    @JvmStatic
    fun <T : Any> warn(from: T, message: String) = Logger.warn(from::class, message)

    @JvmStatic
    fun <T : Any> error(from: KClass<T>, message: String) = Log.e(from.simpleName, message)

    @JvmStatic
    fun <T : Any> error(from: T, message: String) = Logger.error(from::class, message)
}