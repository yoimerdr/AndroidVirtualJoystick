package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.control.drawer.CircleControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.ControlDrawer
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme
import com.yoimerdr.android.virtualjoystick.views.JoystickView

/**
 * [Control] that uses by default by the [CircleControlDrawer].
 */
open class CircleControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    directionType: JoystickView.DirectionType,
    radiusRatio: Float
) : Control(invalidRadius, directionType) {

    override var drawer: ControlDrawer = CircleControlDrawer(colors, radiusRatio)

}