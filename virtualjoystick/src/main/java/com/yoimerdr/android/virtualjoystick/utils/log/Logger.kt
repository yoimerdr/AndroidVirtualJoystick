package com.yoimerdr.android.virtualjoystick.utils.log

import android.util.Log
import kotlin.reflect.KClass

object Logger {
    @JvmStatic
    fun log(tag: String, message: Any) = Log.d(tag, message.toString())

    @JvmStatic
    fun log(message: Any) = Logger.log("LOG", message.toString())

    @JvmStatic
    fun <T : Any> warn(from: KClass<T>, message: Any) = Log.w(from.simpleName, message.toString())

    @JvmStatic
    fun <T : Any> warn(from: T, message: Any) = Logger.warn(from::class, message)

    @JvmStatic
    fun <T : Any> error(from: KClass<T>, message: Any) = Log.e(from.simpleName, message.toString())

    @JvmStatic
    fun <T : Any> error(from: T, message: Any) = Logger.error(from::class, message.toString())
}