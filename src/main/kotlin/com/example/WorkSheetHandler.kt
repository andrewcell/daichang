package com.example

import com.example.database.DatabaseHandler
import org.apache.poi.ss.SpreadsheetVersion
import org.apache.poi.ss.usermodel.CellCopyPolicy
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.util.AreaReference
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDate
import java.time.ZoneId

object WorkSheetHandler {
    fun import(input: InputStream): String? {
        try {
            val workbook = XSSFWorkbook(input)
            val pcList = readPC(workbook.getSheet("PC"))
            val laptopList = readPC(workbook.getSheet("노트북"))
            val monitorList = readMonitor(workbook.getSheet("모니터"))
            arrayOf(pcList, laptopList, monitorList).forEachIndexed { index, list ->
                list.forEach {
                    DatabaseHandler.insertNewEquipment(index + 1, it)
                }
            }
            workbook.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return e.message
        }
        return null
    }

    private fun readPC(sheet: XSSFSheet): List<PC> {
        val laptop = sheet.sheetName == "노트북"
        return buildList {
            sheet.forEach { row ->
                if (row.rowNum == 0) return@forEach
                try {
                    val inch = if (laptop) row.getCell(9).numericCellValue.toFloat() else null
                    val pc = PC(
                        id = -1,
                        cabinetNumber = row.getCell(0).numericCellValue.toInt(),
                        mgmtNumber = row.getCell(1).stringCellValue,
                        modelName = row.getCell(2).stringCellValue,
                        mfrDate = LocalDate.ofInstant(row.getCell(3).dateCellValue.toInstant(), ZoneId.systemDefault()),
                        serialNumber = row.getCell(4).stringCellValue,
                        cpu = row.getCell(5).stringCellValue,
                        hdd = teraByteToGigaByte(row.getCell(6).stringCellValue).toShort(),
                        ram = row.getCell(7).stringCellValue.replace("GB", "").trim().toFloatOrNull() ?: 0.0f,
                        OS = row.getCell(8).stringCellValue,
                        lastUser = row.getCell(if (laptop) 10 else 9).stringCellValue,
                        importDate = LocalDate.ofInstant(row.getCell(if (laptop) 11 else 10).dateCellValue.toInstant(), ZoneId.systemDefault()),
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

    private fun readMonitor(sheet: XSSFSheet): List<Monitor> {
        return buildList {
            sheet.forEach { row ->
                if (row.rowNum == 0) return@forEach
                try {
                    val monitor = Monitor(
                        id = -1,
                        cabinetNumber = row.getCell(0).numericCellValue.toInt(),
                        mgmtNumber = row.getCell(1).stringCellValue,
                        modelName = row.getCell(2).stringCellValue,
                        mfrDate = LocalDate.ofInstant(row.getCell(3).dateCellValue.toInstant(), ZoneId.systemDefault()),
                        serialNumber = row.getCell(4).stringCellValue,
                        ratio = row.getCell(5).stringCellValue,
                        resolution = row.getCell(6).stringCellValue,
                        inch = row.getCell(7).numericCellValue.toFloat(),
                        cable = row.getCell(8).stringCellValue,
                        lastUser = row.getCell(9).stringCellValue,
                        importDate = LocalDate.ofInstant(row.getCell(10).dateCellValue.toInstant(), ZoneId.systemDefault()),
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

    private fun teraByteToGigaByte(raw: String): Int {
        val value = raw.dropLast(2).trim().toIntOrNull() ?: 0
        return if (raw.endsWith("TB")) {
            return value * 1024
        } else value
    }

    private fun save(): OutputStream? {
        //TODO: Not implemented.
        return null
    }
}