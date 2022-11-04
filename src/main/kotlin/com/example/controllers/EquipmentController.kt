package com.example.controllers

import com.example.AjaxResponse
import com.example.ResponseMessage
import com.example.database.DatabaseHandler
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.equipment() {
    route("/equipment") {
        val availableType = arrayOf("type", "id", "mgmtNumber")
        get("/{type}/{value}") {
            val type = call.parameters["type"]
            val value = call.parameters["value"]
            if (type !in availableType || value == null) {
                call.respond(AjaxResponse(false, ResponseMessage.BAD_REQUEST))
                return@get
            }
            val result = when (type) {
                "type" -> {
                    val index = value.toIntOrNull() ?: -1
                    DatabaseHandler.getList(index)
                }
                "id" -> {
                    val id = value.toIntOrNull()
                    if (id == null) {
                        null
                    } else {
                        val equipment = DatabaseHandler.getEquipmentById(id)
                        if (equipment != null) listOf(equipment) else null
                    }
                }
                "mgmtNumber" -> {
                    val mgmtNumber = value
                    val equipment = DatabaseHandler.getEquipmentByMgmtNumber(mgmtNumber)
                    if (equipment != null) listOf(equipment) else null
                }
                else -> null
            }
            if (result == null) {
                call.respond(AjaxResponse(false, ResponseMessage.EQUIPMENT_NOT_FOUND))
            } else {
                if (result.size == 1) {
                    call.respond(AjaxResponse(true, ResponseMessage.SUCCESS, Json.encodeToString(result.first())))
                } else call.respond(AjaxResponse(true, ResponseMessage.SUCCESS, Json.encodeToString(result)))
            }
        }
        /*get("/type/{index}") {
            val index = call.parameters["index"]?.toIntOrNull()
            val invalidIndex = AjaxResponse(false, ResponseMessage.INVALID_INDEX)
            if (index == null) {
                call.respond(invalidIndex)
                return@get
            }
            val list = DatabaseHandler.getList(index)
            if (list == null) {
                call.respond(AjaxResponse(false, ResponseMessage.INVALID_INDEX))
            } else {
                call.respond(AjaxResponse(true, ResponseMessage.SUCCESS, Json.encodeToString(list)))
            }
        }*/
    }
}