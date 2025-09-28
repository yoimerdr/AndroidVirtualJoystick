package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.drawer.shapes.circle.CircleDrawer
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * [Control] that uses by default the [CircleDrawer.withRatio] as drawer.
 */
open class CircleControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    directionType: DirectionType,
    radiusRatio: Float,
) : SimpleControl(
    CircleDrawer.withRatio(colors, radiusRatio),
    invalidRadius,
    directionType
)