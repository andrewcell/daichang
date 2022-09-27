package com.example.plugins

import com.example.*
import com.example.templates.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.webjars.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import kotlinx.html.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat

fun Application.configureRouting() {
    install(Webjars) {
        path = "/webjars" //defaults to /webjars
    }
    routing {
        get("/") {
            val equipments = WorkSheetHandler.getAll()
            call.respondHtmlTemplate(LayoutTemplate()) {
                content {
                    insert(IndexTemplate(equipments)) {}
                }
            }
        }
        get("/pc") {
            call.respondHtmlTemplate(LayoutTemplate()) {
                content {
                    insert(TableTemplate(1)) {}
                }
            }
        }
        get("/laptop") {
            call.respondHtmlTemplate(LayoutTemplate()) {
                content {
                    insert(TableTemplate(2)) {}
                }
            }
        }
        get("/monitor") {
            call.respondHtmlTemplate(LayoutTemplate()) {
                content {
                    insert(TableTemplate(3)) {}
                }
            }
        }
        get("/blank") {
            call.respondHtmlTemplate(LayoutTemplate()) {
                content {
                    p {
                        +"It works."
                    }
                }
            }
        }

        post("/save") {
            val parameters = call.receiveParameters()
            val index = parameters["inputIndex"]?.toIntOrNull() ?: -1
            if (index !in 1..3)
                call.respond(HttpStatusCode.BadRequest, "Invalid index or number")
            val number = parameters["inputNumber"]?.toIntOrNull() ?: -1
            val mgmtNumber = parameters["inputMgmtNumber"] ?: ""
            val modelName = parameters["inputModelName"] ?: ""
            val mfrDate = SimpleDateFormat("yyyy-MM-dd").parse(parameters["inputMfrDate"])
            val serial = parameters["inputSerial"] ?: ""
            val lastUser = parameters["inputLastUser"] ?: ""
            val importDate = SimpleDateFormat("yyyy-MM-dd").parse(parameters["inputImportDate"])
            val status = Status.findByValue(parameters["inputStatus"] ?: "") ?: Status.NOT_AVAILABLE
            val memo = parameters["inputMemo"] ?: ""
            if (index in 1..2) {
                val cpu = parameters["inputCPU"] ?: ""
                val hdd = parameters["inputHDD"]?.toShortOrNull() ?: 0
                val ram = parameters["inputRAM"]?.toFloatOrNull() ?: 0.0f
                val OS = parameters["inputOS"] ?: ""
                val inch = if (index == 2) parameters["inputInch"]?.toFloatOrNull() else null
                WorkSheetHandler.insertNewEquipment(index, PC(
                    number, mgmtNumber, modelName, mfrDate, serial, cpu, hdd, ram, OS, inch, lastUser, importDate, status, memo, (index == 2)
                ))
            } else {
                val ratio = parameters["inputRatio"] ?: ""
                val resolution = parameters["inputResolution"] ?: ""
                val cable = parameters["inputCable"] ?: ""
                val inch = parameters["inputInch"]?.toFloatOrNull() ?: 0.0f
                WorkSheetHandler.insertNewEquipment(index, Monitor(
                    number, mgmtNumber, modelName, mfrDate, serial, ratio, resolution,inch, cable, lastUser, importDate, status, memo
                ))
            }
            call.respondRedirect(when (index) {
                1 -> "/pc"
                2 -> "/laptop"
                3 -> "/monitor"
                else -> "/"
            })
            //Save changes to worksheet or database
        }

        post("/delete") {
            val parameters = call.receiveParameters()
            val index = parameters["inputIndex"]?.toIntOrNull() ?: -1
            val number = parameters["inputNumber"]?.toIntOrNull() ?: -1
            val mgmtNumber = parameters["inputMgmtNumber"] ?: ""
            val lastUser = parameters["inputLastUser"] ?: ""
            val modelName = parameters["inputModelName"] ?: ""
            WorkSheetHandler.deleteEquipment(index, number, mgmtNumber, lastUser, modelName)
            call.respondRedirect(when (index) {
                1 -> "/pc"
                2 -> "/laptop"
                3 -> "/monitor"
                else -> "/"
            })

        }

        post("/filter") {
            val parameters = call.receiveParameters()
            val mfr = parameters["mfr"] ?: ""
            val modelName = parameters["modelName"] ?: ""
            val status = parameters["status"] ?: ""
            val lastUser = parameters["lastUser"] ?: ""
            val query = parameters["query"] ?: ""
            val queryType = parameters["queryType"] ?: ""
            val index = parameters["index"]?.toIntOrNull() ?: -1
            val isAnd = parameters["andOr"] == "and"
            val originalList = WorkSheetHandler.getList(index)
            if (originalList.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val filteredList = originalList.filter {
                var mfrMatch = false
                if (mfr == "-" ) {
                    mfrMatch = isAnd
                } else {
                    Constants.staticData?.mfr?.get(mfr)?.forEach { m ->
                        if (it.modelName.startsWith(m)) {
                            mfrMatch = true
                        }
                    }
                }
                val modelMatch = if (modelName != "-") it.modelName == modelName else isAnd
                val statusMatch = if (status != "-") it.status.value == status else isAnd
                val lastUserMatch = if (lastUser != "-") it.lastUser == lastUser else isAnd
                var queryMatch = isAnd
                if (query.trim() !in arrayOf("", "-", "전체")) {
                    queryMatch = when (queryType) {
                        "순번" -> it.number.toString()
                        "관리번호" -> it.mgmtNumber
                        "모델명" -> it.modelName
                        "제조일자" -> SimpleDateFormat("yyyy-MM-dd").format(it.mfrDate)
                        "S/N" -> it.serialNumber
                        "최종사용자" -> it.lastUser
                        "입고일자" -> SimpleDateFormat("yyyy-MM-dd").format(it.importDate)
                        "상태" -> it.status.value
                        "비고" -> it.memo
                        else -> ""
                    }.contains(query)
                    if (!queryMatch) {
                        if (index in 1..2) {
                            it as PC
                            queryMatch = when (queryType) {
                                "CPU" -> it.cpu
                                "HDD" -> it.hdd.toString()
                                "RAM" -> it.ram.toString()
                                "OS" -> it.OS
                                "인치" -> it.inch?.toString() ?: ""
                                else -> ""
                            }.contains(query)
                        } else {
                            it as Monitor
                            queryMatch = when (queryType) {
                                "화면비율" -> it.ratio
                                "해상도" -> it.resolution
                                "인치" -> it.inch.toString()
                                "케이블종류" -> it.cable
                                else -> ""
                            }.contains(query)
                        }
                    }
                }
                if (isAnd) {
                    modelMatch and statusMatch and lastUserMatch and queryMatch and mfrMatch
                } else {
                    modelMatch or statusMatch or lastUserMatch or queryMatch or mfrMatch
                }
            }
            call.respond(Json.encodeToString(filteredList.map { it.number }))
        }

        get("/print/{index}/{number}") {
            val index = call.parameters["index"]?.toIntOrNull()
            val number = call.parameters["number"]?.toIntOrNull()
            if (index == null || number == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val list = WorkSheetHandler.getList(index)
            val found = list.find { it.number == number } ?: return@get
            call.respondHtmlTemplate(LabelTemplate(listOf(found))) { }
            //call.respond(LabelTableTemplate())
       //     println(number + index)
        }

        post("/print") {
            // Print multiple Equipments at one time.
            val parameter = call.receiveParameters()
            val payload = parameter["data"] ?: "{}"
            val data = Json.decodeFromString<PrintPayLoad>(payload)
            val pc = data.pc ?: emptyList()
            val laptop = data.laptop ?: emptyList()
            val monitor = data.monitor ?: emptyList()
            val targetList = WorkSheetHandler.getList(1).filter { pc.contains(it.number) } +
                    WorkSheetHandler.getList(2).filter { laptop.contains(it.number) } +
                    WorkSheetHandler.getList(3).filter { monitor.contains(it.number) }
            call.respondHtmlTemplate(LabelTemplate(targetList)) { }
        }

        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
    }
}
