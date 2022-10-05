package com.example

import com.example.database.EquipmentTable
import com.example.database.PCTable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

//data class CPUModel
fun main(args: Array<String>) {
    val dbUrl = System.getenv("db_url") ?: ""
    val dbUser = System.getenv("db_user") ?:""
    val dbPass = System.getenv("db_pass") ?: ""
    Database.connect(dbUrl, driver = "com.mysql.cj.jdbc.Driver", user = dbUser, password = dbPass)
    transaction {
        val map = EquipmentTable.leftJoin(PCTable).slice(EquipmentTable.modelName, PCTable.cpu).selectAll().associate {
            it[EquipmentTable.modelName] to JsonPrimitive(it[PCTable.cpu])
        }
        val obj = JsonObject(map)
        println(Json.encodeToString(obj))
    }
}