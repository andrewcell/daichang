package com.example

import kotlinx.serialization.Serializable

/**
 * Request data of Print label to parse. POST routing "/print"
 * @property pc Array of mgmtNumbers of PC.
 * @property laptop Array of mgmtNumbers of Laptop.
 * @property monitor Array of mgmtNumbers of Monitor.
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
@Serializable
data class PrintPayload(
    val pc: List<String>? = null,
    val laptop: List<String>? = null,
    val monitor: List<String>? = null
)
