package com.example.models

import kotlinx.serialization.Serializable

/**
 * Static Data from JSON file (See data.json in resources)
 * @property modelNameToCPU Map of A Pair of model Name and CPU. model Name as a key.
 * @property mfr Each array of manufacturer by the part of model name. Manufacturer as a key, contains array of model names.
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
@Serializable
data class StaticData(
    val modelNameToCPU: Map<String, String>,
    val mfr: Map<String, List<String>>
)