package com.example

import java.util.*

interface Equipment {
    val number: Int
    val mgmtNumber: String
    val modelName: String
    val mfrDate: Date
    val serialNumber: String
    val lastUser: String
    val importDate: Date
    val status: Status
    val memo: String
}