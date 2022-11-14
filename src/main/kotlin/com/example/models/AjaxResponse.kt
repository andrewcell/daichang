package com.example.models

import com.example.EncryptSerializer
import kotlinx.serialization.Serializable

/**
 * Response object to client that requested in JSON or by Ajax OR just need to handle as JSON
 * @property success Success or failed
 * @property message Server's message to client
 * @property data Payload send to client. If not success and contains error message, error message will be filled
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
@Serializable
data class AjaxResponse (
    val success: Boolean,
    val message: ResponseMessage,
    @Serializable(EncryptSerializer::class) val data: String? = null
)