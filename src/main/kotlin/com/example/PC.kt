package com.example

import java.time.LocalDate
import java.util.Date

data class PC(
    override var id: Int,
    override val cabinetNumber: Int?,
    override val mgmtNumber: String,
    override val modelName: String,
    override val mfrDate: LocalDate,
    override val serialNumber: String,
    val cpu: String,
    val hdd: Short,
    val ram: Float,
    val OS: String,
    val inch: Float? = null,
    override val lastUser: String,
    override val importDate: LocalDate,
    override val status: Status,
    override val memo: String,
    val isLaptop: Boolean = false,
) : Equipment
