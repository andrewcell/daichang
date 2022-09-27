package com.example

import java.util.Date

data class PC(
    override val number: Int,
    override val mgmtNumber: String,
    override val modelName: String,
    override val mfrDate: Date,
    override val serialNumber: String,
    val cpu: String,
    val hdd: Short,
    val ram: Float,
    val OS: String,
    val inch: Float? = null,
    override val lastUser: String,
    override val importDate: Date,
    override val status: Status,
    override val memo: String,
    val isLaptop: Boolean = false,
) : Equipment
