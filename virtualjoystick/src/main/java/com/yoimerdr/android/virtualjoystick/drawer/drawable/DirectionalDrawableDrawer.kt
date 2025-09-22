package com.yoimerdr.android.virtualjoystick.drawer.drawable

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.yoimerdr.android.virtualjoystick.api.drawable.DrawableBitCache
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.drawer.core.EmptyDrawer
import com.yoimerdr.android.virtualjoystick.geometry.factory.RectFFactory
import com.yoimerdr.android.virtualjoystick.geometry.size.Size

/**
 * A drawer that uses different drawables based on the control's direction.
 * */
open class DirectionalDrawableDrawer(
    /**
     * The drawer properties.
     * */
    override val properties: DirectionalDrawableProperties,
) : EmptyDrawer(
    properties
) {

    /**
     * @param states A map of drawables for each direction.
     * @param mode The sizing mode for the drawables.
     * */
    @JvmOverloads
    constructor(
        states: Map<Control.Direction, Drawable>,
        mode: Mode = Mode.AUTOSIZE,
    ) : this(
        DirectionalDrawableProperties(
            states,
            mode
        )
    )

    /**
     * @param states A list of pairs of direction and drawable.
     * @param mode The sizing mode for the drawables.
     * */
    @JvmOverloads
    constructor(
        states: List<Pair<Control.Direction, Drawable>>,
        mode: Mode = Mode.AUTOSIZE,
    ) : this(
        states.toMap(),
        mode
    )

    /**
     * @param states A map of drawables for each direction.
     * @param mode The sizing mode for the drawables.
     * */
    open class DirectionalDrawableProperties @JvmOverloads constructor(
        states: Map<Control.Direction, Drawable>,
        mode: Mode = Mode.AUTOSIZE,
    ) : SimpleProperties() {

        private val mStates: MutableMap<Control.Direction, Drawable> =
            states.toMutableMap()

        /**
         * Gets the map of drawables for each direction.
         * */
        val states: Map<Control.Direction, Drawable>
            get() = mStates.toMap()

        /**
         * The sizing mode for the drawables.
         * */
        var mode: Mode = mode
            /**
             * Sets the sizing mode for the drawables.
             * */
            set(value) {
                hasChanged = value != field
                field = value
            }

        /**
         * Sets the drawable for a specific direction.
         *
         * @param direction The target direction.
         * @param drawable The drawable to set.
         * */
        fun setState(
            direction: Control.Direction,
            drawable: Drawable,
        ) {
            hasChanged = drawable != mStates[direction]
            mStates[direction] = drawable
        }

        /**
         * Removes the drawable for a specific direction.
         * @param direction The target direction to remove.
         * */
        fun removeState(
            direction: Control.Direction,
        ) {
            hasChanged = mStates.remove(direction) != null
        }

        /**
         * Gets the drawable for a specific direction.
         *
         * @param direction The target direction.
         * @return The drawable for the specified direction, or null if not set.
         * */
        fun getState(
            direction: Control.Direction,
        ): Drawable? = mStates[direction]
    }

    /**
     * The sizing mode for the drawables.
     * */
    enum class Mode {
        /**
         * The drawable size is adjusted to fit the control's radius.
         * */
        AUTOSIZE,
        /**
         * The drawable size is fixed to its intrinsic size.
         * */
        FIXED
    }

    private val mCaches: MutableMap<Control.Direction, DrawableBitCache> = mutableMapOf()

    override fun canDraw(control: Control): Boolean {
        return !isValid(control)
    }

    override fun onConfigured() {
        properties.states.forEach {
            if (mCaches[it.key] == null) {
                mCaches[it.key] = DrawableBitCache(it.value)
            }
        }
    }

    override fun onChange() {
        properties.states.apply {
            forEach {
                var cache = mCaches[it.key]
                if (cache == null) {
                    cache = DrawableBitCache(it.value)
                    mCaches[it.key] = cache
                } else cache.drawable = it.value
            }

            if(size < mCaches.size) {
                val keysToRemove = mCaches.keys - keys
                keysToRemove.forEach { key ->
                    val cache = mCaches.remove(key)
                    cache?.recycle()
                }
            }
        }

        super.onChange()
    }

    /**
     * Calculates the drawable size to draw.
     *
     * @param control The [Control] from where the drawer is used.
     * @param drawable The drawable to be drawn.
     * */
    protected open fun getSize(control: Control, drawable: Drawable): Size {
        if (properties.mode == Mode.FIXED)
            return Size(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight
            )
        val side = (control.radius * 2f).toInt()
        return Size(
            side,
            side
        )
    }

    /**
     * Adds the bitmap of the drawable for the specified direction.
     * @param bitmaps The list to add the bitmap to.
     * @param direction The target direction.
     * */
    protected fun addComponentBitmap(
        bitmaps: MutableList<DrawableBitCache>,
        direction: Control.Direction,
    ) {
        val cache = mCaches[direction]
        if (cache != null)
            bitmaps.add(cache)
    }

    override fun onDraw(canvas: Canvas, control: Control) {
        val bitmaps = mutableListOf<DrawableBitCache>()
        val direction = lastDirection ?: control.direction

        addComponentBitmap(
            bitmaps,
            direction
        )

        if (
            bitmaps.isEmpty() &&
            direction != Control.Direction.NONE &&
            lastType == Control.DirectionType.COMPLETE
        ) {
            // If there is no specific drawable for the direction,
            // try to compose it using the available ones.

            when (direction) {
                Control.Direction.UP_RIGHT -> {
                    addComponentBitmap(bitmaps, Control.Direction.UP)
                    addComponentBitmap(bitmaps, Control.Direction.RIGHT)
                }

                Control.Direction.UP_LEFT -> {
                    addComponentBitmap(bitmaps, Control.Direction.UP)
                    addComponentBitmap(bitmaps, Control.Direction.LEFT)
                }

                Control.Direction.DOWN_RIGHT -> {
                    addComponentBitmap(bitmaps, Control.Direction.DOWN)
                    addComponentBitmap(bitmaps, Control.Direction.RIGHT)
                }

                Control.Direction.DOWN_LEFT -> {
                    addComponentBitmap(bitmaps, Control.Direction.DOWN)
                    addComponentBitmap(bitmaps, Control.Direction.LEFT)
                }

                else -> {}
            }
        }

        val center = control.center
        bitmaps.forEach {
            val size = getSize(control, it.drawable)
            val dest = RectFFactory.withCenterAt(center, size.width / 2f, size.height / 2f)
            canvas.drawBitmap(it.bitmap, null, dest, null)
        }
    }
}