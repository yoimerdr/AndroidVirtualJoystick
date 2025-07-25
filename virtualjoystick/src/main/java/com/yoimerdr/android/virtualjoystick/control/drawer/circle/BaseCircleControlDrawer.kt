package com.yoimerdr.android.virtualjoystick.control.drawer.circle

import android.graphics.Canvas
import android.graphics.RadialGradient
import android.graphics.Shader
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.control.drawer.ColorfulControlDrawer
import com.yoimerdr.android.virtualjoystick.geometry.Circle
import com.yoimerdr.android.virtualjoystick.geometry.position.ImmutablePosition
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

abstract class BaseCircleControlDrawer protected constructor(
    private val properties: BasicCircleProperties,
) : ColorfulControlDrawer(properties) {

    var isBounded: Boolean
        /**
         * Gets whether the circle is bounded by the control radius.
         */
        get() = properties.isBounded
        /**
         * Sets whether the circle is bounded by the control radius.
         * @param isBounded The new value for the bounded state.
         */
        set(isBounded) {
            properties.isBounded = isBounded
        }

    open class BasicCircleProperties(
        colors: ColorsScheme,
        var isBounded: Boolean,
    ) : ColorfulProperties(colors)

    /**
     * The [Shader] for the drawer paint.
     * @param control The [Control] from where the drawer is used.
     * @param position The position where the center of the circle is.
     */
    protected open fun getPaintShader(control: Control, position: ImmutablePosition): Shader {
        return position.let {
            RadialGradient(
                it.x, it.y, getCircleRadius(control).toFloat(),
                intArrayOf(accentColor, primaryColor),
                null,
                Shader.TileMode.CLAMP
            )
        }
    }

    /**
     * Gets the circle radius.
     * @param control The [Control] from where the drawer is used.
     */
    protected abstract fun getCircleRadius(control: Control): Double

    /**
     * Gets the maximum distance to where the center position of the circle can be.
     */
    protected open fun getMaxDistance(control: Control): Double = control.radius.let {
        if (isBounded)
            it - getCircleRadius(control)
        else it
    }

    /**
     * Gets the current position where the control is located
     * and that the drawer will take as center to draw the circle.
     * @param control The [Control] from where the drawer is used.
     */
    protected open fun getPosition(control: Control): ImmutablePosition {
        val maxRadius = getMaxDistance(control)
        return if (control.distance > maxRadius) {
            Circle.fromImmutableCenter(maxRadius, control.center)
                .parametricPositionOf(control.angle)
        } else control.position
    }

    override fun draw(canvas: Canvas, control: Control) {
        val position: ImmutablePosition = getPosition(control)
        paint.shader = getPaintShader(control, position)
        position.apply {
            canvas.drawCircle(x, y, getCircleRadius(control).toFloat(), paint)
        }
    }
}