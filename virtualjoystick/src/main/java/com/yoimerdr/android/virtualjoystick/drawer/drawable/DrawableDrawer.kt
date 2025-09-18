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
import com.yoimerdr.android.virtualjoystick.api.log.LoggerSupplier.withLogger
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

    /**
     * The bitmap cache for the drawable.
     * */
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
     * @param color The primary color to tint the drawable.
     * @param isBounded Indicates whether the maximum distance is bounded.
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
     * @param color The primary color to tint the drawable.
     * @param isBounded Indicates whether the maximum distance is bounded.
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

    /**
     * @param drawable The drawable resource to be drawn.
     * @param scale The scale ratio to scale the dimensions of the [drawable].
     * @param colors The colors for the drawer.
     * @param isBounded Indicates whether the maximum distance is bounded.
     * */
    open class DrawableProperties @JvmOverloads constructor(
        drawable: Drawable,
        @FloatRange(
            from = 0.0,
            fromInclusive = false
        )
        scale: Float,
        colors: ColorsScheme,
        /**
         * Indicates whether the maximum distance is bounded.
         * */
        var isBounded: Boolean = true,
    ) : ColorfulProperties(colors) {

        /**
         * Properties for a [DrawableDrawer].
         * @param drawable The drawable resource to be drawn.
         * @param scale The scale ratio to scale the dimensions of the [drawable].
         * Must be a value greater than zero.
         * @param color The primary color to tint the drawable.
         * @param isBounded Indicates whether the maximum distance is bounded.
         * */
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

        /**
         * The scale ratio
         * */
        var scale = scale.greaterThan(0f)
            /**
             * Sets the scale ratio. Must be a value greater than zero.
             * */
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

        /**
         * Gets the drawable resource.
         * */
        open var drawable: Drawable = drawable
            /**
             * Sets the drawable resource and applies the primary color as tint.
             * */
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
         */
        protected fun checkDrawableSize() {
            drawable.apply {
                if (intrinsicHeight != intrinsicWidth)
                    withLogger("DrawableProperties") {
                        error(
                            "To avoid unexpected behavior, the width and height of the drawable should be the same.",
                        )
                    }
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
         *
         * @param context The current activity or view context.
         * @param id The drawable resource id.
         * @param scale The scale ratio to scale the drawable. Must be a value greater than zero.
         * @param color The primary color to tint the drawable.
         * @param isBounded Indicates whether the maximum distance is bounded.
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
         * @param color The primary color to tint the drawable.
         * @param isBounded Indicates whether the maximum distance is bounded.
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


    /**
     * Calculates the maximum distance from the center of the control
     * that the drawable can reach.
     * */
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
            withLogger("DrawableDrawer") {
                error(
                    "The size of the scaled drawable is too large with respect to the view.",
                    "Try scaling it using a smaller value for the scale property of this drawer."
                )
            }

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
        super.onChange()
    }
}