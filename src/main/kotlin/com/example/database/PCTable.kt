package com.example.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object PCTable : IntIdTable("pcs") {
    val equipmentId = reference("equipmentId", EquipmentTable.id, onDelete = ReferenceOption.CASCADE)
    val cpu = varchar("cpu", 50)
    val ssd = integer("ssd").default(256)
    val hdd = integer("hdd").default(500)
    val ram = integer("ram").default(8)
    val os = varchar("os", 30).default("Win 10")
    val inch = float("inch").nullable()
}