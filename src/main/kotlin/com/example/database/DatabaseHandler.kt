package com.example.database

import com.example.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DatabaseHandler {
    private val pc: MutableList<PC> = mutableListOf()
    private val laptop: MutableList<PC> = mutableListOf()
    private val monitor: MutableList<Monitor> = mutableListOf()
    var isBusy = false
    init {
        buildCache()
    }
    private fun buildCache() {
        transaction {
            (EquipmentTable innerJoin PCTable).select { EquipmentTable.id eq PCTable.equipmentId }.forEach {
                val inch = it[PCTable.inch]
                val pcEntry = PC(
                    id = it[EquipmentTable.id],
                    cabinetNumber = it[EquipmentTable.cabinetNumber] ?: -1,
                    mgmtNumber = it[EquipmentTable.mgmtNumber],
                    modelName = it[EquipmentTable.modelName],
                    mfrDate = it[EquipmentTable.mfrDate],
                    serialNumber = it[EquipmentTable.serialNumber],
                    cpu = it[PCTable.cpu],
                    hdd = it[PCTable.hdd],
                    ram = it[PCTable.ram].toFloat(),
                    OS = it[PCTable.os],
                    inch = inch,
                    lastUser = it[EquipmentTable.lastUser],
                    importDate = it[EquipmentTable.importDate],
                    status = Status.findByValue(it[EquipmentTable.status]) ?: Status.NOT_AVAILABLE,
                    memo = it[EquipmentTable.memo],
                    isLaptop = inch != null
                )
                if (pcEntry.isLaptop) {
                    laptop.add(pcEntry)
                } else pc.add(pcEntry)
            }
            (EquipmentTable innerJoin MonitorTable).select { EquipmentTable.id eq MonitorTable.equipmentId }.forEach {
                monitor.add(Monitor(
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
                ))
            }
        }
    }

    fun rebuild(): String? {
        try {
            pc.clear()
            laptop.clear()
            monitor.clear()
            buildCache()
        } catch (e: Exception) {
            return e.message
        }
        return null
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

    private fun insertNewMonitor(monitor: Monitor, equipId: Int, exists: Boolean) {
        transaction {
            fun insertToDB(iv: UpdateStatement? = null, k: InsertStatement<Number>? = null) {
                val i = iv ?: k
                if (i != null) {
                  //  i[MonitorTable.id] = equipId
                    i[MonitorTable.equipmentId] = equipId
                    i[MonitorTable.ratio] = monitor.ratio
                    i[MonitorTable.resolution] = monitor.resolution
                    i[MonitorTable.inch] = monitor.inch
                    i[MonitorTable.cable] = monitor.cable
                }
            }
            if (exists) {
                MonitorTable.update({ MonitorTable.equipmentId eq monitor.id }) {
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
                   // i[PCTable.id] = equipmentId
                    i[PCTable.equipmentId] = equipmentId
                    i[PCTable.cpu] = pc.cpu
                    i[PCTable.hdd] = pc.hdd.toInt()
                    i[PCTable.ram] = pc.ram.toInt()
                    i[PCTable.os] = pc.OS
                    i[PCTable.inch] = pc.inch
                }
            }
            if (exists) {
                PCTable.update({ PCTable.equipmentId eq pc.id}) {
                    insertToDB(iv = it)
                }
            } else {
                PCTable.insert {
                    insertToDB(k = it)
                }
            }
        }
    }

    fun insertNewEquipment(index: Int, equipment: Equipment): String? {
        when (index) {
            1, 2 -> {
                equipment as PC
            }
            3 -> {
                equipment as Monitor
            }
            else -> return "Internal server error"
        }
        var equipId = -1
        var updated = false
        try {
            transaction {
                /*val existsEquipId = if (equipment.id != -1) {
                    EquipmentTable.slice(EquipmentTable.id).select { EquipmentTable.id eq equipment.id }.firstOrNull()?.get(EquipmentTable.id)
                } else null*/ // Disabled for prevent duplicate insert from spreadsheet import. Without it, Very poor performance.
                // Check every equipment its exists. Very poor performance. Change to above if it feels too slow.
                val existsEquipId = EquipmentTable.slice(EquipmentTable.id).select { EquipmentTable.id eq equipment.id }.firstOrNull()?.get(EquipmentTable.id)
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
                    updated = true
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
        } catch (e: Exception) {
            println(e.printStackTrace())
            return "데이터베이스 기록 중 오류 발생"
        } finally {
            equipment.id = equipId
            if (updated) removeFromList(index, equipment.id)
            when (index) {
                1 -> pc.add(equipment as PC)
                2 -> laptop.add(equipment as PC)
                3 -> monitor.add(equipment as Monitor)
            }
        }
        return null
    }

    fun deleteEquipment(index: Int, mgmtNumber: String, lastUser: String, modelName: String): String? {
        if (index == -1) return "Invalid index"
        try {
            transaction {
                EquipmentTable.deleteWhere {
                    //(EquipmentTable.id eq id) and
                    (EquipmentTable.mgmtNumber eq mgmtNumber) and
                    (EquipmentTable.lastUser eq lastUser) and
                    (EquipmentTable.modelName eq modelName)
                }
            }
        } catch (e: Exception) {
            println(e.message)
            return "데이터베이스 기록 중 오류 발생"
        } finally {
            when (index) {
                1 -> pc
                2 -> laptop
                3 -> monitor
                else -> mutableListOf()
            }.removeIf { it.mgmtNumber == mgmtNumber }
        }
        return null
    }

    private fun removeFromList(index: Int, id: Int) {
        when (index) {
            1 -> pc.removeIf { it.id == id }
            2 -> laptop.removeIf { it.id == id }
            3-> monitor.removeIf { it.id == id }
        }
    }

    fun getEmptyCabinetNumber(index: Int): Int {
        val lst = when (index) {
            1 -> pc
            2 -> laptop
            3 -> monitor
            else -> emptyList()
        }
        val numbers = lst.sortedBy { it.cabinetNumber }.mapNotNull {
            it.cabinetNumber
        }
        if (numbers.isNotEmpty()) {
            numbers.forEachIndexed { _index, it ->
                if (it + 1 != numbers[_index + 1]) {
                    return it + 1
                }
            }
        }
        return 1
    }

    fun importERP(list: List<ERPData>) {
        transaction {
            //ERPDataTable.deleteAll()
            ERPDataTable.batchInsert(list) { eq ->
                this[ERPDataTable.index] = eq.index
                this[ERPDataTable.mgmtNumber] = eq.mgmtNumber
                this[ERPDataTable.modelName] = eq.modelName
                this[ERPDataTable.serialNumber] = eq.serialNumber
                this[ERPDataTable.var1] = eq.var1
                this[ERPDataTable.var2] = eq.var2
                this[ERPDataTable.var3] = eq.var3
                this[ERPDataTable.mfrDate] = LocalDate.parse(eq.mfrDate, DateTimeFormatter.ISO_LOCAL_DATE)
                this[ERPDataTable.lastUser] = eq.lastUser
            }
        }
    }

    fun getERPDataByMgmtNumber(mgmtNumber: String, requestIndex: Int): ERPData? {
        var erpData: ERPData? = null
        transaction {
            val found = ERPDataTable.select { (ERPDataTable.mgmtNumber eq mgmtNumber) and (ERPDataTable.index eq requestIndex) }.limit(1).firstOrNull() ?: return@transaction
            erpData = ERPData(
                id = found[ERPDataTable.id].value,
                index = found[ERPDataTable.index],
                mgmtNumber = found[ERPDataTable.mgmtNumber],
                modelName = found[ERPDataTable.modelName],
                serialNumber = found[ERPDataTable.serialNumber],
                mfrDate = found[ERPDataTable.mfrDate].format(DateTimeFormatter.ISO_LOCAL_DATE),
                var1 = found[ERPDataTable.var1],
                var2 = found[ERPDataTable.var2],
                var3 = found[ERPDataTable.var3],
                lastUser = found[ERPDataTable.lastUser]
            )
        }
        return erpData
    }

    fun getCPUByModelName(model: String): String? {
        var cpu: String? = null
        transaction {
            val found = EquipmentTable.leftJoin(PCTable).slice(PCTable.cpu).select { EquipmentTable.modelName like "$model%" }.limit(1).firstOrNull()
                    ?: return@transaction
            cpu = found[PCTable.cpu]
        }
        return cpu
    }

    fun cleanERPData(): String? {
        var message: String? = null
        try {
            transaction {
                ERPDataTable.deleteAll()
            }
        } catch (e: Exception) {
            message = e.message
        }
        return message
    }
}