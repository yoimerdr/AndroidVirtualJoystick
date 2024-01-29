package com.yoimerdr.android.virtualjoystick.control.drawer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.ImmutablePosition
import com.yoimerdr.android.virtualjoystick.geometry.factory.RectFFactory
import com.yoimerdr.android.virtualjoystick.utils.log.Logger

/**
 * A [ControlDrawer] that draws an drawable resource.
 */
open class DrawableControlDrawer(
    drawable: Drawable,
    /**
     * The drawer paint.
     */
    protected val paint: Paint?
) : ControlDrawer {

    /**
     * The drawable to draw.
     */
    var drawable: Drawable = drawable
        set(value) {
            field = value
            checkDrawableSize()
        }

    /**
     * Gets the drawable half width
     */
    protected open val halfWidth: Float get() = drawable.intrinsicWidth / 2f

    /**
     * Gets the drawable half height
     */
    protected open val halfHeight: Float get() = drawable.intrinsicHeight / 2f

    constructor(drawable: Drawable) : this(drawable, null)

    init {
        checkDrawableSize()
    }

    companion object {

        /**
         * Obtains a drawable from a context and a [DrawableRes] id.
         *
         * @param context The current activity or view context.
         * @param resourceId The drawable resource id.
         * @throws IllegalArgumentException if doesn't exist a drawable for the given resourceId.
         */
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun getDrawable(context: Context, @DrawableRes resourceId: Int): Drawable {
            val drawable = ContextCompat.getDrawable(context, resourceId)
            if (drawable != null)
                return drawable
            throw IllegalArgumentException("Don't exists a valid drawable for given resource id")
        }

        /**
         * Instance a [DrawableControlDrawer] from context and a [DrawableRes] id.
         * @param context The current activity or view context.
         * @param id The drawable resource id.
         * @param paint The [DrawableControlDrawer] paint.
         * @throws IllegalArgumentException if doesn't exist a drawable for the given id.
         */
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun fromDrawableRes(context: Context, @DrawableRes id: Int, paint: Paint?): DrawableControlDrawer {
            return DrawableControlDrawer(getDrawable(context, id), paint)
        }

        /**
         * Instance a [DrawableControlDrawer] from context and a [DrawableRes] id.
         * @param context The current activity or view context.
         * @param id The drawable resource id.
         * @throws IllegalArgumentException if doesn't exist a drawable for the given id.
         */
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun fromDrawableRes(context: Context, @DrawableRes id: Int): DrawableControlDrawer {
            return fromDrawableRes(context, id, null)
        }
    }

    protected fun checkDrawableSize() {
        drawable.apply {
            if (intrinsicHeight != intrinsicWidth)
                Logger.error(
                    this@DrawableControlDrawer,
                    "To avoid unexpected behavior, the width and height of the drawable should be the same or should not differ too much."
                )
        }
    }

    /**
     * Gets the current position where the control is located
     * and that the drawer will take as center to draw the drawable.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getPosition(control: Control): ImmutablePosition {
        val max = control.viewRadius - maxOf(halfWidth, halfHeight)

        return if (control.distanceFromCenter > max)
            Circle.fromImmutableCenter(max, control.center)
                .parametricPositionOf(control.anglePosition)
        else control.position
    }

    /**
     * Gets the rectangle that the drawable will be scaled/translated to fit into.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getDestination(control: Control): RectF {
        return RectFFactory.withCenterAt(getPosition(control), halfWidth, halfHeight)
    }

    override fun draw(canvas: Canvas, control: Control) {
        canvas.drawBitmap(drawable.toBitmap(), null, getDestination(control), paint)
    }
}