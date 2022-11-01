package com.example

import kotlinx.serialization.Serializable

/**
 * ERP data class. Use for erp data imported from spreadsheet file
 * @property index 1 = pc, 2 = laptop, 3 = monitor
 * @property mgmtNumber Management number from ERP 3
 * @property modelName Model name
 * @property serialNumber Equipment's serial number
 * @property mfrDate Manufactured date
 * @property var1 CPU (pc, laptop), Cable (monitor)
 * @property var2 RAM (pc, laptop), Inch (monitor)
 * @property var3 HDD (pc, laptop), Power (monitor)
 * @property var4 Inch (laptop), Resolution (monitor)
 * @property var5 Ratio (monitor)
 * @property lastUser Last user or store of equipment
 * @property id primary key id in database. Can be null if not written to database yet.
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
@Serializable
data class ERPData(
    val index: Int,
    val mgmtNumber: String,
    val modelName: String,
    val serialNumber: String,
    val mfrDate: String,
    var var1: String,
    var var2: String,
    var var3: String,
    var var4: String = "",
    var var5: String = "",
    val lastUser: String,
    val id: Int? = null
)
