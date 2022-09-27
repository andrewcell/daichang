package com.example

import kotlinx.serialization.Serializable

@Serializable
data class StaticData(
    val modelNameToCPU: Map<String, String>,
    val mfr: Map<String, List<String>>
)