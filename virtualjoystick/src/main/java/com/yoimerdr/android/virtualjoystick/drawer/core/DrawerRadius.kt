package com.yoimerdr.android.virtualjoystick.drawer.core

import androidx.annotation.FloatRange
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.extensions.greaterThan

/**
 * A sealed class for obtaining a radius value.
 * */
sealed class DrawerRadius {
    abstract fun getValue(control: Control): Double

    /**
     * The zero radius.
     * */
    data object Zero : DrawerRadius() {
        override fun getValue(control: Control): Double = 0.0
    }

    /**
     * The fixed radius.
     *
     * @param radius The fixed radius value. Must be greater than 0.
     * */
    class Fixed(
        @FloatRange(
            from = 0.0,
            fromInclusive = false
        )
        radius: Float,
    ) : DrawerRadius() {
        val radius: Float = radius.greaterThan(0f)

        override fun getValue(control: Control): Double = radius.toDouble()
    }

    /**
     * The radius based on a ratio of the control radius.
     *
     * @param ratio The ratio value in the range [MIN_RADIUS_RATIO] to [MAX_RADIUS_RATIO].
     * */
    class Ratio(
        @FloatRange(
            from = MIN_RADIUS_RATIO.toDouble(),
            to = MAX_RADIUS_RATIO.toDouble(),
        )
        ratio: Float,
    ) : DrawerRadius() {

        companion object {
            /**
             * The minimum valid radius ratio value.
             */
            const val MIN_RADIUS_RATIO = 0.1f

            /**
             * The maximum valid radius ratio value.
             */
            const val MAX_RADIUS_RATIO = 0.80f

            /**
             * Clamps the [ratio] value in the valid range.
             *
             * @param ratio The ratio value.
             *
             * @return A valid radius ratio in the range [MIN_RADIUS_RATIO] to [MAX_RADIUS_RATIO]
             */
            @JvmStatic
            @FloatRange(from = MIN_RADIUS_RATIO.toDouble(), to = MAX_RADIUS_RATIO.toDouble())
            fun clampRatio(ratio: Float): Float {
                return ratio.coerceIn(MIN_RADIUS_RATIO, MAX_RADIUS_RATIO)
            }
        }

        val ratio = clampRatio(ratio)

        override fun getValue(control: Control): Double = control.radius * ratio
    }
}

