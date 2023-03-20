package com.example

import com.example.database.*
import com.example.plugins.configureRouting
import com.typesafe.config.ConfigException.Null
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.io.FileOutputStream
import java.lang.NullPointerException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

/**
 * Start-up
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
fun main() {
    val port = System.getenv("daichang_port")?.toIntOrNull() ?: 8080 // Port for web server
    val host = System.getenv("daichang_host") ?: "127.0.0.1" // Host for web server
    val dbUrl = System.getenv("db_url") ?: "" // Full database connection url
    val dbUser = System.getenv("db_user") ?:"" // database user
    val dbPass = System.getenv("db_pass") ?: "" // databae password
    val backupLocation = System.getenv("backup_path") ?: "backup/" // Directory for backup file
    Database.connect(dbUrl, user = dbUser, password = dbPass)
    transaction {
        addLogger(Slf4jSqlDebugLogger)
        SchemaUtils.createMissingTablesAndColumns(EquipmentTable, PCTable, MonitorTable, ERPDataTable, LogTable, withLogs = true) // Construct database schemas
        println("Creating backup file before start... ")
        try {
            val spreadsheet = WorkSheetHandler.export() ?: throw NullPointerException()
            Files.createDirectories(Paths.get(backupLocation)) // create directory
            val path = backupLocation + "/" +LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + ".xlsx"
            val fos = FileOutputStream(path)
            fos.write(spreadsheet.toByteArray())
            fos.close()
            println("Backup created at: ${File(path).absolutePath}")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error caused when try to create backup file. Continuing start procedure anyway...")
        }
        embeddedServer(
            Netty,
            port = port,
            host = host,
            watchPaths = listOf("classes", "resources")
        ) {
      //      configureTemplating()
            configureRouting()
            install(CORS) {
                allowHost("localhost:5173")
                allowHost("192.168.56.101:5173")
                allowHost("daichang.local")
                allowHeader(HttpHeaders.ContentType)
            }
            install(ContentNegotiation) {
                json()
            }
        }.start(wait = true)
    }
}