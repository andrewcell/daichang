package com.example.templates

import com.example.Equipment
import com.example.Status
import io.ktor.server.html.*
import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.*
import kotlinx.html.script
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class IndexTemplate(private val list: Array<List<Equipment>>) : Template<FlowContent> {
    override fun FlowContent.apply() {
        var available = 0
        var notAvailable = 0
        var toBeDispose = 0
        val counts = mutableListOf<Array<Int>>()
        list.forEach { equip ->
            equip.forEach {
                when (it.status) {
                    Status.AVAILABLE -> available++
                    Status.NOT_AVAILABLE -> notAvailable++
                    Status.TO_BE_DISPOSE -> toBeDispose++
                }
            }
            counts.add(arrayOf(available, notAvailable, toBeDispose))
            available = 0
            notAvailable = 0
            toBeDispose = 0
        }
        div {
            id = "equipmentCounts"
            style = "display: none;"
            +Json.encodeToString(counts)
        }
        val equipmentNameMap = mapOf(
            Pair("pc", "PC 상태 현황"),
            Pair("laptop", "노트북 상태 현황"),
            Pair("monitor", "모니터 상태 현황")
        )
        div("row") {
            div("col-6") {
                div("card mb-1") { // Print card
                    div("card-header") {
                        h5("card-title") { +"인쇄" }
                    }

                    div("card-body") {
                        div(classes = "btn-group") {
                            role = "group"
                            listOf(
                                Pair("pc", "PC"),
                                Pair("laptop", "노트북"),
                                Pair("monitor", "모니터")
                            ).forEach { (identifier, title) ->
                                val bid = "${identifier}PrintButton"
                                val target = "#${identifier}PrintModal"
                                button(type = ButtonType.button, classes = "btn btn-primary") {
                                    id = bid
                                    attributes["data-bs-toggle"] = "modal"
                                    attributes["data-bs-target"] = target
                                    +title
                                }
                            }
                        }
                    }
                    form("/print", method = FormMethod.post) {
                        target = "_blank"
                        id = "printForm"
                        input(type = InputType.hidden) {
                            id = "printPayload"
                            name = "data"
                            value = "{}"
                        }
                    }
                    div("card-footer") {
                        button(type = ButtonType.button, classes = "btn btn-warning") {
                            id = "printButton"
                            +"인쇄"
                        }
                    }
                }
            }
            div("col-6") {
                div("card") {
                    div("card-header") {
                        h5("card-title") { +"스프레드시트 Import/Export" }
                    }
                    div("card-body") {
                        form("/import", method = FormMethod.post, encType = FormEncType.multipartFormData) {
                            id = "spreadsheetCardImportForm"
                            div("row") {
                                div("col-9") {
                                    input(classes = "form-control", type = InputType.file) {
                                        id = "spreadsheetImportFile"
                                        name = "importFile"
                                    }
                                }
                                div("col-3") {
                                    div(classes = "btn-group") {
                                        role = "group"
                                        button(type = ButtonType.button, classes = "btn btn-primary") {
                                            id = "spreadsheetCardImportButton"
                                            +"들여오기"
                                        }
                                        a(classes = "btn btn-primary", href = "/export") {
                                            target = "_blank"
                                            role = "button"
                                            id = "spreadsheetCardExportButton"
                                            +"내보내기"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        div("row") {
            equipmentNameMap.forEach { (id, title) ->
                insert(ChartCardTemplate(id)) {
                    titleText {
                        +title
                    }
                }
            }
        }
        insert(PrintSelectionModalTemplate(1, list[0])) {}
        insert(PrintSelectionModalTemplate(2, list[1])) {}
        insert(PrintSelectionModalTemplate(3, list[2])) {}
        script(src = "https://cdn.jsdelivr.net/npm/chart.js") {}
        script(src = "/static/index.js") {}
    }
}