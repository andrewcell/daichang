package com.example.templates

import io.ktor.server.html.*
import kotlinx.html.*

/**
 * Single card tag contains chart object for index page
 * @param tid identifier of what equipment chart is. value must be one of these: pc, laptop, monitor
 * @property titleText Title for Card
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
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