package com.yoimerdr.android.virtualjoystick.drawer.core

import android.graphics.Canvas
import androidx.annotation.CallSuper
import com.yoimerdr.android.virtualjoystick.control.Control

/**
 * Base class for control drawers with simple property handling and invalidation logic.
 *
 * This class provides a basic behavior for drawing controls, managing properties, and
 * handling invalidation when the control's state changes.
 *
 * @property properties The properties associated with this control drawer.
 */
open class SimpleDrawer(
    override val properties: DrawerProperties,
) : ConfigurableDrawer {

    /**
     * Initializes the drawer with empty properties.
     */
    constructor() : this(EmptyProperties)

    private var mConfigured = false

    /**
     * Simple class for properties.
     *
     * This class tracks whether the properties have changed and provides a simple mechanism
     * to reset the changed state.
     */
    open class SimpleProperties : DrawerProperties {
        /**
         * Flag indicating whether the properties have changed since the last resolution.
         */
        protected var hasChanged = false

        override fun changed(): Boolean = hasChanged

        override fun resolve() {
            hasChanged = false
        }
    }

    /**
     * Represents empty properties.
     */
    data object EmptyProperties : SimpleProperties()

    /**
     * The last recorded direction of the control.
     */
    protected var lastDirection: Control.Direction? = null
        private set

    /**
     * The last recorded direction type of the control.
     */
    protected var lastType: Control.DirectionType? = null
        private set

    /**
     * Called once to configure the drawer.
     *
     * Subclasses can override this method to perform initial setup.
     */
    protected open fun configure() {}

    /**
     * Invalidates the drawer, resetting the recorded values.
     */
    @CallSuper
    protected open fun invalidate() {
        lastDirection = null
        lastType = null
    }

    /**
     * Checks if the current control state is valid based on the recorded values.
     *
     * By default, it checks if the direction type and direction are the same as the
     * last recorded values.
     *
     * @param control The current control.
     * @return `true` if the state is valid, `false` otherwise.
     */
    protected open fun isValid(control: Control): Boolean {
        return control.directionType == lastType && control.direction == lastDirection
    }

    /**
     * Calculates the current direction of the control.
     *
     * By default, it returns the control's direction.
     *
     * @param control The current control.
     */
    protected open fun getDirection(control: Control): Control.Direction = control.direction

    /**
     * Calculate the maximum distance from the center that can be reached.
     *
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getMaxDistance(control: Control): Double = control.radius

    override fun canDraw(control: Control): Boolean = true

    @CallSuper
    override fun draw(canvas: Canvas, control: Control) {
        if (!mConfigured) {
            configure()
            mConfigured = true
        }

        val direction = getDirection(control)

        if (!isValid(control)) {
            lastDirection = direction
            lastType = control.directionType
            onPrepare(canvas, control)
        }

        if (properties.changed())
            onChange()

        onDraw(canvas, control)
        properties.resolve()
    }

    /**
     * Called when the control needs to be prepared due to it is invalid.
     *
     * @param canvas The view canvas.
     * @param control The [Control] from where the drawer is used.
     * @see [isValid]
     */
    protected open fun onPrepare(canvas: Canvas, control: Control) {

    }

    /**
     * Called when the properties have changed.
     *
     * @see [DrawerProperties.changed]
     */
    @CallSuper
    protected open fun onChange() {
        properties.resolve()
    }

    /**
     * Called when the drawer is ready to draw.
     *
     * @param canvas The view canvas.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun onDraw(canvas: Canvas, control: Control) {

    }

    override fun release() {
        lastDirection = null
        lastType = null
    }
}