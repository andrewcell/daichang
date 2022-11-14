package com.example.models

import com.example.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

/**
 * Monitor class. Use for Monitor
 * @property id Primary key for database
 * @property cabinetNumber Cabinet number of imported Monitor
 * @property mgmtNumber Management number from ERP 3
 * @property modelName Commercial model name of Monitor
 * @property mfrDate Date of manufactured
 * @property serialNumber Serial number
 * @property inch Size of screen
 * @property ratio Screen Ratio e.g. 16:9 for 1920x1080
 * @property resolution Screen resolution
 * @property cable Available ports
 * @property lastUser Latest user of Monitor
 * @property importDate Date of imported
 * @property status Value of status
 * @property memo Any further description of monitor
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
@Serializable
data class Monitor(
    override var id: Int,
    override val cabinetNumber: Int?,
    override val mgmtNumber: String,
    override val modelName: String,
    @Serializable(LocalDateSerializer::class) override val mfrDate: LocalDate,
    override val serialNumber: String,
    val ratio: String,
    val resolution: String,
    val inch: Float,
    val cable: String,
    override val lastUser: String,
    @Serializable(LocalDateSerializer::class) override val importDate: LocalDate,
    override val status: Status,
    override val memo: String,
) : Equipment
