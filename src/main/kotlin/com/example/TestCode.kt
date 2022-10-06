package com.example

import com.example.database.MonitorTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

//data class CPUModel
fun main(args: Array<String>) {
    val dbUrl = System.getenv("db_url") ?: ""
    val dbUser = System.getenv("db_user") ?:""
    val dbPass = System.getenv("db_pass") ?: ""
    Database.connect(dbUrl, driver = "com.mysql.cj.jdbc.Driver", user = dbUser, password = dbPass)
    transaction {
        addLogger(StdOutSqlLogger)
        //var chars = emptyArray<String>()
        MonitorTable.selectAll().forEach {
            val resolution = it[MonitorTable.resolution]
            val chars = resolution.split("x").toTypedArray()
            MonitorTable.update({ MonitorTable.id eq  it[MonitorTable.id]}) { it1 ->
                it1[MonitorTable.resolution] = "${chars[0].trim()}x${chars[1].trim()}"
            }
        }

        /*val map = EquipmentTable.leftJoin(PCTable).slice(EquipmentTable.modelName, PCTable.cpu).selectAll().associate {
            it[EquipmentTable.modelName] to JsonPrimitive(it[PCTable.cpu])
        }
        val obj = JsonObject(map)
        println(Json.encodeToString(obj))*
         */
    }
}