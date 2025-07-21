package com.yoimerdr.android.virtualjoystick.control

import com.yoimerdr.android.virtualjoystick.control.drawer.CircleControlDrawer
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme

/**
 * [Control] that uses by default by the [CircleControlDrawer].
 */
open class CircleControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    directionType: DirectionType,
    radiusRatio: Float
) : SimpleControl(
    CircleControlDrawer(colors, radiusRatio),
    invalidRadius,
    directionType
)