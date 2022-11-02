package com.example

import kotlinx.serialization.Serializable

/**
 * Response object to client that requested in JSON or by Ajax OR just need to handle as JSON
 * @property success Success or failed
 * @property message Server's message to client. if not succeed, error or failed, error message will be filled. or It could be null, if is success and any further request is not needed. or some values if its need to be handle in client side.
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
@Serializable
data class AjaxResponse (
    val success: Boolean,
    val message: String?
)