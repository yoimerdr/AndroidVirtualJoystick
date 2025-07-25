package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.control.drawer.circle.RatioCircleControlDrawer
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * [Control] that uses by default by the [RatioCircleControlDrawer].
 */
open class CircleControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    directionType: DirectionType,
    radiusRatio: Float
) : SimpleControl(
    RatioCircleControlDrawer(colors, radiusRatio),
    invalidRadius,
    directionType
)