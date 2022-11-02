package com.example.database

import com.example.database.EquipmentTable.cabinetNumber
import com.example.database.EquipmentTable.id
import com.example.database.EquipmentTable.importDate
import com.example.database.EquipmentTable.lastUser
import com.example.database.EquipmentTable.memo
import com.example.database.EquipmentTable.mfrDate
import com.example.database.EquipmentTable.mgmtNumber
import com.example.database.EquipmentTable.modelName
import com.example.database.EquipmentTable.serialNumber
import com.example.database.EquipmentTable.status
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

/**
 * Equipment table for store duplicated data in PC, Laptop, Monitor
 * Three equipment types have many common properties like management number, cabinet number, model name.
 *
 * @property id Primary key for database
 * @property cabinetNumber Cabinet number of imported Monitor
 * @property mgmtNumber Management number from ERP 3
 * @property modelName Commercial model name of Monitor
 * @property mfrDate Date of manufactured
 * @property serialNumber Serial number
 * @property lastUser Latest user of Monitor
 * @property importDate Date of imported
 * @property status Value of status
 * @property memo Any further description of monitor
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
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