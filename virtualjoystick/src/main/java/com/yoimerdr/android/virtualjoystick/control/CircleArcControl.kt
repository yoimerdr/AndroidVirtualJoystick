package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.control.drawer.CircleArcControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.ControlDrawer
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme
import com.yoimerdr.android.virtualjoystick.views.JoystickView

/**
 * [Control] that defines the methods to use a [drawer] that draws something similar to an circle with an arc.
 *
 * By default, the [drawer] is [CircleArcControlDrawer].
 */
open class CircleArcControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    directionType: JoystickView.DirectionType,
    strokeWidth: Float,
    sweepAngle: Float,
    radiusProportion: Float
) : Control(invalidRadius, directionType) {

    override var drawer: ControlDrawer = CircleArcControlDrawer(colors, strokeWidth, sweepAngle, radiusProportion)
}