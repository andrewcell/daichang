package com.example.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

object EquipmentTable : Table("equipments") {
    val id = integer("id").autoIncrement()
    val cabinetNumber = integer("cabinetNumber").nullable() // 보관순번, 미보관 시 null
    val mgmtNumber = varchar("mgmtNumber", 45) // 관리번호
    val mfrDate = date("mfrDate")
    val serialNumber = varchar("serialNumber", 45)
    val modelName = varchar("modelName", 45)
    val lastUser = varchar("lastUser", 45)
    val importDate = date("importDate").clientDefault { LocalDate.now() }
    val status = varchar("status", 20)
    val memo = varchar("memo", 60)
    override val primaryKey = PrimaryKey(id)
}