package com.example.templates

import io.ktor.server.html.*
import kotlinx.html.*

/**
 * Control panel for maintenance server of Daichang in lively or without restart the server.
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
class ControlPanelTemplate : Template<FlowContent> {
    override fun FlowContent.apply() {
        div("row") {
            div("col-6") {
                div("mb-2") {
                    h3 {
                        +"ERP3 data management"
                    }
                    p {
                        +"With existing data stored in ERP3, It can helps you with auto-filling with the management number when add or modify a equipment. Export spreadsheet file from ERP3, import this file, Hit ENTER key after type a management number of desired equipment. If found and matched the type of equipment, Most of other fields will be fill with existing data."
                    }
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
                                        +"Import"
                                    }
                                    button(type = ButtonType.button, classes = "btn btn-warning") {
                                        id = "spreadsheetCardCleanButton"
                                        +"Clean"
                                    }
                                }
                            }
                        }
                    }
                }
            }
            div("col-6") {
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
    }
}