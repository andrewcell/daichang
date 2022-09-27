package com.example.templates

import com.example.Monitor
import io.ktor.server.html.*
import kotlinx.html.FlowContent
import kotlinx.html.*
import java.text.SimpleDateFormat
import java.util.*

class MonitorTableTemplate(val monitor: Monitor) : Template<FlowContent> {
    override fun FlowContent.apply() {
        table("monitorTable1") {
            tr {
                td("monitorTitle backColor") {
                    colSpan = "4"
                    +"모니터"
                }
            }
            tr("item") {
                td("title") {
                    attributes["width"] = "35"
                    colSpan = "1"
                    +"관리번호"
                }
                td {
                    colSpan = "3"
                    +monitor.mgmtNumber
                }
            }
            tr("item") {
                td("title") {
                    colSpan = "1"
                    +"모델명"
                }
                td {
                    colSpan = "3"
                    +monitor.modelName
                }
            }
            tr("item") {
                td("title") {
                    colSpan = "1"
                    +"제조년월"
                }
                td {
                    colSpan = "3"
                    +SimpleDateFormat("yyyy. MM.").format(monitor.mfrDate)
                }
            }
            tr("item") {
                td("title") {
                    colSpan = "1"
                    +"사양"
                }
                td {
                    colSpan = "3"
                    +monitor.cable
                }
            }
            tr {
                td("title no-bottom-border") {
                    colSpan = "1"
                    +"사용처"
                }
                td("item no-bottom-border") {
                    colSpan = "3"
                    +monitor.lastUser
                }
            }
            tr {
                td("monitorTitle backColor") {
                    colSpan = "2"
                    +"상태"
                }
                td("monitorTitle no-bottom-border backColor") {
                    attributes["width"] = "50%"
                    colSpan = "2"
                    +"보관 순번"
                }
            }
            tr {
                td("item") {
                    style = "font-size: 32px; text-align: center; height: 110px"
                    colSpan = "2"
                    +monitor.status.value
                }
                td {
                    style = "font-size: 52px; text-align: center;"
                    colSpan = "2"
                    +monitor.number.toString()
                }
            }
            tr {
                td("backColor") {
                    style = "border-bottom: 2px solid black; text-align: right;"
                    colSpan = "4"
                    +"작성자 : 최승연 / 작성일자 : ${SimpleDateFormat("yyyy.MM.dd.").format(Date())}"
                }
            }
        }
    }
}