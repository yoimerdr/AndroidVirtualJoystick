package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.control.drawer.CircleArcControlDrawer
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * [Control] that uses by default by the [CircleArcControlDrawer].
 */
open class CircleArcControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    directionType: DirectionType,
    strokeWidth: Float,
    sweepAngle: Float,
    radiusProportion: Float
) : SimpleControl(
    CircleArcControlDrawer(colors, strokeWidth, sweepAngle, radiusProportion),
    invalidRadius,
    directionType
)