package com.example

enum class Status(val value: String) {
    AVAILABLE("사용가능"), NOT_AVAILABLE("사용불가"), TO_BE_DISPOSE("폐기예정");

    companion object {
        fun findByValue(value: String) = values().find { it.value == value }
    }
}