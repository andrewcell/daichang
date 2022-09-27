package com.example.database

import org.jetbrains.exposed.sql.Table

object MonitorTable : Table() {
    val id = integer("id").uniqueIndex().autoIncrement()
    val equipmentId = integer("equipmentId").references(EquipmentTable.id)
    val inch = float("inch").default(24.0f)
    val ratio = varchar("ratio", 10)
    val resolution = varchar("resolution", 30).default("1920x1080")
    val cable = varchar("cable", 20).default("RGB/HDMI")
    override val primaryKey = PrimaryKey(PCTable.id)
}