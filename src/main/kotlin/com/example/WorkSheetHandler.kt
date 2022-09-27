package com.example

import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.ss.SpreadsheetVersion
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.CellCopyPolicy
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.util.AreaReference
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

object WorkSheetHandler {
    private val workbook = XSSFWorkbook(FileInputStream(Constants.worksheetPath))
    private val pc = readPC().toMutableList()
    private val laptop = readPC(true).toMutableList()
    private val monitor = readMonitor().toMutableList()

    fun getAll(): Array<List<Equipment>> {
        return arrayOf(pc.toList(), laptop.toList(), monitor.toList())
    }

    fun getList(index: Int): List<Equipment> = when (index) {
        1 -> pc
        2 -> laptop
        3 -> monitor
        else -> emptyList()
    }.toList()

    private fun readPC(laptop: Boolean = false): List<PC> {
        return buildList {
            val sheet = workbook.getSheet(if (laptop) "노트북" else "PC")
            sheet.forEach { row ->
                if (row.rowNum == 0) return@forEach
                try {
                    val inch = if (laptop) row.getCell(9).numericCellValue.toFloat() else null
                    val pc = PC(
                        number = row.getCell(0).numericCellValue.toInt(),
                        mgmtNumber = row.getCell(1).stringCellValue,
                        modelName = row.getCell(2).stringCellValue,
                        mfrDate = row.getCell(3).dateCellValue,
                        serialNumber = row.getCell(4).stringCellValue,
                        cpu = row.getCell(5).stringCellValue,
                        hdd = teraByteToGigaByte(row.getCell(6).stringCellValue).toShort(),
                        ram = row.getCell(7).stringCellValue.replace("GB", "").trim().toFloatOrNull() ?: 0.0f,
                        OS = row.getCell(8).stringCellValue,
                        lastUser = row.getCell(if (laptop) 10 else 9).stringCellValue,
                        importDate = row.getCell(if (laptop) 11 else 10).dateCellValue,
                        status = Status.findByValue(row.getCell(if (laptop) 12 else 11).stringCellValue) ?: Status.TO_BE_DISPOSE,
                        memo = row.getCell(if (laptop) 15 else 14).stringCellValue,
                        inch = inch,
                        isLaptop = laptop,
                    )
                    add(pc)
                } catch (_: Exception) {
                    return@forEach
                }
            }
        }
    }

    private fun readMonitor(): List<Monitor> {
        return buildList {
            val sheet = workbook.getSheet("모니터")
            sheet.forEach { row ->
                if (row.rowNum == 0) return@forEach
                try {
                    val monitor = Monitor(
                        number = row.getCell(0).numericCellValue.toInt(),
                        mgmtNumber = row.getCell(1).stringCellValue,
                        modelName = row.getCell(2).stringCellValue,
                        mfrDate = row.getCell(3).dateCellValue,
                        serialNumber = row.getCell(4).stringCellValue,
                        ratio = row.getCell(5).stringCellValue,
                        resolution = row.getCell(6).stringCellValue,
                        inch = row.getCell(7).numericCellValue.toFloat(),
                        cable = row.getCell(8).stringCellValue,
                        lastUser = row.getCell(9).stringCellValue,
                        importDate = row.getCell(10).dateCellValue,
                        status = Status.findByValue(row.getCell(11).stringCellValue) ?: Status.TO_BE_DISPOSE,
                        memo = row.getCell(14).stringCellValue,
                    )
                    add(monitor)
                } catch (_: Exception) {
                    return@forEach
                }
            }
        }
    }

    fun close() {
        workbook.close()
    }

