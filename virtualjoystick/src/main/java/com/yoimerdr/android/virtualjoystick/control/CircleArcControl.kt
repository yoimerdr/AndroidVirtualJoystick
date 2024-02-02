package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.control.drawer.CircleArcControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.ControlDrawer
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme
import com.yoimerdr.android.virtualjoystick.views.JoystickView

/**
 * [Control] that uses by default by the [CircleArcControlDrawer].
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