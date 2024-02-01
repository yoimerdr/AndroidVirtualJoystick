package com.yoimerdr.android.virtualjoystick.control.drawer

import android.content.Context
import android.graphics.Bitmap
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
    private val properties: DrawableProperties
) : ControlDrawer {

    constructor(drawable: Drawable, scale: Float, paint: Paint?) : this(
        DrawableProperties(
            drawable,
            scale,
            paint
        )
    )

    constructor(drawable: Drawable, scale: Float) : this(drawable, scale, null)
    constructor(drawable: Drawable, paint: Paint?) : this(drawable, 1f, paint)
    constructor(drawable: Drawable) : this(drawable, null)

    /**
     * The drawable resource.
     */
    open var drawable: Drawable
        get() = properties.drawable
        set(value) {
            properties.drawable = value
            checkDrawableSize()
            convertDrawableToBitmap()
        }

    /**
     * Getter and setter for scaling the size of the drawable.
     *
     * @throws IllegalArgumentException If when set the value is negative.
     */
    @set:Throws(IllegalArgumentException::class)
    open var scale: Float
        get() = properties.scale
        set(value) {
            if (value <= 0)
                throw IllegalArgumentException("The scale value must be a value greater than zero.")
            if (scale != value) {
                properties.scale = value
                convertDrawableToBitmap()
            }
        }

    /**
     * The drawer paint.
     */
    protected open val paint: Paint? get() = properties.paint

    /**
     * Gets the bitmap to draw.
     */
    protected val bitmap: Bitmap get() {
        if(mBitmap == null)
            convertDrawableToBitmap()
        return mBitmap!!
    }

    /**
     * The drawable bitmap
     */
    private var mBitmap: Bitmap? = null

    init {
        checkDrawableSize()
        convertDrawableToBitmap()
    }

    open class DrawableProperties(var drawable: Drawable, var scale: Float, val paint: Paint?)

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
         * @param scale The scale proportion to scale the drawable. Must be a value greater than zero.
         * @param paint The [DrawableControlDrawer] paint.
         * @throws IllegalArgumentException If doesn't exist a drawable for the given id or the [scale] is not positive.
         */
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun fromDrawableRes(
            context: Context,
            @DrawableRes id: Int,
            scale: Float,
            paint: Paint?
        ): DrawableControlDrawer {
            return DrawableControlDrawer(getDrawable(context, id), scale, paint)
        }

        /**
         * Instance a [DrawableControlDrawer] from context and a [DrawableRes] id.
         * @param context The current activity or view context.
         * @param id The drawable resource id.
         * @param scale The scale proportion to scale the drawable. Must be a value greater than zero.
         * @throws IllegalArgumentException If doesn't exist a drawable for the given id or the [scale] is not positive.
         */
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun fromDrawableRes(
            context: Context,
            @DrawableRes id: Int,
            scale: Float
        ): DrawableControlDrawer {
            return DrawableControlDrawer.fromDrawableRes(context, id, scale, null)
        }

        /**
         * Instance a [DrawableControlDrawer] from context and a [DrawableRes] id.
         * @param context The current activity or view context.
         * @param id The drawable resource id.
         * @param paint The [DrawableControlDrawer] paint.
         * @throws IllegalArgumentException If doesn't exist a drawable for the given id.
         */
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun fromDrawableRes(
            context: Context,
            @DrawableRes id: Int,
            paint: Paint?
        ): DrawableControlDrawer {
            return DrawableControlDrawer.fromDrawableRes(context, id, 1f, paint)
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
            return DrawableControlDrawer.fromDrawableRes(context, id, null)
        }
    }

    /**
     * Getter for the drawable scaled width.
     */
    protected val width: Float get() = drawable.intrinsicWidth * scale

    /**
     * Getter for the drawable scaled height.
     */
    protected val height: Float get() = drawable.intrinsicHeight * scale

    /**
     * Gets the scaled half [width].
     */
    protected val halfWidth: Float get() = width / 2f

    /**
     * Gets the scaled half [height].
     */
    protected val halfHeight: Float get() = height / 2f

    /**
     * Gets the bitmap to draw.
     */


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
     * Convert the drawable to a bitmap according the [scale] value.
     */
    @Throws(IllegalArgumentException::class)
    protected fun convertDrawableToBitmap() {
        mBitmap?.recycle()
        mBitmap = drawable.toBitmap(width.toInt(), height.toInt())
    }

    /**
     * Gets the current position where the control is located
     * and that the drawer will take as center to draw the drawable.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getPosition(control: Control): ImmutablePosition {
        val max = control.viewRadius - maxOf(halfWidth, halfHeight)
        return if (max <= 0) {
            Logger.error(
                this,
                "The size of the scaled drawable (width and height) is too large with respect to the view where the control is used. Try scaling it using the scale property of this drawer using a smaller value."
            )
            control.center
        } else if (control.distanceFromCenter > max)
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
        canvas.drawBitmap(bitmap, null, getDestination(control), paint)
    }
}