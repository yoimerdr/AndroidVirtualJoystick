package com.yoimerdr.android.virtualjoystick.api.log

import android.util.Log
import kotlin.reflect.KClass

object LoggerSupplier {
    fun withLogger(block: Logger.() -> Unit) {
        DefaultLogger.block()
    }

    fun withLogger(clazz: KClass<*>, block: Logger.() -> Unit) {
        withLogger(clazz.simpleName ?: "LOGGER", block)
    }

    fun withLogger(clazz: Class<*>, block: Logger.() -> Unit) {
        withLogger(clazz.simpleName ?: "LOGGER", block)
    }

    fun withLogger(label: String, block: Logger.() -> Unit) {
        LabeledLogger(label)
            .block()
    }

    private fun createMessage(message: Any, others: Array<out Any>): String {
        return "$message\n${others.joinToString("\n") { it.toString() }}"
    }

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