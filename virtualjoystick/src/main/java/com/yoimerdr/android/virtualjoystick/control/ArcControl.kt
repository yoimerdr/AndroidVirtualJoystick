package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.drawer.shapes.arc.ArcDrawer
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * [Control] that uses by default by the [ArcDrawer].
 */
open class ArcControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    directionType: DirectionType,
    strokeWidth: Float,
    sweepAngle: Float,
) : SimpleControl(
    ArcDrawer(colors, strokeWidth, sweepAngle),
    invalidRadius,
    directionType
)