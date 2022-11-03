package com.example.logging

/**
 * Action type of log entry
 */
enum class LogAction(val value: String) {
    ADD("add"),
    MODIFY("modify"),
    DELETE("delete"),
    IMPORT("import"),
    EXPORT("export"),
    PRINT("print"),
    FILTER("filter");

    companion object {
        /**
         * Return LogAction object from [value].
         * @param value value to find. It has to be same.
         * @return Status exactly has same [value]
         */
        fun findByValue(value: String) = values().find { it.value == value }
    }
}