package com.yoimerdr.android.virtualjoystick.api.drawable

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.yoimerdr.android.virtualjoystick.geometry.size.Size

/**
 * Cache a [Drawable] as a [Bitmap] to improve performance when drawing it multiple times.
 *
 * @param drawable The [Drawable] to cache.
 * @param size The desired size of the [Bitmap]. If null, the intrinsic size of the [Drawable] will be used.
 *
 */
class DrawableBitCache @JvmOverloads constructor(
    drawable: Drawable,
    size: Size? = null,
) {

    /**
     * The desired size of the bitmap.
     * */
    var size = size
        set(value) {
            if (field != value) {
                field = value
                recycle()
            }
        }

    /**
     * The drawable to be cached as a bitmap.
     * */
    var drawable = drawable
        set(value) {
            if (field != value) {
                field = value
                size = null
                recycle()
            }
        }

    /**
     * The width of the bitmap.
     *
     * If [size] is null, it's the intrinsic width of the [drawable].
     * */
    var width: Int
        get() = size?.width ?: drawable.intrinsicWidth
        set(value) {
            if (size == null)
                size = Size()

            if (size?.width != value) {
                size?.width = value
                recycle()
            }
        }

    /**
     * The height of the bitmap.
     *
     * If [size] is null, it's the intrinsic height of the [drawable].
     * */
    var height: Int
        get() = size?.height ?: drawable.intrinsicHeight
        set(value) {
            if (size == null)
                size = Size()

            if (size?.height != value) {
                size?.height = value
                recycle()
            }
        }

    private var mBitmap: Bitmap? = null

    /**
     * The cached bitmap of the [drawable].
     * */
    val bitmap: Bitmap
        get() {
            if (mBitmap == null)
                mBitmap = drawable.toBitmap(
                    width,
                    height
                )

            return mBitmap!!
        }

    /**
     * Recycles the cached bitmap.
     * */
    fun recycle() {
        mBitmap?.recycle()
        mBitmap = null
    }
}