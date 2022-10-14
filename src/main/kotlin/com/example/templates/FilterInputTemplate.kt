package com.example.templates

import io.ktor.server.html.*
import kotlinx.html.*

/**
 * Select-Option tag area in Filter Modal
 *
 * @param inputName name, id of select tag
 * @param label select tag label text
 * @param optionList List of options string
 * @property inputContent If need to have extra input tag or html tags. OPTIONAL.
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
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