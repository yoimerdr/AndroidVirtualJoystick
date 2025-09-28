package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.drawer.core.ControlDrawer

/**
 * A simple implementation of the [Control]
 *
 * @param drawer The [ControlDrawer] used to draw the control.
 * @param invalidRadius The radius within which the control is considered inactive.
 * @param directionType The type of direction the control supports.
 * */
open class SimpleControl(
    /**
     * The control drawer.
     * */
    override var drawer: ControlDrawer,
    invalidRadius: Float,
    directionType: DirectionType,
) : Control(invalidRadius, directionType)