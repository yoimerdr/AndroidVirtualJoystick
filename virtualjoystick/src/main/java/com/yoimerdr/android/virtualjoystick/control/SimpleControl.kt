package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.drawer.core.ControlDrawer

open class SimpleControl(
    override var drawer: ControlDrawer,
    invalidRadius: Float,
    directionType: DirectionType
) : Control(invalidRadius, directionType)