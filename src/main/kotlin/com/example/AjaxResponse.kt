package com.example

import kotlinx.serialization.Serializable

@Serializable
data class AjaxResponse (
    val success: Boolean,
    val message: String?
)