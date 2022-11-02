package com.example.database

import com.example.database.MonitorTable.cable
import com.example.database.MonitorTable.equipmentId
import com.example.database.MonitorTable.inch
import com.example.database.MonitorTable.ratio
import com.example.database.MonitorTable.resolution
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * Table for store monitors
 * @property equipmentId Id from equipments table
 * @property inch Size of screen
 * @property ratio Screen Ratio e.g. 16:9 for 1920x1080
 * @property resolution Screen resolution
 * @property cable Available ports
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
object MonitorTable : IntIdTable("monitors") {
    val equipmentId = reference("equipmentId", EquipmentTable.id, onDelete = ReferenceOption.CASCADE)
    val inch = float("inch").default(24.0f)
    val ratio = varchar("ratio", 10)
    val resolution = varchar("resolution", 30).default("1920x1080")
    val cable = varchar("cable", 20).default("RGB/HDMI")
}