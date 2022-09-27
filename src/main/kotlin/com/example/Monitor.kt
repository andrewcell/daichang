package com.example

import java.util.*

data class Monitor(
    override val number: Int,
    override val mgmtNumber: String,
    override val modelName: String,
    override val mfrDate: Date,
    override val serialNumber: String,
    val ratio: String,
    val resolution: String,
    val inch: Float,
    val cable: String,
    override val lastUser: String,
    override val importDate: Date,
    override val status: Status,
    override val memo: String,
) : Equipment
