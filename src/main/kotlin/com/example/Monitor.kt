package com.example

import java.time.LocalDate
import java.util.*

data class Monitor(
    override var id: Int,
    override val cabinetNumber: Int?,
    override val mgmtNumber: String,
    override val modelName: String,
    override val mfrDate: LocalDate,
    override val serialNumber: String,
    val ratio: String,
    val resolution: String,
    val inch: Float,
    val cable: String,
    override val lastUser: String,
    override val importDate: LocalDate,
    override val status: Status,
    override val memo: String,
) : Equipment
