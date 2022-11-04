package com.example.templates

import io.ktor.server.html.*
import kotlinx.html.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Add Modal template. Modal for add or modify equipment
 * @param index Index number for which equipment type going to add or modify
 * @param modelList List of model names for auto-listing model name input field
 * @param cpuList List of cpus for auto-listing cpu input field
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
class AddModalTemplate(
    private val index: Int,
    private val modelList: List<String>,
    private val cpuList: List<String>? = null,
) : Template<FlowContent> {
    override fun FlowContent.apply() {
        insert(ModalTemplate("addModal")) {
            insert(ModalTemplate("deleteConfirmModal")) { // Delete confirm modal for prevent accidental click delete button
                modalTitle { +"삭제 확인" }
                modalBody { +"정말로 삭제합니까?"}
                modalButton {
                    button(classes = "btn btn-warning") {
                        attributes["data-bs-toggle"] = "modal"
                        attributes["data-bs-target"] = "#addModal"
                        +"취소"
                    }
                    button(classes = "btn btn-danger") {
                        id = "deleteConfirmButton"
                        onClick = "deleteConfirm"
                        +"삭제 확인"
                    }
                }
            }
            modalTitle {
                div {
                    +"장비 추가"
                }
            }
            modalBody {
                form(action = "/save", method = FormMethod.post) { // action is deprecated.
                    id = "addModalForm"
                    insert(InputTemplate("inputCabinetNumber", "456", inputRequired = true)) {
                        inputLabel { +"순번" }
                    }
                    insert(InputTemplate("inputMgmtNumber", "EQ2020090001", "EQ")) {
                        inputLabel { +"관리번호" }
                    }
                    insert(InputTemplate("inputModelName", "B80GV", inputList = "modelList")) {
                        inputLabel { +"모델명" }
                    }
                    dataList {
                        id = "modelList"
                        modelList.forEach { model ->
                            option { +model }
                        }
                    }
                    insert(InputTemplate("inputMfrDate", inputType = InputType.date, inputRequired = true)) {
                        inputLabel { +"제조일자" }
                    }
                    insert(InputTemplate("inputSerial", "112SHSA001234")) {
                        inputLabel { +"S/N" }
                    }
                    if (index in 1..2) { // If is Computer or Laptop.
                        insert(InputTemplate("inputCPU", "Intel(R) Core(TM) i5-10400 CPU @ 2.90GHz", inputList = "cpuDataList")) {
                            inputLabel { +"CPU" }
                        }
                        dataList {
                            id = "cpuDataList"
                            cpuList?.forEach { cpu ->
                                option { +cpu }
                            }
                        }
                        insert(InputTemplate("inputHDD", inputType = InputType.number)) {
                            inputLabel { +"HDD (GB)" }
                        }
                        insert(InputTemplate("inputRAM", inputType = InputType.number)) {
                            inputLabel { +"RAM (GB)" }
                        }
                        insert(InputTemplate("inputOS", "Win 10")) {
                            inputLabel { +"OS" }
                        }
                    }
                    if (index in 2..3) { // If is Laptop or Monitor
                        insert(InputTemplate("inputInch", "15.6")) {
                            inputLabel { +"인치" }
                        }
                    }
                    if (index.toInt() == 3) { // If is Monitor
                        insert(InputTemplate("inputRatio", "16:9")) {
                            inputLabel { +"화면비율" }
                        }
                        insert(InputTemplate("inputResolution", "1920x1080")) {
                            inputLabel { +"해상도" }
                        }
                        insert(InputTemplate("inputCable", "RGB/HDMI")) {
                            inputLabel { +"케이블종류" }
                        }
                    }
                    insert(InputTemplate("inputLastUser", "이이름")) {
                        inputLabel { +"최종사용자" }
                    }
                    insert(
                        InputTemplate(
                            "inputImportDate",
                            inputValue = SimpleDateFormat("yyyy-MM-dd").format(Date()),
                            inputType = InputType.date
                        )
                    ) {
                        inputLabel { +"입고일자" }
                    }
                    div("mb-3") {
                        label("form-label") {
                            htmlFor = "inputStatus"
                            +"상태"
                        }
                        select(classes = "form-select") {
                            name = "inputStatus"
                            form = "addModalForm"
                            id = "inputStatus"
                            option { +"사용가능" } // consider change to generate list from Status enum class
                            option { +"사용불가" }
                            option { +"폐기예정" }
                        }
                    }
                    /*insert(InputTemplate("inputStatus", "사용가능")) {
                        inputLabel { +"상태" }
                    }*/
                    insert(InputTemplate("inputMemo", "비고", inputRequired = false)) {
                        inputLabel { +"비고" }
                    }
                    div {
                        input(type = InputType.hidden, name = "inputIndex") {
                            value = index.toString()
                            id = "inputIndex"
                        }
                    }
                    div {
                        input(type = InputType.hidden, name = "inputId") {
                            //value = dbId.toString()
                            id = "inputId"
                        }
                    }
                }
            }
            modalButton {
                button(type = ButtonType.button, classes = "btn btn-danger") { // Show deleteConfimModal button
                    id = "addModalDeleteButton"
                    attributes["data-bs-toggle"] = "modal"
                    attributes["data-bs-target"] = "#deleteConfirmModal"
                    +"삭제"
                }
                button(type = ButtonType.button, classes = "btn btn-info") {
                    id = "addModalPrintButton"
                    +"인쇄"
                }
                button(type = ButtonType.button, classes = "btn btn-primary") {
                    id = "addModalSaveButton"
                    +"저장"
                }
            }
        }
    }
}