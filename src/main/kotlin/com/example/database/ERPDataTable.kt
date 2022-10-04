package com.example.database

import org.jetbrains.exposed.dao.id.IntIdTable

object ERPDataTable : IntIdTable("erpdata") {
    val index = integer("index")
    val mgmtNumber = varchar("mgmtNumber", 45)
    val modelName = varchar("modelName", 60)
    val serialNumber = varchar("serialNumber", 45)
    val mfrDate = varchar("mfrDate", 45)
    val var1 = varchar("var1", 45)
    val var2 = varchar("var2", 45)
    val var3 = varchar("var3", 45)
}