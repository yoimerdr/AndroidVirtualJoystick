package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.control.drawer.CircleControlDrawer
import com.yoimerdr.android.virtualjoystick.geometry.Position
import com.yoimerdr.android.virtualjoystick.geometry.Size
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * [Control] that draws a circle as a Joystick Control.
 */
open class CircleControl(
    colors: ColorsScheme,
    position: Position,
    invalidRadius: Int
) : Control(position, invalidRadius) {

    init {
        drawer = CircleControlDrawer(position, colors, inCircle)
    }

    /**
     * Set radius restrictions based on the view size.
     *
     * The inner circle occupies 25% of half of the maximum view width, while the outer circle occupies 75%.
     * @param size The size of the view.
     */
    override fun setRadiusRestriction(size: Size) {
        size.apply {
            (width.coerceAtMost(height) / 2f)
                .also {
                    outCircle.radius = it * 0.75f
                    inCircle.radius = it * 0.25f
                }
        }
    }

    /**
     * Check if the distance at the current position and the center is greater than the maximum view radius.
     * If so, change the position to the extreme maximum at that position.
     */
    override fun validatePositionLimits() {
        val distance = distanceFromCenter()
        if (distance > outCircle.radius) {
            val proportion = outCircle.radius / distance
            position.set(deltaX() * proportion + center.x, deltaY() * proportion + center.y)
        }
    }

}