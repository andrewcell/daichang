package com.example.models

import com.example.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

/**
 * PC class. Use for PC and Laptop.
 * @property id Primary key for database
 * @property cabinetNumber Cabinet number of imported PC
 * @property mgmtNumber Management number from ERP 3
 * @property modelName Commercial model name of PC
 * @property mfrDate Date of manufactured
 * @property serialNumber Serial number
 * @property cpu CPU name from System info
 * @property hdd Total capacity of Hard disk drives
 * @property ram Total installed RAM size
 * @property OS Operating System. Mostly Win 10 just few exceptions for Mac
 * @property inch Size of internal screen. If is null, handle as Desktop PC, or not null, handle as Laptop
 * @property lastUser Latest user of PC
 * @property importDate Date of imported
 * @property status Value of status
 * @property memo Any further description of PC
 * @property isLaptop Is it laptop
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
@Serializable
data class PC(
    override var id: Int,
    override val cabinetNumber: Int?,
    override val mgmtNumber: String,
    override val modelName: String,
    @Serializable(LocalDateSerializer::class) override val mfrDate: LocalDate,
    override val serialNumber: String,
    val cpu: String,
    val hdd: Int,
    val ram: Float,
    val OS: String,
    val inch: Float? = null,
    override val lastUser: String,
    @Serializable(LocalDateSerializer::class) override val importDate: LocalDate,
    override val status: Status,
    override val memo: String,
    val isLaptop: Boolean = false,
) : Equipment
