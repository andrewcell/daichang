package com.example.database

import com.example.database.PCTable.cpu
import com.example.database.PCTable.equipmentId
import com.example.database.PCTable.hdd
import com.example.database.PCTable.inch
import com.example.database.PCTable.os
import com.example.database.PCTable.ram
import com.example.database.PCTable.ssd
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * Table for store PC and laptops
 * @property equipmentId Id from equipments table
 * @property cpu CPU name from System info
 * @property ssd Total capacity of Solid slate drives
 * @property hdd Total capacity of Hard disk drives
 * @property ram Total installed RAM size
 * @property os Operating System. Mostly Win 10 just few exceptions for Mac
 * @property inch Size of internal screen. If is null, handle as Desktop PC, or not null, handle as Laptop
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
object PCTable : IntIdTable("pcs") {
    val equipmentId = reference("equipmentId", EquipmentTable.id, onDelete = ReferenceOption.CASCADE)
    val cpu = varchar("cpu", 50)
    val ssd = integer("ssd").default(256)
    val hdd = integer("hdd").default(500)
    val ram = integer("ram").default(8)
    val os = varchar("os", 30).default("Win 10")
    val inch = float("inch").nullable()
}