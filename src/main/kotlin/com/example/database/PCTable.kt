package com.example.database

import org.jetbrains.exposed.sql.Table

object PCTable : Table() {
    val id = integer("id").uniqueIndex().autoIncrement()
    val equipmentId = integer("equipmentId").references(EquipmentTable.id)
    val cpu = varchar("cpu", 50)
    val ssd = integer("ssd").default(256)
    val hdd = integer("hdd").default(500)
    val ram = integer("ram").default(8)
    val os = varchar("os", 30).default("Win 10")
    val inch = float("inch").nullable()

    override val primaryKey = PrimaryKey(id)
}