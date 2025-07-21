package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.control.drawer.ControlDrawer

open class SimpleControl(
    override var drawer: ControlDrawer,
    invalidRadius: Float,
    directionType: DirectionType
) : Control(invalidRadius, directionType)