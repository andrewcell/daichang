package com.example

/**
 * Commonly used response messages.
 */
enum class ResponseMessage {
    /**
     * If request parameter, form, uri is formed unacceptable. (Unusual request like modified value by hand, 3rd-tools. No need to know to the client)
     */
    BAD_REQUEST,

    /**
     * If index number of equipment type is invalid
     */
    INVALID_INDEX,

    /**
     * If requested user don't have permission to do that. (Especially admin console, whatever)
     */
    PERMISSION_DENIED,

    /**
     * Simply not logged in.
     */
    UNAUTHORIZED,

    /**
     * Error cannot be shown to public or programmatically wrong
     */
    INTERNAL_ERROR,

    /**
     * Everything processed properly.
     */
    SUCCESS,

    // Equipment Controller
    /**
     * Equipment try to find is not found
     */
    EQUIPMENT_NOT_FOUND,

    // Account Controller
    /**
     * Requested registration of a new account, but requested user's IP already in database.
     */
    ALREADY_REGISTERED_IP,

    /**
     * E-mail address already exists
     */
    ALREADY_REGISTERED_USERNAME,

    /**
     * E-mail address and password combination cannot be found in database.
     */
    INCORRECT_USERNAME_PASSWORD,
}