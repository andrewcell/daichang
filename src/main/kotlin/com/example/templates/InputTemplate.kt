package com.example.templates

import io.ktor.server.html.*
import kotlinx.html.*

/**
 * Single Input field for Add Modal.
 * @param inputId value of tag attributes: 'id', 'name', 'for'
 * @param inputPlaceholder placeholder of input field
 * @param inputValue default value of input field
 * @param inputType Type of input field. Default is text field
 * @param inputList id of datalist tag to link datalist tag and auto-listing
 * @param inputRequired If its required
 * @property inputLabel Label content string. Use with + operator
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
class InputTemplate(
    private val inputId: String,
    private val inputPlaceholder: String = "",
    private val inputValue: String = "",
    private val inputType: InputType = InputType.text,
    private val inputList: String? = null,
    private val inputRequired: Boolean = true
    //private val input
) : Template<FlowContent> {
    val inputLabel = Placeholder<FlowContent>()
    override fun FlowContent.apply() {
        div("mb-3") {
            label("form-label") {
                htmlFor = inputId
                insert(inputLabel)
            }
            input(type = inputType, classes = "form-control") {
                id = inputId
                name = inputId
                placeholder = inputPlaceholder
                value = inputValue
                required = inputRequired
                if (inputList != null) list = inputList
            }
        }
    }
}