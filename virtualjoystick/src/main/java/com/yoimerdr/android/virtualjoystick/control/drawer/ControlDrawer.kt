package com.yoimerdr.android.virtualjoystick.control.drawer

import android.graphics.Canvas
import com.yoimerdr.android.virtualjoystick.control.Control

fun interface ControlDrawer {
    fun draw(canvas: Canvas, control: Control)
}