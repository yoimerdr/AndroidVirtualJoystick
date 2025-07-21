package com.yoimerdr.android.virtualjoystick.control.drawer

import android.graphics.Canvas
import com.yoimerdr.android.virtualjoystick.control.Control

/**
 * A [ControlDrawer] that uses other drawers to draw.
 */
open class CompositeControlDrawer @JvmOverloads constructor(
    /**
     *  The control drawers to use.
     */
    protected open val drawers: List<ControlDrawer>,
    /**
     * The interface to call before each draw.
     */
    protected open val beforeDraw: BeforeDraw? = null
) : ControlDrawer {

    /**
     * Interface for call before each draw.
     */
    fun interface BeforeDraw {
        /**
         * Called before each [ControlDrawer] draw.
         *
         * @param drawer The current [ControlDrawer]
         * @param control The [Control] from where the drawer is used.
         */
        fun before(drawer: ControlDrawer, control: Control)
    }

    override fun draw(canvas: Canvas, control: Control) {
        drawers.forEach { drawer ->
            beforeDraw?.before(drawer, control)
            drawer.draw(canvas, control)
        }
    }
}