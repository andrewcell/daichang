package com.example.database

import com.example.models.*
import com.example.database.DatabaseHandler.isBusy
import com.example.database.DatabaseHandler.laptop
import com.example.database.DatabaseHandler.monitor
import com.example.database.DatabaseHandler.pc
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * All-in-one handler for database related jobs
 * @property pc List of pc objects (for cache)
 * @property laptop List of laptop objects (for cache)
 * @property monitor List of monitor objects (for cache)
 * @property isBusy Use for limit to do resource heavy job one at a time.
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
object DatabaseHandler {
    private val pc: MutableList<PC> = mutableListOf()
    private val laptop: MutableList<PC> = mutableListOf()
    private val monitor: MutableList<Monitor> = mutableListOf()
    var isBusy = false

    init {
        buildCache()
    }

    /**
     * Build cache(list) for make less connectivity to database server.
     */
    private fun buildCache() {
        transaction {
            (EquipmentTable innerJoin PCTable).select { EquipmentTable.id eq PCTable.equipmentId }.forEach { // select PC and laptops
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
            (EquipmentTable innerJoin MonitorTable).select { EquipmentTable.id eq MonitorTable.equipmentId }.forEach { // select monitors
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

    /**
     * clear and rebuild all caches
     * @return error message. If no exception threw, return null as success signal
     */
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

    /**
     * return list of equipments lists.
     * first pc, second laptop, third monitor
     * @return list of all lists. (0 = pc, 1 = laptop, 2 = monitor)
     */
    fun getAll(): Array<List<Equipment>> {
        return arrayOf(pc, laptop, monitor)
    }

    /**
     * return equipment list by index
     * @param index Index number of equipment type. 0=PC, 1=Laptop, 2=Monitor
     * @return List of equipments
     */
    fun getList(index: Int): List<Equipment> = when (index) {
        1 -> pc
        2 -> laptop
        3 -> monitor
        else -> emptyList() // return empty list if invalid index number passed.
    }.toList()

    /**
     * Insert monitor or update existing monitor row
     * @param monitor monitor to insert or update
     * @param equipId id from equipments table. Mandatory.
     * @param exists Is it exists (Is it update)
     */
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

    /**
     * Insert monitor or update existing monitor row
     * @param pc PC to insert or update
     * @param equipmentId id from equipments table. Mandatory.
     * @param exists Is it exists (Is it update)
     */
    private fun insertNewPC(pc: PC, equipmentId: Int, exists: Boolean) {
        transaction {
            fun insertToDB(iv: UpdateStatement? = null, k: InsertStatement<Number>? = null) {
                val i = iv ?: k
                if (i != null) {
                    // i[PCTable.id] = equipmentId
                    i[PCTable.equipmentId] = equipmentId
                    i[PCTable.cpu] = pc.cpu
                    i[PCTable.hdd] = pc.hdd
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

    /**
     * Insert or update equipment to database
     * @param index Index number of equipment type
     * @param equipment Equipment to insert or update
     * @return error message. return null if no problem found.
     */
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
        var message: String? = null
        try {
            transaction {
                val existsEquipId = EquipmentTable.slice(EquipmentTable.id).select { EquipmentTable.id eq equipment.id }.firstOrNull()?.get(EquipmentTable.id) //find it is exists
                val duplicated = if (existsEquipId == null) {
                    val found = getList(index).find { it.cabinetNumber == equipment.cabinetNumber || it.mgmtNumber == equipment.mgmtNumber }
                    found != null } else false
                if (duplicated) {
                    message = "사용 할 수 없는 순번이거나 관리번호입니다."
                    return@transaction
                }
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
                equipId = if (existsEquipId != null) { // if equipId is not null, it is exists, and need to update.
                    EquipmentTable.update({ EquipmentTable.id eq existsEquipId }) {
                        insertToDB(it)
                    }
                    updated = true
                    existsEquipId
                } else {
                    EquipmentTable.insert { // If equipId null, insert to equipments table and get id of new row.
                        insertToDB(k = it)
                    }.resultedValues?.firstOrNull()?.get(EquipmentTable.id) ?: -1
                }
                if (equipId < 0) throw NullPointerException("Equipment Id cannot be found. Possibility to failed insert or update equipment table.")
                if (equipment is PC) {
                    insertNewPC(equipment, equipId, existsEquipId != null)
                } else if (equipment is Monitor) {
                    insertNewMonitor(equipment, equipId, existsEquipId != null)
                }
                equipment.id = equipId
                if (updated) removeFromList(index, equipment.id)
                when (index) { // Add new equipment to cache list
                    1 -> pc.add(equipment as PC)
                    2 -> laptop.add(equipment as PC)
                    3 -> monitor.add(equipment as Monitor)
                    else -> null
                }
            }
        } catch (e: Exception) {
            println(e.printStackTrace())
            message = "데이터베이스 기록 중 오류 발생"
        }
        return message
    }

    /**
     * Delete equipment from database. Get multiple parameters for validate to prevent macro or something.
     * All parameters must be matched to equipment to delete.
     * @param index Index number of equipment type
     * @param mgmtNumber Management number of equipment to delete
     * @param lastUser Last user name of equipment to delete
     * @param modelName Model name of equipment to delete
     * @return error message. return null If no problem is found
     */
    fun deleteEquipment(index: Int, mgmtNumber: String, lastUser: String, modelName: String): String? {
        if (index !in 1..3) return "Invalid index" // Check index number
        try {
            transaction {
                EquipmentTable.deleteWhere {
                    //(EquipmentTable.id eq id) and
                    (EquipmentTable.mgmtNumber eq mgmtNumber) and
                            (EquipmentTable.lastUser eq lastUser) and
                            (EquipmentTable.modelName eq modelName) // 3 items must be matched to delete
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
            }.removeIf { it.mgmtNumber == mgmtNumber } // Remove equipment from cache list
        }
        return null
    }

    /**
     * Remove equipment from cache list
     * @param index Index number of equipment type
     * @param id Id of equipment to delete
     */
    private fun removeFromList(index: Int, id: Int) {
        when (index) {
            1 -> pc.removeIf { it.id == id }
            2 -> laptop.removeIf { it.id == id }
            3-> monitor.removeIf { it.id == id }
        }
    }

    /**
     * Get empty cabinet number to register new equipment
     * @param index Index number of equipment type
     * @return Empty cabinet number that can use
     */
    fun getEmptyCabinetNumber(index: Int): Int {
        val lst = when (index) {
            1 -> pc
            2 -> laptop
            3 -> monitor
            else -> emptyList()
        }
        val numbers = lst.sortedBy { it.cabinetNumber }.mapNotNull { // Sort to forEach, get list of cabinet numbers
            it.cabinetNumber
        }
        if (numbers.size <= 1) return (numbers.firstOrNull() ?: 0) + 1
        if (numbers.isNotEmpty()) {
            numbers.forEachIndexed { _index, it ->
                if (it + 1 != numbers[_index + 1]) { // check next number is not in sequential
                    return it + 1
                }
            }
        }
        return 1 // If list is empty or Something gone wrong, return 1
    }

    /**
     * Using list of ERPData objects, insert to ERPData table
     * @param list list of ERPData objects to import
     */
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

    /**
     * Get ERPData object from database by management number. Normally use for autofill form by management number
     * @param mgmtNumber Management number to find
     * @param requestIndex Index number of equipment type
     * @return ERPData object. Return null if mgmtNumber is invalid or not found
     */
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

    /**
     * Get CPU name by equipment's model name. Search fromd database
     * @param model model name of equipment
     * @return CPU name. Search by model name in database
     */
    fun getCPUByModelName(model: String): String? {
        var cpu: String? = null
        transaction {
            val found = EquipmentTable.leftJoin(PCTable).slice(PCTable.cpu).select { EquipmentTable.modelName like "$model%" }.limit(1).firstOrNull() // select by model name and get first one
                ?: return@transaction
            cpu = found[PCTable.cpu]
        }
        return cpu
    }

    /**
     * Wipe all ERPData table
     * @return error message. return null if no problem found.
     */
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