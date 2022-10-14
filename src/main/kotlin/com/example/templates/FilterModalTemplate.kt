package com.example.templates

import com.example.Constants
import com.example.Status
import io.ktor.server.html.*
import kotlinx.html.*

class FilterModalTemplate(
    private val index: Int,
    private val modelNames: MutableList<String>,
    private val lastUsers: MutableList<String>,
    private val colList: List<String>
) : Template<FlowContent> {
    override fun FlowContent.apply() {
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