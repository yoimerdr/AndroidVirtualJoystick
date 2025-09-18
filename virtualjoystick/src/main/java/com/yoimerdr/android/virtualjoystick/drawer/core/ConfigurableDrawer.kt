package com.yoimerdr.android.virtualjoystick.drawer.core

import com.yoimerdr.android.virtualjoystick.control.Control


interface ConfigurableDrawer : ControlDrawer {
    /**
     * The drawer properties
     */
    val properties: DrawerProperties

    fun canDraw(control: Control): Boolean

    fun release()
}