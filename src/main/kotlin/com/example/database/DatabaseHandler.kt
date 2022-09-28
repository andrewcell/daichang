package com.example.database

import com.example.Equipment
import com.example.Monitor
import com.example.PC
import com.example.Status
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.Statement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseHandler {
    private val pc: MutableList<PC>
    private val laptop: MutableList<PC>
    private val monitor: MutableList<Monitor>

    init {
        val pcList = mutableListOf<PC>()
        val laptopList = mutableListOf<PC>()
        var monitorList = emptyList<Monitor>()

        transaction {
            EquipmentTable.join(PCTable, JoinType.INNER).selectAll().forEach {
                val inch = it[PCTable.inch]
                val pc = PC(
                    id = it[EquipmentTable.id],
                    cabinetNumber = it[EquipmentTable.cabinetNumber] ?: -1,
                    mgmtNumber = it[EquipmentTable.mgmtNumber],
                    modelName = it[EquipmentTable.modelName],
                    mfrDate = it[EquipmentTable.mfrDate],
                    serialNumber = it[EquipmentTable.serialNumber],
                    cpu = it[PCTable.cpu],
                    hdd = it[PCTable.hdd].toShort(),
                    ram = it[PCTable.ram].toFloat(),
                    OS = it[PCTable.os],
                    inch = inch,
                    lastUser = it[EquipmentTable.lastUser],
                    importDate = it[EquipmentTable.importDate],
                    status = Status.findByValue(it[EquipmentTable.status]) ?: Status.NOT_AVAILABLE,
                    memo = it[EquipmentTable.memo],
                    isLaptop = inch != null
                )
                if (pc.isLaptop) {
                    laptopList.add(pc)
                } else pcList.add(pc)
            }
            monitorList = EquipmentTable.innerJoin(MonitorTable).selectAll().groupBy(EquipmentTable.id).map {
                Monitor(
                    id = it[EquipmentTable.id],
                    cabinetNumber = it[EquipmentTable.cabinetNumber] ?: -1,
                    mgmtNumber = it[EquipmentTable.mgmtNumber],
                    modelName = it[EquipmentTable.modelName],
                    mfrDate = it[EquipmentTable.mfrDate],
                    serialNumber = it[EquipmentTable.serialNumber],
                    ratio = it[MonitorTable.ratio],
                    resolution = it[MonitorTable.resolution],
                    inch = it[MonitorTable.inch],
                    cable = it[MonitorTable.cable],
                    lastUser = it[EquipmentTable.lastUser],
                    importDate = it[EquipmentTable.importDate],
                    status = Status.findByValue(it[EquipmentTable.status]) ?: Status.NOT_AVAILABLE,
                    memo = it[EquipmentTable.memo],
                )
            }
        }
        pc = pcList
        laptop = laptopList
        monitor = monitorList.toMutableList()
    }

    fun getAll(): Array<List<Equipment>> {
        return arrayOf(pc, laptop, monitor)
    }

    fun getList(index: Int): List<Equipment> = when (index) {
        1 -> pc
        2 -> laptop
        3 -> monitor
        else -> emptyList()
    }.toList()

    private fun teraByteToGigaByte(raw: String): Int {
        //stringCellValue.dropLast(2).trim().toShortOrNull() ?: 0,
        //val prefix = raw.takeLast(2)
        val value = raw.dropLast(2).trim().toIntOrNull() ?: 0
        return if (raw.endsWith("TB")) {
            return value * 1024
        } else value
    }

    private fun insertNewMonitor(monitor: Monitor, equipId: Int, exists: Boolean) {
        transaction {
            fun insertToDB(iv: UpdateStatement? = null, k: InsertStatement<Number>? = null) {
                val i = iv ?: k
                if (i != null) {
                    i[MonitorTable.id] = equipId
                    i[MonitorTable.equipmentId] = equipId
                    i[MonitorTable.ratio] = monitor.ratio
                    i[MonitorTable.resolution] = monitor.resolution
                    i[MonitorTable.inch] = monitor.inch
                    i[MonitorTable.cable] = monitor.cable
                }
            }
            if (exists) {
                MonitorTable.update({ MonitorTable.id eq monitor.id }) {
                    insertToDB(iv = it)
                }
            } else {
                MonitorTable.insert {
                    insertToDB(k = it)
                }
            }
        }
    }

    private fun insertNewPC(pc: PC, equipmentId: Int, exists: Boolean) {
        transaction {
            fun insertToDB(iv: UpdateStatement? = null, k: InsertStatement<Number>? = null) {
                val i = iv ?: k
                if (i != null) {
                    i[PCTable.id] = equipmentId
                    i[PCTable.equipmentId] = equipmentId
                    //it[PCTable.equipmentId] = if (equipment.id == -1) equipmentId else equipment.id
                    i[PCTable.cpu] = pc.cpu
                    i[PCTable.hdd] = pc.hdd.toInt()
                    i[PCTable.ram] = pc.ram.toInt()
                    i[PCTable.os] = pc.OS
                    i[PCTable.inch] = pc.inch
                }
            }
            if (exists) {
                PCTable.update({ PCTable.id eq pc.id}) {
                    insertToDB(iv = it)
                }
            } else {
                PCTable.insert {
                    insertToDB(k = it)
                }
            }
        }
    }

    fun insertNewEquipment(index: Int, equipment: Equipment, forceInsert: Boolean = false) {
        when (index) {
            1, 2 -> {
                equipment as PC
            }
            3 -> {
                equipment as Monitor
            }
            else -> return
        }
        var equipId = -1
        try {
            transaction {
                val existsEquipId = if (equipment.id != -1) {
                    EquipmentTable.slice(EquipmentTable.id).select { EquipmentTable.id eq equipment.id }.firstOrNull()?.get(EquipmentTable.id)
                } else null
                fun insertToDB(iv: UpdateStatement? = null, k: InsertStatement<Number>? = null) {
                    val i = iv ?: k
                    if (i != null) {
                        i[EquipmentTable.cabinetNumber] = equipment.cabinetNumber
                        i[EquipmentTable.mgmtNumber] = equipment.mgmtNumber
                        i[EquipmentTable.mfrDate] = equipment.mfrDate
                        i[EquipmentTable.serialNumber] = equipment.serialNumber
                        i[EquipmentTable.modelName] = equipment.modelName
                        i[EquipmentTable.lastUser] = equipment.lastUser
                        i[EquipmentTable.importDate] = equipment.importDate
                        i[EquipmentTable.status] = equipment.status.value
                        i[EquipmentTable.memo] = equipment.memo
                    }
                }
                equipId = if (existsEquipId != null) {
                    EquipmentTable.update({ EquipmentTable.id eq existsEquipId }) {
                        insertToDB(it)
                    }
                    existsEquipId
                } else {
                    EquipmentTable.insert {
                        insertToDB(k = it)
                    }.resultedValues?.firstOrNull()?.get(EquipmentTable.id) ?: -1
                }
                if (equipId < 0) throw NullPointerException("Equipment Id cannot be found. Possibility to failed insert or update equipment table.")
                if (equipment is PC) {
                    insertNewPC(equipment, equipId, existsEquipId != null)
                } else if (equipment is Monitor) {
                    insertNewMonitor(equipment, equipId, existsEquipId != null)
                }
            }
                /*
                val equipmentId = EquipmentTable.upsert({ EquipmentTable.id eq equipment.id }) {
                        /*if (equipment.id != -1) {
                            it[id] = equipment.id
                        }*/
                        it[cabinetNumber] = equipment.cabinetNumber
                        it[mgmtNumber] = equipment.mgmtNumber
                        it[mfrDate] = equipment.mfrDate
                        it[serialNumber] = equipment.serialNumber
                        it[modelName] = equipment.modelName
                        it[lastUser] = equipment.lastUser
                        it[importDate] = equipment.importDate
                        it[status] = equipment.status.value
                        it[memo] = equipment.memo
                }.resultedValues?.get(0)?.get(EquipmentTable.id) ?: throw (NullPointerException("Id not found. Update could be failed already."))
                val insertedId = when (equipment) {
                    is PC -> {
                        PCTable.upsert({ PCTable.id eq equipmentId }) {
                            it[id] = equipmentId
                            it[PCTable.equipmentId] = equipmentId
                            //it[PCTable.equipmentId] = if (equipment.id == -1) equipmentId else equipment.id
                            it[cpu] = equipment.cpu
                            it[hdd] = equipment.hdd.toInt()
                            it[ram] = equipment.ram.toInt()
                            it[os] = equipment.OS
                            it[inch] = equipment.inch
                        }.resultedValues?.firstOrNull()?.get(PCTable.id)?.value ?: throw NullPointerException("Id not found. Update could be failed already.")
                    }

                    is Monitor -> {
                        MonitorTable.upsert({ MonitorTable.id eq equipmentId }) {

                        }.resultedValues?.firstOrNull()?.get(MonitorTable.id)?.value ?: throw NullPointerException("Id not found. Update could be failed already.")
                    }

                    else -> {
                        null
                    }
                }
                nid = insertedId ?: throw NullPointerException("Id not found. Update could be failed already.")
            }*/
        } catch (e: Exception) {
            println(e.printStackTrace())
        } finally {
            equipment.id = equipId
            when (index) {
                1 -> pc.add(equipment as PC)
                2 -> laptop.add(equipment as PC)
                3 -> monitor.add(equipment as Monitor)
            }
        }
    }

    fun deleteEquipment(index: Int, id: Int, mgmtNumber: String, lastUser: String, modelName: String) {
        if (id == -1 || index == -1) return
        try {
            transaction {
                EquipmentTable.deleteWhere {
                    (EquipmentTable.id eq id) and
                    (EquipmentTable.mgmtNumber eq mgmtNumber) and
                    (EquipmentTable.lastUser eq lastUser) and
                    (EquipmentTable.modelName eq modelName)
                }
            }
        } catch (e: Exception) {
            println(e.message)
        } finally {
            when (index) {
                1 -> pc.removeIf { it.id == id }
                2 -> laptop.removeIf { it.id == id }
                3 -> monitor.removeIf { it.id == id }
            }
        }
    }

    fun getEmptyCabinetNumber(index: Int): Int {
        val lst = when (index) {
            1 -> pc
            2 -> laptop
            3 -> monitor
            else -> emptyList()
        }
        val numbers = lst.mapNotNull {
            it.cabinetNumber
        }
        if (numbers.isNotEmpty()) {
            for (i in 1..numbers.last() + 1) {
                if (i !in numbers) return i
            }
        }
        return 1
    }
    /*
        // The below code is just a copy-paste that should actually be in the lib
        class BatchInsertUpdateOnDuplicate(table: Table, val onDupUpdate: List<Column<*>>) : BatchInsertStatement(table, false) {
            override fun prepareSQL(transaction: Transaction): String {
                val onUpdateSQL = if (onDupUpdate.isNotEmpty()) {
                    " ON DUPLICATE KEY UPDATE " + onDupUpdate.joinToString { "${transaction.identity(it)}=VALUES(${transaction.identity(it)})" }
                } else ""
                return super.prepareSQL(transaction) + onUpdateSQL
            }
        }

        fun <T : Table, E> T.batchInsertOnDuplicateKeyUpdate(data: List<E>, onDupUpdateColumns: List<Column<*>>, body: T.(BatchInsertUpdateOnDuplicate, E) -> Unit) {
            data.
            takeIf { it.isNotEmpty() }?.
            let {
                val insert = BatchInsertUpdateOnDuplicate(this, onDupUpdateColumns)
                data.forEach {
                    insert.addBatch()
                    body(insert, it)
                }
                TransactionManager.current().exec(insert)
            }
        }*/

}