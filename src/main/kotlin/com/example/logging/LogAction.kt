package com.example.logging

enum class LogAction(val value: String) {
    ADD("add"),
    MODIFY("modify"),
    DELETE("delete"),
    IMPORT("import"),
    EXPORT("export"),
    PRINT("print"),
    FILTER("filter");

    companion object {
        fun findByValue(value: String) = values().find { it.value == value }
    }
}