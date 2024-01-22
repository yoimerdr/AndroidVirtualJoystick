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
import java.security.InvalidParameterException

open class DrawableControlDrawer(
    protected open val drawable: Drawable,
    protected open val paint: Paint?
) : ControlDrawer {

    protected open val halfWidth: Float get() = drawable.intrinsicWidth / 2f

    protected open val halfHeight: Float get() = drawable.intrinsicHeight / 2f

    constructor(drawable: Drawable) : this(drawable, null)

    constructor(context: Context, @DrawableRes resourceId: Int, paint: Paint?) :
            this(getValidDrawable(context, resourceId), paint)

    constructor(context: Context, @DrawableRes resourceId: Int) : this(context, resourceId, null)

    companion object {
        @Throws(InvalidParameterException::class)
        fun getValidDrawable(context: Context, @DrawableRes resourceId: Int): Drawable {
            val drawable = ContextCompat.getDrawable(context, resourceId)
            if(drawable != null)
                return drawable
            throw InvalidParameterException("Don't exists a valid drawable for given resource id")
        }
    }

    override fun draw(canvas: Canvas, control: Control) {
        canvas.drawBitmap(drawable.toBitmap(), null, getDestination(control), paint)
    }

    protected open fun getDestination(control: Control): RectF {
        val halfX = halfWidth
        val halfY = halfHeight

        return control.immutablePosition.let {
            val x = it.x
            val y = it.y

            RectF(x - halfX, y - halfY, x + halfX, y + halfY)
        }

    }
}