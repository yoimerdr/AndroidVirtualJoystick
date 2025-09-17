package com.yoimerdr.android.virtualjoystick.drawer.drawable

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import com.yoimerdr.android.virtualjoystick.api.drawable.DrawableBitCache
import com.yoimerdr.android.virtualjoystick.api.log.Logger
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.drawer.core.ControlDrawer
import com.yoimerdr.android.virtualjoystick.drawer.core.SimpleDrawer
import com.yoimerdr.android.virtualjoystick.drawer.core.ColorfulProperties
import com.yoimerdr.android.virtualjoystick.extensions.greaterThan
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.factory.RectFFactory
import com.yoimerdr.android.virtualjoystick.geometry.position.ImmutablePosition
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * A [ControlDrawer] that draws an drawable resource.
 */
open class DrawableDrawer(
    /**
     * The drawable drawer properties.
     */
    override val properties: DrawableProperties,
) : SimpleDrawer(properties) {

    private var mCache: DrawableBitCache? = null

    protected val cache: DrawableBitCache
        get() {
            if (mCache == null) {
                mCache = DrawableBitCache(properties.drawable)
            }
            return mCache!!
        }

    /**
     * @param drawable The drawable resource to be drawn.
     * @param scale The scale ratio to scale the dimensions of the [drawable].
     */
    @JvmOverloads
    constructor(
        drawable: Drawable,
        @FloatRange(
            from = 0.0,
            fromInclusive = false
        )
        scale: Float,
        @ColorInt color: Int = Color.TRANSPARENT,
        isBounded: Boolean = true,
    ) : this(
        DrawableProperties(
            drawable,
            scale,
            color,
            isBounded
        )
    )


    /**
     * @param drawable The drawable resource to be drawn.
     */
    @JvmOverloads
    constructor(
        drawable: Drawable,
        @ColorInt color: Int = Color.TRANSPARENT,
        isBounded: Boolean = true,
    ) : this(
        drawable,
        1f,
        color,
        isBounded
    )

    open class DrawableProperties @JvmOverloads constructor(
        drawable: Drawable,
        @FloatRange(
            from = 0.0,
            fromInclusive = false
        )
        scale: Float,
        colors: ColorsScheme,
        var isBounded: Boolean = true,
    ) : ColorfulProperties(colors) {

        @JvmOverloads
        constructor(
            drawable: Drawable,
            @FloatRange(
                from = 0.0,
                fromInclusive = false
            )
            scale: Float,
            color: Int = Color.TRANSPARENT,
            isBounded: Boolean = true,
        ) : this(
            drawable,
            scale,
            ColorsScheme(color),
            isBounded
        )

        init {
            drawable.setTint(colors.primary)
        }

        var scale = scale.greaterThan(0f)
            set(value) {
                field = value.greaterThan(0f)
                hasChanged = true
            }

        override var primaryColor: Int
            get() = super.primaryColor
            set(color) {
                super.primaryColor = color
                drawable.setTint(color)
            }

        override var accentColor: Int
            get() = super.accentColor
            set(value) {
                super.accentColor = value
                hasChanged = false
            }

        open var drawable: Drawable = drawable
            set(value) {
                if (field != value) {
                    field = value
                    field.setTint(primaryColor)
                    checkDrawableSize()
                    hasChanged = true
                }
            }


        /**
         * Check whether the dimensions of the drawable are similar,
         * if not log an error message.
         * @see [Logger.errorFromClass]
         */
        protected fun checkDrawableSize() {
            drawable.apply {
                if (intrinsicHeight != intrinsicWidth)
                    Logger.errorFromClass(
                        this@DrawableProperties,
                        "To avoid unexpected behavior, the width and height of the drawable should be the same."
                    )
            }
        }
    }

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
         * Instance a [DrawableDrawer] from context and a [DrawableRes] id.
         * @param context The current activity or view context.
         * @param id The drawable resource id.
         * @param scale The scale ratio to scale the drawable. Must be a value greater than zero.
         * @throws NotFoundException If doesn't exist a drawable for the given id or the [scale] is not positive.
         */
        @JvmStatic
        @JvmOverloads
        @Throws(NotFoundException::class)
        fun fromDrawableRes(
            context: Context,
            @DrawableRes id: Int,
            scale: Float,
            @ColorInt color: Int = Color.TRANSPARENT,
            isBounded: Boolean = true,
        ): DrawableDrawer {
            return DrawableDrawer(getDrawable(context, id), scale, color, isBounded)
        }

        /**
         * Instance a [DrawableDrawer] from context and a [DrawableRes] id.
         * @param context The current activity or view context.
         * @param id The drawable resource id.
         * @throws NotFoundException If doesn't exist a drawable for the given id.
         */
        @JvmStatic
        @JvmOverloads
        @Throws(NotFoundException::class)
        fun fromDrawableRes(
            context: Context,
            @DrawableRes id: Int,
            @ColorInt color: Int = Color.TRANSPARENT,
            isBounded: Boolean = true,
        ): DrawableDrawer {
            return fromDrawableRes(context, id, 1f, color, isBounded)
        }

    }

    /**
     * Gets the scaled width dimension of the drawable.
     */
    protected val width: Float
        get() = properties.drawable.intrinsicWidth * properties.scale

    /**
     * Gets for the scaled height dimension of the drawable.
     */
    protected val height: Float
        get() = properties.drawable.intrinsicHeight * properties.scale

    /**
     * Gets half the value of [width].
     */
    protected val halfWidth: Float get() = width / 2f

    /**
     * Gets half the value of [height].
     */
    protected val halfHeight: Float get() = height / 2f


    protected open fun getMaxRadius(control: Control): Double {
        return if (properties.isBounded)
            control.radius - maxOf(halfWidth, halfHeight)
        else control.radius
    }

    /**
     * Gets the current position where the control is located
     * and that the drawer will take as center to draw the drawable.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getPosition(control: Control): ImmutablePosition {
        val max = getMaxRadius(control)
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

    override fun onDraw(canvas: Canvas, control: Control) {
        canvas.drawBitmap(cache.bitmap, null, getDestination(control), properties.paint)
    }

    override fun onChange() {
        cache.recycle()
        cache.width = width.toInt()
        cache.height = height.toInt()
    }
}