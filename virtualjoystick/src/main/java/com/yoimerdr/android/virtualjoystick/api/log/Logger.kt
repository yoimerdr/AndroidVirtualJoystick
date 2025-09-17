package com.yoimerdr.android.virtualjoystick.api.log

import android.util.Log
import kotlin.reflect.KClass

object Logger {
    /**
     * Log a message with a specified tag.
     *
     * This is equals to:
     * ```
     * Log.d(tag, message.toString())
     * ```
     * @param tag The tag to use in the log.
     * @param message The message to log.
     */
    @JvmStatic
    fun log(tag: String, message: Any) = Log.d(tag, message.toString())

    /**
     * Log a message with a default tag.
     *
     * This is equals to:
     * ```
     * Log.d("LOG", message.toString())
     * ```
     * @param message The message to log.
     */
    @JvmStatic
    fun log(message: Any) = Logger.log("LOG", message.toString())

    /**
     * Log a warning with the simple name of the provided class as the tag.
     *
     * This is equals to:
     * ```
     * Log.w(from.simpleName, message.toString())
     * ```
     * @param from The class from which the warning originates.
     * @param message The warning message.
     */
    @JvmStatic
    fun <T : Any> warn(from: KClass<T>, message: Any) = Log.w(from.simpleName, message.toString())

    /**
     * Log a warning with the simple name of the class of the provided object as the tag.
     *
     * This is equals to:
     * ```
     * Logger.warn(from::class, message.toString())
     * ```
     * @param from The object from which the warning originates.
     * @param message The warning message.
     */
    @JvmStatic
    fun <T : Any> warn(from: T, message: Any) = warn(from::class, message)

    /**
     * Log an error with the simple name of the provided class as the tag.
     *
     * This is equals to:
     * ```
     * Log.e(from.simpleName, message.toString())
     * ```
     * @param from The class from which the error originates.
     * @param message The error message.
     */
    @JvmStatic
    fun <T : Any> error(from: KClass<T>, message: Any) = Log.e(from.simpleName, message.toString())

    /**
     * Log an error with the simple name of the class of the provided object as the tag.
     *
     * This is equals to:
     * ```
     * Logger.error(from::class, message.toString())
     * ```
     * @param from The object from which the error originates.
     * @param message The error message.
     */
    @JvmStatic
    fun <T : Any> errorFromClass(from: T, message: Any) = error(from::class, message.toString())
}