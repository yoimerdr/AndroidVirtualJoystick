package com.yoimerdr.android.virtualjoystick.utils.extensions

import kotlin.enums.EnumEntries

@JvmOverloads
fun <E : Enum<E>> EnumEntries<E>.firstOrdinal(ordinal: Int, default: E? = null): E {
    return firstOrNull { it.ordinal == ordinal } ?: (default ?: first { it.ordinal == 0 })
}
