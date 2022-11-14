package com.example.templates

import com.example.Constants
import com.example.models.Status
import io.ktor.server.html.*
import kotlinx.html.*

/**
 * Modal for filtering table rows in TableTemplate. Provide advanced search tool for equipments table.
 *
 * @param index Index of table. 1 for PC, 2 for Laptop, 3 for Monitor. Other values will be ignored.
 * @param modelNames List of equipment model names. Use for select tag to select a model name extracted from the table.
 * @param lastUsers List of equipment last usernames. Use for select tag to select a last username extracted from the table.
 * @param colList List of Columns. For search in value from the specific column.
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
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