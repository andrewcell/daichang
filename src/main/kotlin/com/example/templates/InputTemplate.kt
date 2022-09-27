package com.example.templates

import io.ktor.server.html.*
import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.*

class InputTemplate(
    private val inputId: String,
    private val inputPlaceholder: String = "",
    private val inputValue: String = "",
    private val inputType: InputType = InputType.text,
    private val inputList: String? = null,
    private val inputRequired: Boolean = false
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