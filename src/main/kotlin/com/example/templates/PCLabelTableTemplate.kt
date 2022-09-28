package com.example.templates

import com.example.PC
import io.ktor.server.html.*
import kotlinx.html.*
import java.text.SimpleDateFormat
import java.util.*

class PCLabelTableTemplate(val pc: PC) : Template<FlowContent> {
    override fun FlowContent.apply() {
        table {
            attributes["border"] = "1"
            attributes["width"] = "320"
            tr {
                td("title backColor") {
                    colSpan = "3"
                    +"하드웨어 리스트"
                }
            }
            tr {
                td("title") { +"관리번호" }
                td { +pc.mgmtNumber }
                td("number") {
                    rowSpan = "4"
                    attributes["width"] = "80"
                    attributes["align"] = "center"
                    +pc.cabinetNumber.toString()
                }
            }
            tr {
                td("title") { +"모델명" }
                td { +pc.modelName }
            }
            tr {
                td("title") { +"CPU" }
                td { +pc.cpu.replace("Intel(R)", "").replace("(TM)", "").split("CPU")[0].trimIndent() }
            }
            tr {
                td("title") { +"RAM" }
                td { +(pc.ram.toString() + " GB") }
            }
            tr {
                td("title") { +"OS" }
                td {
                    colSpan = "2"
                    +"Windows 10"
                }
            }
            tr {
                td("title") {
                    rowSpan = "2"
                    +"일반사무용"
                }
                td {
                    colSpan = "2"
                    +"  □  Office Standard (      )"
                }
            }
            tr {
                td {
                    colSpan = "2"
                    +"  □  한글 (    )"
                }
            }
            tr {
                td("title") { +"백신" }
                td {
                    colSpan = "2"
                    +"  □  AhnLab V3 Security 9.0"
                }
            }
            tr {
                td("title") { +"상태/등급" }
                td("status") {
                    colSpan = "2"
                    +pc.status.value
                }
            }
            tr {
                td("title") { +"저장장치" }
                td {
                    colSpan = "2"
                    +(pc.hdd.toString() + " GB")
                }
            }
            tr {
                td("title") { +"컴포넌트" }
                td {
                    colSpan = "2"
                    +" □ ERP  □ 포탈  □ 통합회계"
                }
            }
            tr {
                td("title titleBorder backColor") {
                    colSpan = "3"
                    style = "border-top: 3px solid"
                    +"고장내용 및 기타사항"
                }
            }
            tr {
                td("memo") {
                    attributes["height"] = "35"
                    colSpan = "3"
                    +pc.memo
                }
            }
            tr {
                td("title titleBorder backColor") {
                    colSpan = "3"
                    attributes["width"] = "160"
                    style = "text-align: right;"
                    +("작성자: 최승연 / 작성일자: " + SimpleDateFormat("yyyy.MM.dd.").format(Date()))
                }
            }
        }
    }
}