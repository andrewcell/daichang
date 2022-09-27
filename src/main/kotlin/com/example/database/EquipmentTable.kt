package com.example.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object EquipmentTable : Table() {
    val id = integer("id").uniqueIndex()
    val cabinetNumber = integer("cabinetNumber").nullable() // 보관순번, 미보관 시 null
    val mgmtNumber = varchar("mgmtNumber", 45) // 관리번호
    val mfrDate = datetime("mfrDate")
    val serialNumber = varchar("serialNumber", 45)
    val lastUser = varchar("lastUser", 45)
    val importDate = datetime("").clientDefault { LocalDateTime.now() }
    val status = varchar("status", 20)
    val memo = varchar("memo", 60)
    override val primaryKey = PrimaryKey(id)
}