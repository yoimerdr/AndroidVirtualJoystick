package com.yoimerdr.android.virtualjoystick.enums

enum class ControlDrawerType(val id: Int) {
    CIRCLE(0),
    ARC(1);
    companion object {
        /**
         * @param id The id for the enum value
         * @return The enum value for the given id. If not found, returns the value [CIRCLE].
         */
        fun fromId(id: Int): ControlDrawerType {
            for(type in entries)
                if(type.id == id)
                    return type

            return CIRCLE
        }
    }
}