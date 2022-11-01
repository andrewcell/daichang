package com.example

import com.example.database.*
import com.example.plugins.configureRouting
import com.example.plugins.configureTemplating
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Start-up
 */
fun main() {
    val port = System.getenv("daichang_port")?.toIntOrNull() ?: 8080 // Port for web server
    val host = System.getenv("daichang_host") ?: "127.0.0.1" // Host for web server
    val dbUrl = System.getenv("db_url") ?: "" // Full database connection url
    val dbUser = System.getenv("db_user") ?:"" // database user
    val dbPass = System.getenv("db_pass") ?: "" // databae password
    Database.connect(dbUrl, driver = "com.mysql.cj.jdbc.Driver", user = dbUser, password = dbPass)
    transaction {
        addLogger(Slf4jSqlDebugLogger)
        SchemaUtils.createMissingTablesAndColumns(EquipmentTable, PCTable, MonitorTable, ERPDataTable, LogTable, withLogs = true) // Construct database schemas
        embeddedServer(
            Netty,
            port = port,
            host = host,
            watchPaths = listOf("classes", "resources")
        ) {
            configureTemplating()
            configureRouting()
            install(ContentNegotiation) {
                json()
            }
        }.start(wait = true)
    }
}
