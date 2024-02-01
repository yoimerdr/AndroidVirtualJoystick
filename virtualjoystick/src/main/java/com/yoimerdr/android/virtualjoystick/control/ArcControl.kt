package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.control.drawer.ArcControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.ControlDrawer
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme
import com.yoimerdr.android.virtualjoystick.views.JoystickView

/**
 * [Control] that defines the methods to use a [drawer] that draws something similar to an arc.
 *
 * By default, the [drawer] is [ArcControlDrawer].
 */
open class ArcControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    directionType: JoystickView.DirectionType,
    strokeWidth: Float,
    sweepAngle: Float,
) : Control(invalidRadius, directionType) {
    override var drawer: ControlDrawer = ArcControlDrawer(colors, strokeWidth, sweepAngle)
}