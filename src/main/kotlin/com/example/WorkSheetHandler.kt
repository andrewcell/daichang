package com.example

import com.example.database.DatabaseHandler
import org.apache.poi.ss.SpreadsheetVersion
import org.apache.poi.ss.util.AreaReference
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.time.LocalDate
import java.time.ZoneId

object WorkSheetHandler {
    var lock = false
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
                        hdd = teraByteToGigaByte(row.getCell(6).stringCellValue),
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


    fun export(): ByteArrayOutputStream? {
        return try {
            val workbook = XSSFWorkbook()
            val list = DatabaseHandler.getAll()
            val sheetNames = arrayOf("PC", "노트북", "모니터")
            val dateFormat = workbook.createCellStyle()
            dateFormat.dataFormat = workbook.creationHelper.createDataFormat().getFormat("yyyy-MM-dd")
            list.forEachIndexed { equipIndex, equipList ->
                val sheet = workbook.createSheet(sheetNames[equipIndex])
                val colLabelRow = sheet.createRow(0)
                val colList = when (equipIndex + 1) {
                    1 -> Constants.colsPC
                    2 -> Constants.colsLaptop
                    3 -> Constants.colsMonitor
                    else -> return null
                }
                for((index, col) in colList.withIndex()) {
                    colLabelRow.createCell(index).setCellValue(col)
                }
                equipList.forEachIndexed { rowIndex, equipment ->
                    val newRow = sheet.createRow(rowIndex + 1)
                    newRow.createCell(0).setCellValue(equipment.cabinetNumber?.toDouble() ?: 0.0) // 순번
                    newRow.createCell(1).setCellValue(equipment.mgmtNumber) // 관리번호
                    newRow.createCell(2).setCellValue(equipment.modelName) // 모델명
                    val mfrDateCell = newRow.createCell(3)
                    mfrDateCell.setCellValue(equipment.mfrDate) // 제조일자
                    mfrDateCell.cellStyle = dateFormat
                    newRow.createCell(4).setCellValue(equipment.serialNumber) // SN
                    if (equipment is PC) {
                        newRow.createCell(5).setCellValue(equipment.cpu)
                        newRow.createCell(6).setCellValue(equipment.hdd.toString() + " GB")
                        newRow.createCell(7).setCellValue(equipment.ram.toString() + " GB")
                        newRow.createCell(8).setCellValue(equipment.OS)
                        if (equipIndex + 1 == 2 && equipment.inch != null) {
                            newRow.createCell(9).setCellValue(equipment.inch.toDouble())
                        }
                    } else if (equipment is Monitor) {
                        newRow.createCell(5).setCellValue(equipment.ratio)
                        newRow.createCell(6).setCellValue(equipment.resolution)
                        newRow.createCell(7).setCellValue(equipment.inch.toDouble())
                        newRow.createCell(8).setCellValue(equipment.cable)
                    } else return null
                    val columnAddition = if (equipIndex + 1 == 2) 1 else 0
                    newRow.createCell(9 + columnAddition).setCellValue(equipment.lastUser)
                    val importDateCell = newRow.createCell(10 + columnAddition)
                    importDateCell.setCellValue(equipment.importDate)
                    importDateCell.cellStyle = dateFormat
                    newRow.createCell(11 + columnAddition).setCellValue(equipment.status.value)
                    newRow.createCell(12 + columnAddition).setCellValue(equipment.memo)
                }
                val table = sheet.createTable(AreaReference(
                    CellReference(0, 0),
                    CellReference(equipList.size,  colList.size - 1),
                    SpreadsheetVersion.EXCEL2007))
                table.columns.forEach {
                    sheet.autoSizeColumn(it.columnIndex)
                }
            }

            val os = ByteArrayOutputStream()
            workbook.write(os)
            //TODO: Not implemented.
            os
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}