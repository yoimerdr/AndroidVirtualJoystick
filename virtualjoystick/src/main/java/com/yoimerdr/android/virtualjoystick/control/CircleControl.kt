package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.control.drawer.CircleControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.ControlDrawer
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme
import com.yoimerdr.android.virtualjoystick.views.JoystickView

/**
 * [Control] that defines the methods to use a [drawer] that draws something similar to a circle.
 *
 * By default, the [drawer] is [CircleControlDrawer].
 */
open class CircleControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    directionType: JoystickView.DirectionType,
    radiusProportion: Float
) : Control(invalidRadius, directionType) {

    override var drawer: ControlDrawer = CircleControlDrawer(colors, radiusProportion)

}