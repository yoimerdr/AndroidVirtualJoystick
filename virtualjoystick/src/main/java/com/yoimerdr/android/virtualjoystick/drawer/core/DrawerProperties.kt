package com.yoimerdr.android.virtualjoystick.drawer.core

/**
 * The properties of a configurable control drawer.
 */
interface DrawerProperties {

    /**
     * Checks if any property of the control drawer has changed since the last reset.
     *
     * @return `true` if any property has changed, `false` otherwise.
     */
    fun changed(): Boolean

    /**
     * Resets the changed state of the properties.
     *
     * This method should be called after the changes have been processed or applied,
     * effectively marking the current state as the new baseline (not changed).
     */
    fun resolve()
}