package com.example

import com.example.database.EquipmentTable
import com.example.database.MonitorTable
import com.example.database.PCTable
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    val port = System.getenv("daichang.port")?.toIntOrNull() ?: 8080
    val host = System.getenv("daichang.host") ?: "127.0.0.1"
    Database.connect("jdbc:mysql://192.168.56.101:3306/test", driver = "com.mysql.cj.jdbc.Driver", user = "test", password = "test1234")
    transaction {
        addLogger(Slf4jSqlDebugLogger)
        SchemaUtils.createMissingTablesAndColumns(EquipmentTable, PCTable, MonitorTable, withLogs = true)
        embeddedServer(
            Netty,
            port = port,
            host = host,
            watchPaths = listOf("classes", "/resources/static/", "resources")
        ) {
            configureTemplating()
            configureRouting()
        }.start(wait = true)
    }
}
