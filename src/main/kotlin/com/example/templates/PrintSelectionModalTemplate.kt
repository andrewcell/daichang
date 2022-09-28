package com.example.templates

import com.example.Equipment
import io.ktor.server.html.*
import kotlinx.html.FlowContent
import kotlinx.html.*
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class PrintSelectionModalTemplate(
    private val index: Int,
    private val list: List<Equipment>
) : Template<FlowContent> {
    override fun FlowContent.apply() {
        val modalId = when (index) {
            1 -> "pc"
            2 -> "laptop"
            3 -> "monitor"
            else -> ""
        }
        insert(ModalTemplate("${modalId}PrintModal")) {
            modalTitle { +"인쇄할 항목 선택" }
            modalBody {
                div("list-group") {
                    list.sortedByDescending { it.importDate }.forEach {
                        a("#", classes = "list-group-item list-group-item-action") {
                            attributes["data-info"] = it.id.toString()
                            div("d-flex w-100 justify-content-between") {
                                h6 { +"${it.cabinetNumber} - ${it.mgmtNumber}" }
                            }
                                div {
                                    +"${it.modelName} - ${it.lastUser} - ${it.status.value}"
                                }
                                div {
                                    +"입고일자: ${it.importDate.format(DateTimeFormatter.ISO_LOCAL_DATE)}"
                                }
                            small("text-muted") { +it.memo }
                        }
                    }
                }
            }
            modalButton {
                button(type = ButtonType.button, classes = "btn btn-primary") {
                    id = "${modalId}PrintSelectionButton"
                    attributes["data-bs-dismiss"] = "modal"
                    attributes["data-info"] = modalId
                    +"완료"
                }
            }
        }
    }
}