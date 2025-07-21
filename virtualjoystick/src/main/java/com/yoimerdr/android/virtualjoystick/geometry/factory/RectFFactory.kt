package com.yoimerdr.android.virtualjoystick.geometry.factory

import android.graphics.RectF
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.position.ImmutablePosition

/**
 * A factory for create [RectF] objects.
 */
object RectFFactory {


    /**
     * Creates a Rect around a center position.
     *
     * This is equal to:
     * ```
     * val cx = position.x.
     * val cy = position.y
     * val rect = RectF(cx - x, cy - y, cx + x, cy + y)
     * ```
     * @param position The central position.
     * @param x The distance from the center position to the right and left side.
     * @param y The distance from the center position to the top and bottom side.
     */
    @JvmStatic
    fun withCenterAt(position: ImmutablePosition, x: Float, y: Float): RectF {
        return RectF(position.x - x, position.y - y, position.x + x, position.y + y)
    }

    /**
     * Creates a Rect around a center position.
     *
     * This is equal to:
     * ```
     * val cx = position.x.
     * val cy = position.y
     * val rect = RectF(cx - side, cy - side, cx + side, cy + side)
     * ```
     * @param position The central position.
     * @param side The distance from the center position to each rect side.
     */
    @JvmStatic
    fun withCenterAt(position: ImmutablePosition, side: Float): RectF {
        return withCenterAt(position, side, side)
    }

    /**
     * Creates a Rect around a circle.
     *
     * This method traits the circle center as the rect center
     * and circle's radius as side distance for each rect side.
     *
     * This is equal to:
     * ```
     * val cx = circle.center.x.
     * val cy = circle.center.y
     * val side = circle.radius
     * val rect = RectF(cx - side, cy - side, cx + side, cy + side)
     * ```
     * @param circle The circle with the center and side distance.
     */
    @JvmStatic
    fun fromCircle(circle: Circle): RectF {
        return withCenterAt(circle.center, circle.radius.toFloat())
    }

}