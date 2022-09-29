package com.example

import kotlinx.serialization.Serializable

@Serializable
data class PrintPayLoad(
    val pc: List<String>? = null,
    val laptop: List<String>? = null,
    val monitor: List<String>? = null
)
