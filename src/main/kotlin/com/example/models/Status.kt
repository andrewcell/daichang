package com.example.models

/**
 * Status of each equipment. It is available to use, not available to use, or marked as to be disposed.
 *
 * @property value value to store as in DBMS or handle as in web request or routing.
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
enum class Status(val value: String) {
    AVAILABLE("사용가능"), NOT_AVAILABLE("사용불가"), TO_BE_DISPOSE("폐기예정");

    companion object {

        /**
         * Return Status object from [value].
         * @param value value to find. It has to be same.
         * @return Status exactly has same [value]
         */
        fun findByValue(value: String) = values().find { it.value == value }
    }
}