package com.example.templates

import io.ktor.server.html.*
import kotlinx.html.*

class ChartCardTemplate(val tid: String) : Template<FlowContent> {
    val titleText = Placeholder<FlowContent>()
    override fun FlowContent.apply() {
        div("col-4") {
            div("card") {
                h5("card-header") {
                    insert(titleText)
                }
                div("card-body") {
                    canvas {
                        id = "${tid}Chart"
                    }
                }
            }
        }
    }
}