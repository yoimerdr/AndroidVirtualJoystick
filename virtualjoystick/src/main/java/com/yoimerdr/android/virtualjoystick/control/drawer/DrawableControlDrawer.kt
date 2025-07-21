package com.yoimerdr.android.virtualjoystick.control.drawer

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.position.ImmutablePosition
import com.yoimerdr.android.virtualjoystick.geometry.factory.RectFFactory
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme
import com.yoimerdr.android.virtualjoystick.utils.log.Logger

/**
 * A [ControlDrawer] that draws an drawable resource.
 */
open class DrawableControlDrawer(
    /**
     * The drawable drawer properties.
     */
    private val properties: DrawableProperties
) : ColorfulControlDrawer(properties) {


    /**
     * @param drawable The drawable resource to be drawn.
     * @param scale The scale ratio to scale the dimensions of the [drawable].
     */
    @JvmOverloads
    constructor(drawable: Drawable, scale: Float, @ColorInt color: Int = Color.TRANSPARENT) : this(
        DrawableProperties(
            drawable,
            scale,
            color,
        )
    )


    /**
     * @param drawable The drawable resource to be drawn.
     */
    @JvmOverloads
    constructor(drawable: Drawable, @ColorInt color: Int = Color.TRANSPARENT) : this(drawable, 1f, color)

    init {
        properties.apply {
            if (colors.primary != Color.TRANSPARENT) {
                paint.colorFilter = PorterDuffColorFilter(colors.primary, PorterDuff.Mode.SRC_IN)
            }
        }
    }


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

    override var primaryColor: Int
        get() = super.primaryColor
        set(@ColorInt color) {
            if (color != Color.TRANSPARENT) {
                colors.primary = color
                paint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
            }
        }

    /**
     * Gets the bitmap to draw.
     */
    protected open val bitmap: Bitmap
        get() {
            if (mBitmap == null)
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


    open class DrawableProperties @JvmOverloads constructor(
        var drawable: Drawable,
        var scale: Float,
        @ColorInt color: Int = Color.TRANSPARENT
    ) : ColorfulProperties(ColorsScheme(color, Color.TRANSPARENT))

    companion object {

        /**
         * Obtains a drawable from a context and a [DrawableRes] id.
         *
         * @param context The current activity or view context.
         * @param resourceId The drawable resource id.
         * @throws NotFoundException if doesn't exist a drawable for the given resourceId.
         */
        @JvmStatic
        @Throws(NotFoundException::class)
        fun getDrawable(context: Context, @DrawableRes resourceId: Int): Drawable {
            val drawable = ContextCompat.getDrawable(context, resourceId)
            if (drawable != null)
                return drawable
            throw NotFoundException("Don't exists a valid drawable for given resource id")
        }

        /**
         * Instance a [DrawableControlDrawer] from context and a [DrawableRes] id.
         * @param context The current activity or view context.
         * @param id The drawable resource id.
         * @param scale The scale ratio to scale the drawable. Must be a value greater than zero.
         * @throws NotFoundException If doesn't exist a drawable for the given id or the [scale] is not positive.
         */
        @JvmStatic
        @Throws(NotFoundException::class)
        fun fromDrawableRes(
            context: Context,
            @DrawableRes id: Int,
            scale: Float,
            @ColorInt color: Int,
        ): DrawableControlDrawer {
            return DrawableControlDrawer(getDrawable(context, id), scale, color)
        }

        /**
         * Instance a [DrawableControlDrawer] from context and a [DrawableRes] id.
         * @param context The current activity or view context.
         * @param id The drawable resource id.
         * @param scale The scale ratio to scale the drawable. Must be a value greater than zero.
         * @throws NotFoundException If doesn't exist a drawable for the given id or the [scale] is not positive.
         */
        @JvmStatic
        @Throws(NotFoundException::class)
        fun fromDrawableRes(
            context: Context,
            @DrawableRes id: Int,
            scale: Float
        ): DrawableControlDrawer {
            return fromDrawableRes(context, id, scale, Color.TRANSPARENT)
        }

        /**
         * Instance a [DrawableControlDrawer] from context and a [DrawableRes] id.
         * @param context The current activity or view context.
         * @param id The drawable resource id.
         * @throws NotFoundException If doesn't exist a drawable for the given id.
         */
        @JvmStatic
        @Throws(NotFoundException::class)
        fun fromDrawableRes(
            context: Context,
            @DrawableRes id: Int,
            @ColorInt color: Int,
        ): DrawableControlDrawer {
            return fromDrawableRes(context, id, 1f, color)
        }

        /**
         * Instance a [DrawableControlDrawer] from context and a [DrawableRes] id.
         * @param context The current activity or view context.
         * @param id The drawable resource id.
         * @throws NotFoundException if doesn't exist a drawable for the given id.
         */
        @JvmStatic
        @Throws(NotFoundException::class)
        fun fromDrawableRes(context: Context, @DrawableRes id: Int): DrawableControlDrawer {
            return fromDrawableRes(context, id, Color.TRANSPARENT)
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
                    "To avoid unexpected behavior, the width and height of the drawable should be the same."
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
        val max = control.radius - maxOf(halfWidth, halfHeight)
        return if (max <= 0) {
            Logger.errorFromClass(
                this,
                "The size of the scaled drawable (width and height) is too large with respect to the view where the control is used. Try scaling it using a smaller value for the scale property of this drawer."
            )
            control.center
        } else if (control.distance > max)
            Circle.fromImmutableCenter(max, control.center)
                .parametricPositionOf(control.angle)
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