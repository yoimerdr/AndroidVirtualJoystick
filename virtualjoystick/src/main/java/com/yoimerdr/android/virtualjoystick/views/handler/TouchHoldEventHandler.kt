package com.yoimerdr.android.virtualjoystick.views.handler

import android.view.MotionEvent
import android.view.View
import com.yoimerdr.android.virtualjoystick.utils.extensions.requirePositive
import kotlinx.coroutines.Runnable

/**
 * Handles a simple hold event, and other basics, in onTouchEvent.
 */
abstract class TouchHoldEventHandler<ViewLike : View>(
    /**
     * The view where the handler must be used.
     */
    var view: ViewLike,
    /**
     * The time interval (ms) to consider the [MotionEvent.ACTION_DOWN] as a hold event if a different event has not occurred.
     */
    holdInterval: Long,
    /**
     * The time interval (ms) to consider the [MotionEvent.ACTION_MOVE] as a hold event if a different event has not occurred.
     */
    activeHoldInterval: Long,
) {
    /**
     * @param view The view where the handler must be used.
     * @param holdInterval The time interval (ms) to consider the [MotionEvent.ACTION_DOWN] as a hold event.
     * [TouchHoldEventHandler.activeHoldInterval] take the same value.
     *
     * @see [TouchHoldEventHandler.holdInterval]
     * @see [TouchHoldEventHandler.activeHoldInterval]
     */
    constructor(view: ViewLike, holdInterval: Long) : this(view, holdInterval, holdInterval)

    var activeHoldInterval: Long = activeHoldInterval.requirePositive()
        set(value) {
            field = value.requirePositive()
        }

    var holdInterval: Long = holdInterval.requirePositive()
        set(value) {
            field = value.requirePositive()
        }

    private var isHold = false

    private var holdRunnable = Runnable {
        if (isHold) {
            touchHold()
            postHoldRunnable()
        }
    }

    private var activeHoldRunnable = Runnable {
        isHold = true
        holdRunnable.run()
    }

    private fun postHoldRunnable() {
        view.postDelayed(holdRunnable, holdInterval)
    }

    private fun removeHoldRunnable() {
        view.removeCallbacks(holdRunnable)
    }

    private fun postActiveHoldRunnable() {
        view.postDelayed(activeHoldRunnable, activeHoldInterval)
    }

    private fun removeActiveHoldMoveRunnable() {
        view.removeCallbacks(activeHoldRunnable)
    }

    private fun removeRunnable() {
        removeHoldRunnable()
        removeActiveHoldMoveRunnable()
    }

    /**
     * The touch hold event.
     *
     * It is called after the next cases:
     * - The action event has been held in [MotionEvent.ACTION_DOWN] for at least [holdInterval] ms.
     * - The action event has been held in [MotionEvent.ACTION_MOVE] for at least [activeHoldInterval] ms.
     */
    protected abstract fun touchHold()

    /**
     * The touch down event.
     *
     * Its called when the action event is [MotionEvent.ACTION_DOWN]
     */
    protected abstract fun touchDown(): Boolean

    /**
     * The touch up event.
     *
     * Its called when the action event is [MotionEvent.ACTION_UP]
     */
    protected abstract fun touchUp(): Boolean

    /**
     * The touch move event.
     *
     * Its called when the action event is [MotionEvent.ACTION_MOVE]
     */
    protected abstract fun touchMove(): Boolean


    /**
     * The not handled touch events.
     *
     * Its called if the event action don't match any of the defined events.
     *
     * @param event The event to handle.
     *
     * @see [touchDown]
     * @see [touchUp]
     * @see [touchMove]
     */
    protected abstract fun notHandledTouch(event: MotionEvent): Boolean

    /**
     * The touch event handler.
     *
     * Called in the [View.onTouchEvent] override method of the view in which this handler has been initialized.
     *
     */
    open fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null)
            return false

        return when (event.action) {
            MotionEvent.ACTION_UP -> {
                isHold = false
                removeRunnable()
                touchUp()
            }

            MotionEvent.ACTION_DOWN -> {
                removeRunnable()
                isHold = true
                touchDown().apply {
                    postHoldRunnable()
                }
            }

            MotionEvent.ACTION_MOVE -> {
                removeRunnable()
                touchMove().apply {
                    postActiveHoldRunnable()
                }
            }

            else -> notHandledTouch(event)
        }
    }

}