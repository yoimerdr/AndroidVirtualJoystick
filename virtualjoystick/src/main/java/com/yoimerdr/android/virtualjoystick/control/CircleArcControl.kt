package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.drawer.shapes.arc.CircleArcDrawer
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * [Control] that uses by default by the [CircleArcDrawer.withRatio].
 */
open class CircleArcControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    directionType: DirectionType,
    strokeWidth: Float,
    sweepAngle: Float,
    radiusProportion: Float,
) : SimpleControl(
    CircleArcDrawer.withRatio(colors, strokeWidth, sweepAngle, radiusProportion),
    invalidRadius,
    directionType
)