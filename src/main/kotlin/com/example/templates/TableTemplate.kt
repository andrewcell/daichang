package com.example.templates

import com.example.Constants
import com.example.models.Equipment
import com.example.models.Monitor
import com.example.models.PC
import com.example.database.DatabaseHandler
import io.ktor.server.html.*
import kotlinx.html.*
import java.time.format.DateTimeFormatter

/**
 * Table template for equipment tables
 * @param index Index number of equipment type
 * @property list List of equipment objects
 * @property modelNames List of model names use in filter modal
 * @property lastUsers List of last usernames use in filter modal
 * @property colList List of tables columns
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
class TableTemplate(private val index: Byte) : Template<FlowContent> {
    private var totalCount = 0
    private var list: List<Equipment> = DatabaseHandler.getList(index.toInt()) ?: emptyList()
    private val modelNames = mutableListOf<String>()
    private val lastUsers = mutableListOf<String>()
    private val colList = when(index.toInt()) { // Every equipment type has different columns
        1 -> {
            Constants.colsPC
        }
        2 -> {
            Constants.colsLaptop
        }
        3 -> {
            Constants.colsMonitor
        }
        else -> Constants.colsPC
    }
    override fun FlowContent.apply() {
        val dataInfoAttr = "data-info" // In javascript app.js, need identify which value is. Have an extra attribute to fix this problem.
        insert(PanelTemplate()) {}
        // index 1 pc 2 laptop 3 monitor
        table("table") {
            thead {
                tr {
                    colList.forEach { // Generate column tags
                        th(scope = ThScope.col) {
                            attributes[dataInfoAttr] = it.second
                            +it.first
                        }
                    }
                }
            }
            tbody {
                totalCount = list.size
                list.forEach { // Every single equipment
                    if (!modelNames.contains(it.modelName)) modelNames.add(it.modelName) // Add model name to list for filter.
                    if (!lastUsers.contains(it.lastUser)) lastUsers.add(it.lastUser) // Add model name to list for filter.
                    tr {
                        attributes["data-bs-toggle"] = "modal"
                        attributes["data-bs-target"] = "#addModal" // Make table row clickable
                        attributes["data-id"] = it.id.toString()
                        /*td {
                            attributes[dataInfoAttr] = "id"
                            +it.id.toString()
                        }*/
                        td {
                            attributes[dataInfoAttr] = "cabinetNumber"
                            +it.cabinetNumber.toString()
                        }
                        td {
                            attributes[dataInfoAttr] = "mgmtNumber"
                            +it.mgmtNumber
                        }
                        td {
                            attributes[dataInfoAttr] = "modelName"
                            +it.modelName
                        }
                        td {
                            attributes[dataInfoAttr] = "mfrDate"
                            +it.mfrDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        }
                        td {
                            attributes[dataInfoAttr] = "serial"
                            +it.serialNumber
                        }
                        if (it is PC) {
                            td {
                                attributes[dataInfoAttr] = "CPU"
                                +it.cpu
                            }
                            td {
                                attributes[dataInfoAttr] = "HDD"
                                +(it.hdd.toString() + "GB")
                            }
                            td {
                                attributes[dataInfoAttr] = "RAM"
                                +(it.ram.toString() + "GB")
                            }
                            td {
                                attributes[dataInfoAttr] = "OS"
                                +it.OS
                            }
                            if (index.toInt() == 2) {
                                td {
                                    attributes[dataInfoAttr] = "inch"
                                    +it.inch.toString()
                                }
                            }
                        } else if (it is Monitor) {
                            td {
                                attributes[dataInfoAttr] = "ratio"
                                +it.ratio
                            }
                            td {
                                attributes[dataInfoAttr] = "resolution"
                                +it.resolution
                            }
                            td {
                                attributes[dataInfoAttr] = "inch"
                                +it.inch.toString()
                            }
                            td {
                                attributes[dataInfoAttr] = "cable"
                                +it.cable
                            }
                        }
                        td {
                            attributes[dataInfoAttr] = "lastUser"
                            +it.lastUser
                        }
                        td {
                            attributes[dataInfoAttr] = "importDate"
                            +it.importDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        }
                        td {
                            attributes[dataInfoAttr] = "status"
                            +it.status.value
                        }
                        td {
                            attributes[dataInfoAttr] = "memo"
                            +it.memo
                        }
                    }
                }
            }
        }
        div { // Extra Empty number tag to autofill empty number when click add button
            id = "emptyNumber"
            style = "display: none;" // Hide it
            +DatabaseHandler.getEmptyCabinetNumber(index.toInt()).toString()
        }
        h5 {
            +"총 개수: $totalCount"
        }
        val cpuList = if (index in 1..2) {
            val list = DatabaseHandler.getList(index.toInt())?.mapNotNull {
                if (it is PC)
                    it.cpu
                else null
            }
            list?.distinct()?.sorted()
        } else null
        insert(AddModalTemplate(index.toInt(), list.map { it.modelName }.distinct().sorted(), cpuList)) {} // Add addModal to page
        insert(FilterModalTemplate(index.toInt(), modelNames, lastUsers, colList.map { it.first })) { } // Add filterModal to page
    }
}