package com.yoimerdr.android.virtualjoystick.api.drawable

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.yoimerdr.android.virtualjoystick.geometry.size.Size

class DrawableBitCache @JvmOverloads constructor(
    drawable: Drawable,
    size: Size? = null,
) {

    var size = size
        private set

    var drawable = drawable
        set(value) {
            if (field != value) {
                field = value
                recycle()
            }
        }

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

    val bitmap: Bitmap
        get() {
            if (mBitmap == null)
                mBitmap = drawable.toBitmap(
                    width,
                    height
                )

            return mBitmap!!
        }

    fun recycle() {
        mBitmap?.recycle()
        mBitmap = null
    }

    fun setSize(size: Size) {
        if (this.size != size) {
            this.size = size
            recycle()
        }
    }
}