package com.example.templates

import io.ktor.server.html.*
import kotlinx.html.*

class ControlPanelTemplate : Template<FlowContent> {
    override fun FlowContent.apply() {
        div("card") {
            div("card-header") {
                h5 {
                    +"ERP3 데이터 관리"
                }
            }
            div("card-body") {
                form("/erp", method = FormMethod.post, encType = FormEncType.multipartFormData) {
                    id = "spreadsheetCardImportForm"
                    div("row") {
                        div("col-9") {
                            input(classes = "form-control", type = InputType.file) {
                                id = "spreadsheetImportFile"
                                name = "importFile"
                            }
                        }
                        div("col-3") {
                            div(classes = "btn-group") {
                                role = "group"
                                button(type = ButtonType.button, classes = "btn btn-primary") {
                                    id = "spreadsheetCardImportButton"
                                    +"들여오기"
                                }
                                a(classes = "btn btn-primary", href = "/export") {
                                    target = "_blank"
                                    role = "button"
                                    id = "spreadsheetCardCleanButton"
                                    +"전체삭제"
                                }
                            }
                        }
                    }
                }
            }
        }
        div("mb-2") {
            h3 { +"In-memory cache management" }
            p { +"This website stores every data from database into memory upon initial start for better performance. If database modified by hand or 3rd-party tools, you need to clear the cache to use latest data. After click bottom button, Server will clear cache and rebuild from database. During this operation, server could turned to be in-operable." }
            button(classes = "btn btn-info") {
                id = "clearCacheButton"
                +"Rebuild cache"
            }
        }
    }
}