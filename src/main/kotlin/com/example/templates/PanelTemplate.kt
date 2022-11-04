package com.example.templates

import io.ktor.server.html.*
import kotlinx.html.*

/**
 * Top panel of Table of equipments to Search, Filter
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
class PanelTemplate : Template<FlowContent> {
    val totalCount = Placeholder<FlowContent>()
    override fun FlowContent.apply() {
        div("input-group") {
                button(type = ButtonType.button, classes = "btn btn-primary") {
                    id = "addButton"
                    attributes["data-bs-toggle"] = "modal"
                    attributes["data-bs-target"] = "#addModal"
                    +"추가"
                }
                button(type = ButtonType.button, classes = "btn btn-secondary") {
                    id = "filterButton"
                    attributes["data-bs-toggle"] = "modal"
                    attributes["data-bs-target"] = "#filterModal"
                    +"필터"
                }
            input(classes = "form-control") { // Working with dataTables
                id = "searchBox"
                placeholder = "간편 검색"
            }
        }
    }
}