package com.example

import kotlinx.serialization.Serializable

@Serializable
data class PrintPayLoad(
    val pc: List<Int>? = null,
    val laptop: List<Int>? = null,
    val monitor: List<Int>? = null
)
