package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.control.drawer.arc.RatioCircleArcControlDrawer
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * [Control] that uses by default by the [RatioCircleArcControlDrawer].
 */
open class CircleArcControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    directionType: DirectionType,
    strokeWidth: Float,
    sweepAngle: Float,
    radiusProportion: Float
) : SimpleControl(
    RatioCircleArcControlDrawer(colors, strokeWidth, sweepAngle, radiusProportion),
    invalidRadius,
    directionType
)