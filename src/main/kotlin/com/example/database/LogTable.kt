package com.example.database

import com.example.database.LogTable.accountId
import com.example.database.LogTable.action
import com.example.database.LogTable.datetime
import com.example.database.LogTable.ip
import com.example.database.LogTable.query
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * Table for data modification logging
 * @property accountId id from accounts table
 * @property action value of action type. See: logging/LogAction.kt
 * @property ip IP Address of client
 * @property datetime action performed date and time
 * @property query SQL query for data modification, or raw string value of request data
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
object LogTable : IntIdTable("logs") {
    val accountId = integer("accountId").default(-1)
    val action = varchar("action", 30)
    val ip = varchar("ip", 45)
    val datetime = datetime("datetime").clientDefault { LocalDateTime.now() }
    val query = text("query")
}