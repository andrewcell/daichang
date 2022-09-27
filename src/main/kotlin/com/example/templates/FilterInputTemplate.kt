package com.example.templates

import io.ktor.server.html.*
import kotlinx.html.*

class FilterInputTemplate(
    private val inputName: String,
    private val label: String,
    private val optionList: List<String>
) : Template<FlowContent> {
    val inputContent = Placeholder<FlowContent>()
    override fun FlowContent.apply() {
        div("mb-3") {
            label("form-label") {
                htmlFor = inputName
                +label
            }
            select(classes = "form-select") {
                name = inputName
                form = "filterModalForm"
                id = inputName
                option { +"-" }
                optionList.forEach {
                    option { +it }
                }
            }
            insert(inputContent)
        }
    }
}