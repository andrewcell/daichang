package com.example.templates

import com.example.*
import com.example.database.DatabaseHandler
import com.example.database.PCTable
import io.ktor.server.html.*
import kotlinx.html.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class TableTemplate(private val index: Byte) : Template<FlowContent> {
    private var totalCount = 0
    private var list: List<Equipment> = DatabaseHandler.getList(index.toInt())
    private val modelNames = mutableListOf<String>()
    private val lastUsers = mutableListOf<String>()
    private val colList = when(index.toInt()) {
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
        insert(PanelTemplate()) {}
        // index 1 pc 2 laptop 3 monitor
        table("table") {
            thead {
                tr {
                    colList.forEach {
                        th(scope = ThScope.col) { +it }
                    }
                }
            }
            tbody {
                val dataInfoAttr = "data-info"
                totalCount = list.size
                list.forEach {
                    if (!modelNames.contains(it.modelName)) modelNames.add(it.modelName) // Add model name to list for filter.
                    if (!lastUsers.contains(it.lastUser)) lastUsers.add(it.lastUser) // Add model name to list for filter.
                    tr {
                        attributes["data-bs-toggle"] = "modal"
                        attributes["data-bs-target"] = "#addModal"
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
        div {
            id = "emptyNumber"
            style = "display: none;"
            +DatabaseHandler.getEmptyCabinetNumber(index.toInt()).toString()
        }
        h5 {
            +"총 개수: $totalCount"
        }
        val cpuList = if (index in 1..2) {
            var list = emptyList<String>()
            transaction {
                val rowList = PCTable.selectAll().toList().map {
                    it[PCTable.cpu]
                }
                list = rowList
            }//.distinct().sorted()
            list.distinct().sorted()
        } else null
        insert(AddModalTemplate(index.toInt(), list.map { it.modelName }.distinct().sorted(), cpuList)) {}
        insert(ModalTemplate("filterModal")) {
            modalTitle { +"필터" }
            modalBody {
                form(action = "/filter", method = FormMethod.post) {
                    id = "filterModalForm"
                    div("mb-3") {
                        div("form-check form-check-inline") {
                            input(type = InputType.radio, classes = "form-check-input") {
                                id = "AndOrAnd"
                                name = "andOr"
                                value = "and"
                                checked = true
                            }
                            label("form-check-label") {
                                htmlFor = "AndOrAnd"
                                +"AND"
                            }
                        }
                        div("form-check form-check-inline") {
                            input(type = InputType.radio, classes = "form-check-input") {
                                id = "AndOrOr"
                                name = "andOr"
                                value = "or"
                            }
                            label("form-check-label") {
                                htmlFor = "AndOrOr"
                                +"OR"
                            }
                        }
                    }
                    insert(
                        FilterInputTemplate(
                            "mfr",
                            "제조사",
                            (Constants.staticData?.mfr?.keys?.toList() ?: emptyList())
                        )
                    ) { }
                    modelNames.sort()
                    insert(FilterInputTemplate("modelName", "모델명", modelNames)) { }
                    insert(FilterInputTemplate("status", "상태", Status.values().map { it.value })) { }
                    lastUsers.sort()
                    insert(FilterInputTemplate("lastUser", "최종 사용자", lastUsers)) { }
                    insert(FilterInputTemplate("queryType", "문자열 포함", colList)) {
                        inputContent {
                            input(InputType.text, name = "query", classes = "form-control") {
                                id = "filterQuery"
                            }
                        }
                    }
                    input(InputType.hidden, name = "index") {
                        value = index.toString()
                    }
                }
            }
            modalButton {
                button(type = ButtonType.button, classes = "btn btn-warning") {
                    id = "filterModalReleaseButton"
                    +"필터해제"
                }
                button(type = ButtonType.button, classes = "btn btn-primary") {
                    id = "filterModalApplyButton"
                    +"적용"
                }
            }
        }
    }
}