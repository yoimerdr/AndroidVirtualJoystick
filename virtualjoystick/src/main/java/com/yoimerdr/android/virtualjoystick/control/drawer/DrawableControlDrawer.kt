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
    /**
     * The drawable drawer properties.
     */
    private val properties: DrawableProperties
) : ControlDrawer {

    /**
     * @param drawable The drawable resource to be drawn.
     * @param scale The scale ratio to scale the dimensions of the [drawable].
     * @param paint The paint to use to draw the [drawable].
     */
    constructor(drawable: Drawable, scale: Float, paint: Paint?) : this(
        DrawableProperties(
            drawable,
            scale,
            paint
        )
    )

    /**
     * @param drawable The drawable resource to be drawn.
     * @param scale The scale ratio to scale the dimensions of the [drawable].
     */
    constructor(drawable: Drawable, scale: Float) : this(drawable, scale, null)

    /**
     * @param drawable The drawable resource to be drawn.
     * @param paint The paint to use to draw the [drawable].
     */
    constructor(drawable: Drawable, paint: Paint?) : this(drawable, 1f, paint)

    /**
     * @param drawable The drawable resource to be drawn.
     */
    constructor(drawable: Drawable) : this(drawable, null)


    open var drawable: Drawable
        /**
         * Gets the drawable resource.
         */
        get() = properties.drawable
        /**
         * Sets the drawable resource.
         * @param drawable The new drawable resource.
         */
        set(drawable) {
            properties.drawable = drawable
            checkDrawableSize()
            convertDrawableToBitmap()
        }

    open var scale: Float
        /**
         * Gets the scale for the dimensions of the size of the drawable resource.
         */
        get() = properties.scale

        /**
         * Sets the scale for the dimensions of the size of the drawable resource.
         * @throws scale The new scale value.
         * @throws IllegalArgumentException If the scale is negative or zero.
         */
        @Throws(IllegalArgumentException::class)
        set(scale) {
            if (scale <= 0)
                throw IllegalArgumentException("The scale value must be a value greater than zero.")
            if (this.scale != scale) {
                properties.scale = scale
                convertDrawableToBitmap()
            }
        }

    /**
     * Gets the drawer paint.
     */
    protected open val paint: Paint? get() = properties.paint

    /**
     * Gets the bitmap to draw.
     */
    protected open val bitmap: Bitmap get() {
        if(mBitmap == null)
            convertDrawableToBitmap()
        return mBitmap!!
    }

    /**
     * The drawable bitmap.
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
         * @param scale The scale ratio to scale the drawable. Must be a value greater than zero.
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
         * @param scale The scale ratio to scale the drawable. Must be a value greater than zero.
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
     * Gets the scaled width dimension of the drawable.
     */
    protected val width: Float get() = drawable.intrinsicWidth * scale

    /**
     * Gets for the scaled height dimension of the drawable.
     */
    protected val height: Float get() = drawable.intrinsicHeight * scale

    /**
     * Gets half the value of [width].
     */
    protected val halfWidth: Float get() = width / 2f

    /**
     * Gets half the value of [height].
     */
    protected val halfHeight: Float get() = height / 2f


    /**
     * Check whether the dimensions of the drawable are similar,
     * if not log an error message.
     * @see [Logger.errorFromClass]
     */
    protected fun checkDrawableSize() {
        drawable.apply {
            if (intrinsicHeight != intrinsicWidth)
                Logger.errorFromClass(
                    this@DrawableControlDrawer,
                    "To avoid unexpected behavior, the width and height of the drawable should be the same or should not differ too much."
                )
        }
    }

    /**
     * Convert the drawable to a bitmap according the [width] and [height] dimensions.
     *
     * @see [bitmap]
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
            Logger.errorFromClass(
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