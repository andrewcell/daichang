package com.example.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object MonitorTable : IntIdTable("monitors") {
    val equipmentId = reference("equipmentId", EquipmentTable.id, onDelete = ReferenceOption.CASCADE)
    val inch = float("inch").default(24.0f)
    val ratio = varchar("ratio", 10)
    val resolution = varchar("resolution", 30).default("1920x1080")
    val cable = varchar("cable", 20).default("RGB/HDMI")
}