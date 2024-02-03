package com.yoimerdr.android.virtualjoystick.views.handler

import android.view.MotionEvent
import android.view.View

open class SimpleTouchEventHandler(
    view: View,
    holdInterval: Long,
    activeHoldInterval: Long
) : TouchHoldEventHandler<View>(view, holdInterval, activeHoldInterval) {

    constructor(view: View, holdInterval: Long) : this(view, holdInterval, holdInterval)

    override fun touchHold() {
    }

    override fun touchDown(): Boolean {
        return false
    }

    override fun touchUp(): Boolean {
        return false
    }

    override fun touchMove(): Boolean {
        return false
    }

    override fun notHandledTouch(event: MotionEvent): Boolean {
        return false
    }
}