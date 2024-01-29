package com.yoimerdr.android.virtualjoystick.enums

enum class DirectionType(val id: Int) {

    EIGHT(1),
    FOUR(2);

    companion object {
        /**
         * @param id The id for the enum value
         * @return The enum value for the given id. If not found, returns the value [EIGHT].
         */
        fun fromId(id: Int): DirectionType {
            for(type in DirectionType.entries)
                if(type.id == id)
                    return type

            return DirectionType.EIGHT
        }
    }
}