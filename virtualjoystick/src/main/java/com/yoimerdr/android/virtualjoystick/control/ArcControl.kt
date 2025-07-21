package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.control.drawer.ArcControlDrawer
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * [Control] that uses by default by the [ArcControlDrawer].
 */
open class ArcControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    directionType: DirectionType,
    strokeWidth: Float,
    sweepAngle: Float,
) : SimpleControl(
    ArcControlDrawer(colors, strokeWidth, sweepAngle),
    invalidRadius,
    directionType
)