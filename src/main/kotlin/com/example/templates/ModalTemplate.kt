package com.example.templates

import io.ktor.server.html.*
import kotlinx.html.*

/**
 * Template for all the modals
 * @param modalId id for Modal and modal tag
 * @param _tabIndex tabIndex attribute. Default -1
 * @property modalTitle title of modal. Pass title string with + operator
 * @property modalBody html content of body of modal
 * @property modalButton Close button will be placed in default. Use this if modal need more buttons
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
class ModalTemplate(
    private val modalId: String,
    private val _tabIndex: Int = -1
) : Template<FlowContent> {
    val modalTitle = Placeholder<FlowContent>()
    val modalBody = Placeholder<FlowContent>()
    val modalButton = Placeholder<FlowContent>()
    override fun FlowContent.apply() {
        div("modal fade") {
            id = modalId
            tabIndex = _tabIndex.toString()
            div("modal-dialog") {
                div("modal-content") {
                    div("modal-header") {
                        h5("modal-title") {
                            insert(modalTitle)
                        }
                        button(type = ButtonType.button, classes = "btn-close") {
                            attributes["data-bs-dismiss"] = "modal"
                        }
                    }
                    div("modal-body") {
                        insert(modalBody)
                    }
                    div("modal-footer") {
                        button(type = ButtonType.button, classes = "btn btn-secondary") {
                            attributes["data-bs-dismiss"] = "modal"
                            +"닫기"
                        }
                        insert(modalButton)
                    }
                }
            }
        }
    }
}