    private fun teraByteToGigaByte(raw: String): Int {
        //stringCellValue.dropLast(2).trim().toShortOrNull() ?: 0,
        //val prefix = raw.takeLast(2)
        val value = raw.dropLast(2).trim().toIntOrNull() ?: 0
        return if (raw.endsWith("TB")) {
            return value * 1024
        } else value
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
        try {
            val sheetName = getSheetName(index)
            val sheet = workbook.getSheet(sheetName)
            val table = sheet.tables.firstOrNull() ?: return
            val lastRow = table.endCellReference
            val lastRowNum = lastRow.row
            val exists = sheet.find { it.rowNum != 0 && it.getCell(0)?.numericCellValue?.toInt() == equipment.number }
            val update = exists != null
            val newRow = if (!update || forceInsert) {
                sheet.createRow(lastRowNum + 1)
            } else {
                sheet.getRow(exists!!.rowNum)
            }
            if (!update || forceInsert) {
                newRow.copyRowFrom(sheet.getRow(lastRowNum - 2), CellCopyPolicy())
            }
            newRow.getCell(0).setCellValue(equipment.number.toDouble()) // 순번
            newRow.getCell(1).setCellValue(equipment.mgmtNumber) // 관리번호
            newRow.getCell(2).setCellValue(equipment.modelName) // 모델명
            newRow.getCell(3).setCellValue(equipment.mfrDate) // 제조일자
            newRow.getCell(4).setCellValue(equipment.serialNumber) // SN
            if (equipment is PC) {
                newRow.getCell(5).setCellValue(equipment.cpu)
                newRow.getCell(6).setCellValue(equipment.hdd.toString() + " GB")
                newRow.getCell(7).setCellValue(equipment.ram.toString() + " GB")
                newRow.getCell(8).setCellValue(equipment.OS)
                if (index == 2 && equipment.inch != null) {
                    newRow.getCell(9).setCellValue(equipment.inch.toDouble())
                }
            } else if (equipment is Monitor) {
                newRow.getCell(5).setCellValue(equipment.ratio)
                newRow.getCell(6).setCellValue(equipment.resolution)
                newRow.getCell(7).setCellValue(equipment.inch.toDouble())
                newRow.getCell(8).setCellValue(equipment.cable)
            } else return
            val columnAddition = if (index == 2) 1 else 0
            newRow.getCell(9 + columnAddition).setCellValue(equipment.lastUser)
            newRow.getCell(10 + columnAddition).setCellValue(equipment.importDate)
            newRow.getCell(11 + columnAddition).setCellValue(equipment.status.value)
            newRow.getCell(14 + columnAddition).setCellValue(equipment.memo)
            if (!update || forceInsert) {
                table.area = AreaReference(
                    table.startCellReference,
                    CellReference(lastRowNum + 1, table.endColIndex),
                    SpreadsheetVersion.EXCEL2007
                )
            }
        } catch (e: Exception) {
            println(e.message)
        } finally {
            save()
            when (index) {
                1 -> pc.add(equipment as PC)
                2 -> laptop.add(equipment as PC)
                3 -> monitor.add(equipment as Monitor)
            }
        }
    }

    fun deleteEquipment(index: Int, number: Int, mgmtNumber: String, lastUser: String, modelName: String) {
        if (number == -1 || index == -1) return
        try {
            val sheet = workbook.getSheet(getSheetName(index))
            val existsRow = sheet.find {
                it.rowNum != 0 && (it.getCell(0)?.numericCellValue?.toInt() == number)
            } ?: return
            val table = sheet.tables.firstOrNull() ?: return
            val columnAddition = if (index == 2) 1 else 0
            val existsMgmtNumber = existsRow.getCell(1).stringCellValue
            val existsLastUser = existsRow.getCell(9 + columnAddition).stringCellValue
            val existsModelName = existsRow.getCell(2).stringCellValue
            if (existsLastUser != lastUser || existsMgmtNumber != mgmtNumber || existsModelName != modelName) return
            val rowNumber = existsRow.rowNum
            sheet.removeRow(existsRow)
            sheet.shiftRows(rowNumber + 1, sheet.lastRowNum, -1)
            table.area = AreaReference(
                table.startCellReference,
                CellReference(sheet.lastRowNum, table.endColIndex),
                SpreadsheetVersion.EXCEL2007
            )
            sheet.getRow(table.endRowIndex)
        } catch (e: Exception) {
            println(e.message)
        } finally {
            //lastRow.rowStyle.borderTop = BorderStyle.THIN
            //astRow.rowStyle = sheet.getRow(sheet.lastRowNum - 2).rowStyle
            //sheet.removeRow(sheet.getRow(table.endRowIndex + 2))
            save()
            when (index) {
                1 -> pc.removeIf { it.number == number }
                2 -> laptop.removeIf { it.number == number }
                3 -> monitor.removeIf { it.number == number }
            }
        }
    }

    private fun save() {
        workbook.write(FileOutputStream(Constants.worksheetPath))
    }

    private fun getSheetName(index: Int) = when (index) {
        1 -> "PC"
        2 -> "노트북"
        3 -> "모니터"
        else -> null
    }

    fun getEmptyNumber(index: Int): Int {
        val lst = when (index) {
            1 -> pc
            2 -> laptop
            3 -> monitor
            else -> emptyList()
        }
        val numbers = lst.map { it.number }
        for (i in 1..numbers.last() + 1) {
            if (i !in numbers) return i
        }
        return 1
    }
}