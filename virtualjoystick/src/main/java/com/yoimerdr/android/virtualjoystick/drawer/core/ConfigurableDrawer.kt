package com.yoimerdr.android.virtualjoystick.drawer.core

import com.yoimerdr.android.virtualjoystick.control.Control

/**
 * Extension of [ControlDrawer] that provides a more efficient way to draw controls
 * */
interface ConfigurableDrawer : ControlDrawer {
    /**
     * The drawer properties.
     */
    val properties: DrawerProperties

    /**
     * Determines if the drawer can draw.
     *
     * The aim is to avoid redrawing the same drawer if no relevant properties have changed that
     * would cause the drawing to change, for example, color or position.
     *
     * @param control The control associated with the drawer.
     *
     * @return True if the drawer can draw, false otherwise.
     * */
    fun canDraw(control: Control): Boolean

    /**
     * Release cache properties.
     *
     * The aim is to call it to force [canDraw] to always return true.
     * */
    fun release()
}