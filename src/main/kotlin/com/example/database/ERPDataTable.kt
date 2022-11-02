package com.example.database

import com.example.database.ERPDataTable.index
import com.example.database.ERPDataTable.lastUser
import com.example.database.ERPDataTable.mfrDate
import com.example.database.ERPDataTable.mgmtNumber
import com.example.database.ERPDataTable.modelName
import com.example.database.ERPDataTable.serialNumber
import com.example.database.ERPDataTable.var1
import com.example.database.ERPDataTable.var2
import com.example.database.ERPDataTable.var3
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

/**
 * Table to store ERP 3 data
 * @property index Index number of equipment type
 * @property mgmtNumber Management number of equipment
 * @property modelName Model name of equipment
 * @property serialNumber Serial number of equipment
 * @property mfrDate Date of manufactured
 * @property var1 CPU (pc, laptop), Cable (monitor)
 * @property var2 RAM (pc, laptop), Inch (monitor)
 * @property var3 HDD (pc, laptop), Power (monitor)
 * @property lastUser Last user of equipment
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
object ERPDataTable : IntIdTable("erpdata") {
    val index = integer("index")
    val mgmtNumber = varchar("mgmtNumber", 45)
    val modelName = varchar("modelName", 60)
    val serialNumber = varchar("serialNumber", 45)
    val mfrDate = date("mfrDate")
    val var1 = varchar("var1", 45)
    val var2 = varchar("var2", 45)
    val var3 = varchar("var3", 45)
    val lastUser = varchar("lastUser", 30)
}