package com.example.plugins

import com.example.*
import com.example.database.DatabaseHandler
import com.example.templates.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.webjars.*
import kotlinx.html.p
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun Application.configureRouting() {
    install(Webjars) {
        path = "/webjars" //defaults to /webjars
    }
    routing {
        get("/") {
            val equipments = DatabaseHandler.getAll()
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
            val id = parameters["inputId"]?.toIntOrNull() ?: -1
            val cabinetNumber = parameters["inputCabinetNumber"]?.toIntOrNull() ?: -1
            val mgmtNumber = parameters["inputMgmtNumber"] ?: ""
            val modelName = parameters["inputModelName"] ?: ""
            val mfrDate = LocalDate.parse(parameters["inputMfrDate"], DateTimeFormatter.ISO_LOCAL_DATE)
            val serial = parameters["inputSerial"] ?: ""
            val lastUser = parameters["inputLastUser"] ?: ""
            val importDate = LocalDate.parse(parameters["inputImportDate"], DateTimeFormatter.ISO_LOCAL_DATE)
            val status = Status.findByValue(parameters["inputStatus"] ?: "") ?: Status.NOT_AVAILABLE
            val memo = parameters["inputMemo"] ?: ""
            if (index in 1..2) {
                val cpu = parameters["inputCPU"] ?: ""
                val hdd = parameters["inputHDD"]?.toIntOrNull() ?: 0
                val ram = parameters["inputRAM"]?.toFloatOrNull() ?: 0.0f
                val os = parameters["inputOS"] ?: ""
                val inch = if (index == 2) parameters["inputInch"]?.toFloatOrNull() else null
                DatabaseHandler.insertNewEquipment(
                    index, PC(
                        id, cabinetNumber, mgmtNumber, modelName, mfrDate, serial, cpu, hdd, ram, os, inch, lastUser, importDate, status, memo, (index == 2)
                    )
                )
            } else {
                val ratio = parameters["inputRatio"] ?: ""
                val resolution = parameters["inputResolution"] ?: ""
                val cable = parameters["inputCable"] ?: ""
                val inch = parameters["inputInch"]?.toFloatOrNull() ?: 0.0f
                DatabaseHandler.insertNewEquipment(
                    index, Monitor(
                        id, cabinetNumber, mgmtNumber, modelName, mfrDate, serial, ratio, resolution,inch, cable, lastUser, importDate, status, memo
                    )
                )
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
            //val id = parameters["inputId"]?.toIntOrNull() ?: -1
            val mgmtNumber = parameters["inputMgmtNumber"] ?: ""
            val lastUser = parameters["inputLastUser"] ?: ""
            val modelName = parameters["inputModelName"] ?: ""
            DatabaseHandler.deleteEquipment(index, mgmtNumber, lastUser, modelName)
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
            val originalList = DatabaseHandler.getList(index)
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
                        "순번" -> it.cabinetNumber.toString()
                        "관리번호" -> it.mgmtNumber
                        "모델명" -> it.modelName
                        "제조일자" -> it.mfrDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        "S/N" -> it.serialNumber
                        "최종사용자" -> it.lastUser
                        "입고일자" -> it.importDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
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
            call.respond(Json.encodeToString(filteredList.map { it.cabinetNumber }))
        }

        get("/print/{index}/{mgmtNumber}") {
            val index = call.parameters["index"]?.toIntOrNull()
            val mgmtNumber = call.parameters["mgmtNumber"]
            if (index == null || mgmtNumber == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val list = DatabaseHandler.getList(index)
            val found = list.find { it.mgmtNumber == mgmtNumber } ?: return@get
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
            val targetList = DatabaseHandler.getList(1).filter { pc.contains(it.mgmtNumber) } +
                    DatabaseHandler.getList(2).filter { laptop.contains(it.mgmtNumber) } +
                    DatabaseHandler.getList(3).filter { monitor.contains(it.mgmtNumber) }
            call.respondHtmlTemplate(LabelTemplate(targetList)) { }
        }

        fun importData(part: PartData, isERP: Boolean = false): AjaxResponse {
            if (part is PartData.FileItem) {
                part.streamProvider().use {
                    if (DatabaseHandler.isBusy) {
                        return AjaxResponse(false, "다른 작업이 진행중입니다. 잠시후 다시 시도해주세요.")
                    }
                    DatabaseHandler.isBusy = true
                    val result = if (isERP) WorkSheetHandler.importERPData(it) else WorkSheetHandler.import(it)
                    DatabaseHandler.isBusy = false
                    val message = if (result == null) {
                        "들여오기 성공"
                    } else "에러가 발생하였습니다: $result"
                    return AjaxResponse(result == null, message)
                }
            }
            part.dispose()
            return AjaxResponse(false, "알 수 없는 오류.")
        }

        post("/import") {
            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                call.respond(importData(part))
            }
            call.respond(Constants.badRequest)
        }

        post("/erp") {
            val multipart = call.receiveMultipart()
            multipart.forEachPart {
                call.respond(importData(it, true))
            }
            call.respond(Constants.badRequest)
        }

        post("/autofill") {
            val parameter = call.receiveParameters()
            val mgmtNumber = parameter["query"]
            if (mgmtNumber == null || mgmtNumber == "") return@post
            val requestIndex = when (call.request.header(HttpHeaders.Referrer)?.split("/")?.last()) {
                "pc" -> 1
                "laptop" -> 2
                "monitor" -> 3
                else -> -1
            }
            val erpData = DatabaseHandler.getERPDataByMgmtNumber(mgmtNumber, requestIndex)
            if (erpData == null) {
                call.respond(status = HttpStatusCode.BadRequest, "Not found")
                return@post
            }
            if (erpData.index in 1..2) {
                val cpu = DatabaseHandler.getCPUByModelName(erpData.modelName) ?: erpData.var1
                erpData.var1 = cpu
                var hdd = erpData.var3.trim()
                val unit = hdd.takeLast(2)
                hdd = hdd.dropLast(2)
                val hddValue = if (unit == "TB") {
                    (hdd.replace(".0", "").toIntOrNull() ?: 0) * 1024
                } else hdd.toIntOrNull() ?: 0
                erpData.var3 = if (hddValue == 0) "256" else hddValue.toString()
                erpData.var2 = erpData.var2.trim().dropLast(2)
            } else if (erpData.index == 3) {
                val inch = erpData.var2.take(2).toIntOrNull() ?: 24
                erpData.var1 = erpData.var1.replace(Regex("[\uAC00-\uD7A3]"), "").trim()
                erpData.var2 = inch.toString()
                erpData.var4 = when (inch) {
                    in 17..19 -> "1280x1024"
                    in 24..32 -> "1920x1080"
                    else -> "1920x1080"
                }
                erpData.var5 = when (inch) {
                    in 17..19 -> "4:3"
                    in 24..32 -> "16:9"
                    else -> "16:9"
                }
            }
            call.respond(erpData)
        }

        get("/export") {
            if (WorkSheetHandler.lock) {
                call.respond("Server is busy. Try again later.")
            }
            WorkSheetHandler.lock = true
            val stream = WorkSheetHandler.export()
            if (stream == null) {
                call.respond(HttpStatusCode.InternalServerError)
                return@get
            }
            WorkSheetHandler.lock = false
            call.respondBytes(stream.toByteArray(), contentType = ContentType.parse("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        }

        get("/admin") {
            call.respondHtmlTemplate(LayoutTemplate()) {
                content {
                    insert(ControlPanelTemplate()) {}
                }
            }
        }

        post("/rebuild") {
            DatabaseHandler.isBusy = true
            val result = DatabaseHandler.rebuild()
            DatabaseHandler.isBusy = false
            call.respond(AjaxResponse(result == null, result))
        }

        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
    }
}
