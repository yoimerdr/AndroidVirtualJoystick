package com.yoimerdr.android.virtualjoystick.control.builder

import android.graphics.Color
import androidx.annotation.ColorInt
import com.yoimerdr.android.virtualjoystick.control.ArcControl
import com.yoimerdr.android.virtualjoystick.control.CircleArcControl
import com.yoimerdr.android.virtualjoystick.control.CircleControl
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.control.drawer.ArcControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.CircleControlDrawer
import com.yoimerdr.android.virtualjoystick.enums.ControlType
import com.yoimerdr.android.virtualjoystick.enums.DirectionType
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * A builder class to build the controls defined in the package.
 */
class ControlBuilder {
    private val colors: ColorsScheme = ColorsScheme(Color.RED, Color.WHITE)
    private var type: ControlType = ControlType.CIRCLE
    private var directionType: DirectionType = DirectionType.EIGHT
    private var invalidRadius: Float = 70f

    // for arc type
    private var arcStrokeWidth: Float = 13f
    private var arcSweepAngle: Float = 90f

    // for circle type
    private var circleRadiusProportion: Float = 0.25f

    fun primaryColor(@ColorInt color: Int): ControlBuilder {
        colors.primary = color
        return this
    }

    fun accentColor(@ColorInt color: Int): ControlBuilder {
        colors.accent = color
        return this
    }

    fun colors(@ColorInt primary: Int, @ColorInt accent: Int): ControlBuilder {
        return primaryColor(primary)
            .accentColor(accent)
    }

    fun colors(scheme: ColorsScheme): ControlBuilder {
        return colors(scheme.primary, scheme.accent)
    }

    fun invalidRadius(radius: Float): ControlBuilder {
        invalidRadius = radius
        return this
    }

    fun invalidRadius(radius: Double): ControlBuilder = invalidRadius(radius.toFloat())

    fun arcStrokeWidth(width: Float): ControlBuilder {
        arcStrokeWidth = ArcControlDrawer.getValidStrokeWidth(width)
        return this
    }

    fun arcStrokeWidth(width: Double) = arcStrokeWidth(width.toFloat())

    fun arcStrokeWidth(width: Int) = arcStrokeWidth(width.toFloat())

    fun arcSweepAngle(angle: Float): ControlBuilder {
        arcSweepAngle = ArcControlDrawer.getValidSweepAngle(angle)
        return this
    }

    fun arcSweepAngle(angle: Double) = arcSweepAngle(angle.toFloat())

    fun arcSweepAngle(angle: Int) = arcSweepAngle(angle.toFloat())

    fun circleRadiusProportion(proportion: Float): ControlBuilder {
        circleRadiusProportion = CircleControlDrawer.getRadiusProportion(proportion)
        return this
    }

    fun circleRadiusProportion(proportion: Double) = circleRadiusProportion(proportion.toFloat())

    fun type(type: ControlType): ControlBuilder {
        this.type = type
        return this
    }

    fun directionType(type: DirectionType): ControlBuilder {
        this.directionType = type
        return this
    }

    fun build(): Control {
        return when (type) {
            ControlType.ARC -> ArcControl(
                colors,
                invalidRadius,
                directionType,
                arcStrokeWidth,
                arcSweepAngle
            )

            ControlType.CIRCLE_ARC -> CircleArcControl(
                colors,
                invalidRadius,
                directionType,
                arcStrokeWidth,
                arcSweepAngle,
                circleRadiusProportion
            )

            ControlType.CIRCLE -> CircleControl(
                colors,
                invalidRadius,
                directionType,
                circleRadiusProportion
            )
        }
    }
}