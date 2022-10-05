package com.example

import kotlinx.serialization.Serializable

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
