package com.example

import java.time.LocalDate
import java.util.*

interface Equipment {
    var id: Int
    val cabinetNumber: Int?
    val mgmtNumber: String
    val modelName: String
    val mfrDate: LocalDate
    val serialNumber: String
    val lastUser: String
    val importDate: LocalDate
    val status: Status
    val memo: String
}