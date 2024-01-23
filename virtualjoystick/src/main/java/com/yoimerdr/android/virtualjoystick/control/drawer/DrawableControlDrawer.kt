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

/**
 * A [ControlDrawer] that draws an drawable resource.
 */
open class DrawableControlDrawer(
    /**
     * The drawable to draw.
     */
    protected open val drawable: Drawable,
    /**
     * The drawer paint.
     */
    protected open val paint: Paint?
) : ControlDrawer {

    /**
     * Gets the drawable half width
     */
    protected open val halfWidth: Float get() = drawable.intrinsicWidth / 2f

    /**
     * Gets the drawable half height
     */
    protected open val halfHeight: Float get() = drawable.intrinsicHeight / 2f

    constructor(drawable: Drawable) : this(drawable, null)

    companion object {
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun getValidDrawable(context: Context, @DrawableRes resourceId: Int): Drawable {
            val drawable = ContextCompat.getDrawable(context, resourceId)
            if(drawable != null)
                return drawable
            throw IllegalArgumentException("Don't exists a valid drawable for given resource id")
        }

        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun fromDrawableRes(context: Context, @DrawableRes id: Int, paint: Paint?): DrawableControlDrawer {
            return DrawableControlDrawer(getValidDrawable(context, id), paint)
        }

        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun fromDrawableRes(context: Context, @DrawableRes id: Int): DrawableControlDrawer {
            return fromDrawableRes(context, id, null)
        }
    }

    /**
     * The rectangle that the drawable will be scaled/translated to fit into.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getDestination(control: Control): RectF {
        val halfX = halfWidth
        val halfY = halfHeight

        return control.immutablePosition.let {
            val x = it.x
            val y = it.y

            RectF(x - halfX, y - halfY, x + halfX, y + halfY)
        }

    }

    override fun draw(canvas: Canvas, control: Control) {
        canvas.drawBitmap(drawable.toBitmap(), null, getDestination(control), paint)
    }
}