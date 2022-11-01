package com.example

import java.time.LocalDate

/**
 * Equipment interface for PC, Laptop, Monitor
 * Three equipment types have many common properties like management number, cabinet number, model name.
 * Using this interface, handle in one function, less function.
 *
 * @property id Primary key for database
 * @property cabinetNumber Cabinet number of imported Monitor
 * @property mgmtNumber Management number from ERP 3
 * @property modelName Commercial model name of Monitor
 * @property mfrDate Date of manufactured
 * @property serialNumber Serial number
 * @property lastUser Latest user of Monitor
 * @property importDate Date of imported
 * @property status Value of status
 * @property memo Any further description of monitor
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
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