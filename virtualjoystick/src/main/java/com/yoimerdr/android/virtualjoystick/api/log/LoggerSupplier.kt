package com.yoimerdr.android.virtualjoystick.api.log

import android.util.Log
import kotlin.reflect.KClass

object LoggerSupplier {
    /**
     * Calls the specified function [block] with the [DefaultLogger] as its receiver.
     * */
    fun withLogger(block: Logger.() -> Unit) {
        DefaultLogger.block()
    }

    /**
     * Calls the specified function [block] with a [LabeledLogger] as its receiver.
     *
     * The label is derived from the simple name of the provided [clazz].
     * */
    fun withLogger(clazz: KClass<*>, block: Logger.() -> Unit) {
        withLogger(clazz.simpleName ?: "LOGGER", block)
    }

    /**
     * Calls the specified function [block] with a [LabeledLogger] as its receiver.
     *
     * The label is derived from the simple name of the provided [clazz].
     * */
    fun withLogger(clazz: Class<*>, block: Logger.() -> Unit) {
        withLogger(clazz.simpleName ?: "LOGGER", block)
    }

    /**
     * Calls the specified function [block] with a [LabeledLogger] as its receiver.
     * */
    fun withLogger(label: String, block: Logger.() -> Unit) {
        LabeledLogger(label)
            .block()
    }

    private fun createMessage(message: Any, others: Array<out Any>): String {
        return "$message\n${others.joinToString("\n") { it.toString() }}"
    }

    /**
     * The default logger implementation that logs to Logcat with the tag "LOG", "WARN", and "ERROR".
     *
     * @see [Log.d]
     * @see [Log.w]
     * @see [Log.e]
     * */
    object DefaultLogger : Logger {
        override fun log(message: Any, vararg others: Any) {
            Log.d("LOG", createMessage(message, others))
        }

        override fun warn(message: Any, vararg others: Any) {
            Log.w("WARN", createMessage(message, others))
        }

        override fun error(message: Any, vararg others: Any) {
            Log.e("ERROR", createMessage(message, others))
        }
    }

    /**
     * A logger implementation that logs to Logcat with a custom label.
     *
     * @param label The label to use for logging.
     *
     * @see [Log.d]
     * @see [Log.w]
     * @see [Log.e]
     * */
    class LabeledLogger(private val label: String) : Logger {
        override fun log(message: Any, vararg others: Any) {
            Log.d(label, createMessage(message, others))
        }

        override fun warn(message: Any, vararg others: Any) {
            Log.w(label, createMessage(message, others))
        }

        override fun error(message: Any, vararg others: Any) {
            Log.e(label, createMessage(message, others))
        }
    }
}