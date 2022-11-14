package com.example.templates

import com.example.models.Equipment
import io.ktor.server.html.*
import kotlinx.html.*
import java.time.format.DateTimeFormatter

/**
 * Modal for select equipments which going to print cabinet label
 * @param index Index number of equipment type
 * @param list list of all of equipments
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
class PrintSelectionModalTemplate(
    private val index: Int,
    private val list: List<Equipment>
) : Template<FlowContent> {
    override fun FlowContent.apply() {
        val modalId = when (index) { // Set modal tag and id from index
            1 -> "pc"
            2 -> "laptop"
            3 -> "monitor"
            else -> ""
        }
        insert(ModalTemplate("${modalId}PrintModal")) {
            modalTitle { +"인쇄할 항목 선택" }
            modalBody {
                div("list-group") {
                    list.sortedByDescending { it.importDate }.forEach {// Sort by import date
                        a("#", classes = "list-group-item list-group-item-action") {
                            attributes["data-info"] = it.mgmtNumber // Useful extra attribute to set selections in javascript. Management number will be use for request print
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
                button(type = ButtonType.button, classes = "btn btn-primary") { // Close modal and set selections
                    id = "${modalId}PrintSelectionButton"
                    attributes["data-bs-dismiss"] = "modal"
                    attributes["data-info"] = modalId
                    +"완료"
                }
            }
        }
    }
}