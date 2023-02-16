package com.example

import com.example.WorkSheetHandler.lock
import com.example.database.DatabaseHandler
import com.example.models.ERPData
import com.example.models.Monitor
import com.example.models.PC
import com.example.models.Status
import io.ktor.http.*
import org.apache.poi.ss.SpreadsheetVersion
import org.apache.poi.ss.util.AreaReference
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId

/**
 * Spreadsheet file import / export handler. Used for store data in Phase 1, Now use for import and export data to spreadsheet afterwards.
 * @property lock Limit to only one job can be activated. if it is true, some functions will not be worked.
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
object WorkSheetHandler {
    var lock = false

    /**
     * Import spreadsheet into database.
     * @param input Spreadsheet as input stream
     * @return error message. null if is success.
     */
    fun import(input: InputStream): String? {
        try {
            val workbook = XSSFWorkbook(input)
            val pcList = readPC(workbook.getSheet("PC")) // read each sheets and convert it to object
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

    /**
     * Unlike import function, it will handle ERP data from official ERP 3. Import it, save it to database.
     * @param input Spreadsheet as input stream
     * @return error message. null if is success.
     */
    fun importERPData(input: InputStream, contentType: ContentType?): String? {
        try {
            val value = when (contentType?.toString()) {
                "text/csv" -> {
                    val rows = org.apache.commons.io.IOUtils.toString(input, Charset.forName("EUC-KR")).split("\n")
                    buildList {
                        rows.forEach { row ->
                            val vars = row.split(',')
                            if (vars.size != 20) return@forEach
                            val index = when (vars[3]) { // Choose index from equipment type column.
                                "데스크탑" -> 1
                                "노트북" -> 2
                                "모니터" -> 3
                                else -> return@forEach
                            }
                            val mfrDate = SimpleDateFormat("MM/dd/yyyy").parse(vars[7])
                            add(ERPData(
                                index = index,
                                mgmtNumber = vars[0],
                                modelName = vars[1],
                                serialNumber = vars[2],
                                var1 = vars[4],
                                var2 = vars[5],
                                var3 = vars[6],
                                mfrDate = SimpleDateFormat("yyyy-MM-dd").format(mfrDate),
                                lastUser = vars[10]
                            ))
                        }
                    }
                }
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> {
                    val workbook = XSSFWorkbook(input)
                    val sheet = workbook.first() // select first sheet. Only one sheet will exist.
                    buildList {
                        sheet.forEach { row ->
                            if (row.rowNum == sheet.lastRowNum) return@forEach // if its last row <-- total counts is in last row.
                            if (row.rowNum == 0) return@forEach // if its first row <-- column labels are in first row.
                            val index = when (row.getCell(3).stringCellValue ?: "") { // Choose index from equipment type column.
                                "데스크탑" -> 1
                                "노트북" -> 2
                                "모니터" -> 3
                                else -> -1
                            }
                            add(
                                ERPData( // Convert to ERPData object and add to list
                                    index = index,
                                    mgmtNumber = row.getCell(0).stringCellValue,
                                    modelName = row.getCell(1).stringCellValue,
                                    serialNumber = row.getCell(2).stringCellValue,
                                    var1 = row.getCell(4).stringCellValue,
                                    var2 = row.getCell(5).stringCellValue,
                                    var3 = row.getCell(6).stringCellValue,
                                    mfrDate = row.getCell(7).stringCellValue,
                                    lastUser = row.getCell(10).stringCellValue
                                )
                            )
                        }
                    }
                }
                else -> {
                    throw Exception("Unsupported file type uploaded.")
                }
            }
            DatabaseHandler.importERP(value) // Send it to DatabaseHandler to save into database.
        } catch (e: Exception) {
            lock = false // Release lock
            return e.message
        }
        return null
    }

    /**
     * Convert PC or Laptop sheet into the list of PC objects.
     * @param sheet XSSFSheet selected from workbook
     * @return List of PC objects
     */
    private fun readPC(sheet: XSSFSheet): List<PC> {
        val laptop = sheet.sheetName == "노트북" // If is sheet name, handle as laptop
        return buildList {
            sheet.forEach { row ->
                if (row.rowNum == 0) return@forEach // first row is column label row. Pass it.
                try {
                    val inch = if (laptop) row.getCell(9).numericCellValue.toFloat() else null // set inch if marked as laptop.
                    val pc = PC(
                        id = -1,
                        cabinetNumber = row.getCell(0).numericCellValue.toInt(),
                        mgmtNumber = row.getCell(1).stringCellValue,
                        modelName = row.getCell(2).stringCellValue,
                        mfrDate = LocalDate.ofInstant(row.getCell(3).dateCellValue.toInstant(), ZoneId.systemDefault()), // Convert using instant
                        serialNumber = row.getCell(4).stringCellValue,
                        cpu = row.getCell(5).stringCellValue,
                        hdd = teraByteToGigaByte(row.getCell(6).stringCellValue), // 1 TB or 2 TB convert to 1024GB or 2048GB
                        ram = row.getCell(7).stringCellValue.replace("GB", "").trim().toFloatOrNull() ?: 0.0f, // Try to convert into Float type, if failed, 0
                        OS = row.getCell(8).stringCellValue,
                        lastUser = row.getCell(if (laptop) 10 else 9).stringCellValue, // This and above cells, Laptop should be added 1 into index, because inch column is number 9.
                        importDate = LocalDate.ofInstant(row.getCell(if (laptop) 11 else 10).dateCellValue.toInstant(), ZoneId.systemDefault()), // Convert using instant
                        status = Status.findByValue(row.getCell(if (laptop) 12 else 11).stringCellValue) ?: Status.TO_BE_DISPOSE, // Convert to Status object. If failed, mark as to be disposed.
                        memo = row.getCell(if (laptop) 15 else 14).stringCellValue,
                        inch = inch, // null if is not laptop.
                        isLaptop = laptop,
                    )
                    add(pc)
                } catch (_: Exception) {
                    return@forEach // Skip if row has errors. No need to parse so hard.
                }
            }
        }
    }

    /**
     * Convert monitor sheet into the list of Monitor objects.
     * @param sheet XSSFSheet selected from workbook
     * @return List of Monitor objects
     */
    private fun readMonitor(sheet: XSSFSheet): List<Monitor> {
        return buildList {
            sheet.forEach { row ->
                if (row.rowNum == 0) return@forEach // first row is column label row. Pass it.
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
                        status = Status.findByValue(row.getCell(11).stringCellValue) ?: Status.TO_BE_DISPOSE, // Convert to Status object. If failed, mark as to be disposed.
                        memo = row.getCell(14).stringCellValue,
                    )
                    add(monitor)
                } catch (_: Exception) {
                    return@forEach // Skip if row has errors. No need to parse so hard.
                }
            }
        }
    }

    /**
     * To parse storage string value properly. get raw string value, and convert to int type.
     * @param raw Raw string value from spreadsheet
     * @return storage capacity in Gigabyte
     */
    private fun teraByteToGigaByte(raw: String): Int {
        val value = raw.dropLast(2).trim().toIntOrNull() ?: 0 // Drop last "GB" word and convert into int.
        return if (raw.endsWith("TB")) { // If last word is TB, multiply 1024 to store as Gigabyte.
            return value * 1024
        } else value
    }


    /**
     * Create PC, Monitors spreadsheet just same as old spreadsheet file and can be imported to daichang.
     * @return spreadsheet as ByteArrayOutputStream. can be served as HTTP file download, or directly write to a local file.
     */
    fun export(): ByteArrayOutputStream? {
        return try {
            val workbook = XSSFWorkbook()
            val list = DatabaseHandler.getAll()
            val sheetNames = arrayOf("PC", "노트북", "모니터") // Sheet names
            val dateFormat = workbook.createCellStyle()
            dateFormat.dataFormat = workbook.creationHelper.createDataFormat().getFormat("yyyy-MM-dd")
            list.forEachIndexed { equipIndex, equipList ->
                val sheet = workbook.createSheet(sheetNames[equipIndex]) // create sheet names from sheetNames
                val colLabelRow = sheet.createRow(0)
                val colList = when (equipIndex + 1) {
                    1 -> Constants.colsPC
                    2 -> Constants.colsLaptop
                    3 -> Constants.colsMonitor
                    else -> return null
                }.map { it.first }.toMutableList() // Column info name is not necessary
                colList.removeLast() // To insert unused two columns, remove last column(memo)
                colList.add("empty1") // For copy-paste to original spreadsheet. Some unused column still exists
                colList.add("empty2")
                colList.add(Constants.colsPC.last().first) // add memo column to last column
                for((index, col) in colList.withIndex()) {
                    colLabelRow.createCell(index).setCellValue(col) // Set column label using colList above
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
                        if (equipIndex + 1 == 2 && equipment.inch != null) { // Index is laptop and inch is not null
                            newRow.createCell(9).setCellValue(equipment.inch.toDouble())
                        }
                    } else if (equipment is Monitor) {
                        newRow.createCell(5).setCellValue(equipment.ratio)
                        newRow.createCell(6).setCellValue(equipment.resolution)
                        newRow.createCell(7).setCellValue(equipment.inch.toDouble())
                        newRow.createCell(8).setCellValue(equipment.cable)
                    } else return null // If caused error, might be data is corrupted. Bail out and return null
                    val columnAddition = if (equipIndex + 1 == 2) 1 else 0
                    newRow.createCell(9 + columnAddition).setCellValue(equipment.lastUser)
                    val importDateCell = newRow.createCell(10 + columnAddition)
                    importDateCell.setCellValue(equipment.importDate)
                    importDateCell.cellStyle = dateFormat
                    newRow.createCell(11 + columnAddition).setCellValue(equipment.status.value)
                    newRow.createCell(14 + columnAddition).setCellValue(equipment.memo)
                }
                val table = sheet.createTable(AreaReference(
                    CellReference(0, 0),
                    CellReference(equipList.size,  colList.size - 1),
                    SpreadsheetVersion.EXCEL2007)) // Set table
                table.columns.forEach { // Auto size column by values
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