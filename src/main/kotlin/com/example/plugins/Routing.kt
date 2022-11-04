package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.webjars.*

fun Application.configureRouting() {
    install(Webjars) {
        path = "/webjars" //defaults to /webjars
    }
/*
    routing {
        get("/") { // Index page
            val equipments = DatabaseHandler.getAll()
            call.respondHtmlTemplate(LayoutTemplate()) {
                content {
                    insert(IndexTemplate(equipments)) {}
                }
            }
        }
        get("/pc") { // PC lists
            call.respondHtmlTemplate(LayoutTemplate()) {
                content {
                    insert(TableTemplate(1)) {}
                }
            }
        }
        get("/laptop") { // Laptop list
            call.respondHtmlTemplate(LayoutTemplate()) {
                content {
                    insert(TableTemplate(2)) {}
                }
            }
        }
        get("/monitor") { // Monitor list
            call.respondHtmlTemplate(LayoutTemplate()) {
                content {
                    insert(TableTemplate(3)) {}
                }
            }
        }
        get("/blank") { // Blank page for test layout template
            call.respondHtmlTemplate(LayoutTemplate()) {
                content {
                    p {
                        +"It works."
                    }
                }
            }
        }

        /*@Serializable
        data class SaveRequest(
            val inputIndex: Int,
            val input
        )*/
        post("/save") { // Insert or Modify equipment. Request from AddModal
            val message: String?
            val parameters = call.receive<Map<String, String>>() // Parameter
            val index = parameters["inputIndex"]?.toIntOrNull() ?: -1
            if (index !in 1..3)
                call.respond(HttpStatusCode.BadRequest, "Invalid index or number")
            val id = parameters["inputId"]?.toIntOrNull() ?: -1
            val cabinetNumber = parameters["inputCabinetNumber"]?.toIntOrNull() ?: -1 // Cabinet number could be null if not in cabinet.
            val mgmtNumber = parameters["inputMgmtNumber"] ?: ""
            val modelName = parameters["inputModelName"] ?: ""
            val mfrDate = LocalDate.parse(parameters["inputMfrDate"], DateTimeFormatter.ISO_LOCAL_DATE)
            val serial = parameters["inputSerial"] ?: ""
            val lastUser = parameters["inputLastUser"] ?: ""
            val importDate = LocalDate.parse(parameters["inputImportDate"], DateTimeFormatter.ISO_LOCAL_DATE)
            val status = Status.findByValue(parameters["inputStatus"] ?: "") ?: Status.NOT_AVAILABLE
            val memo = parameters["inputMemo"] ?: ""
            if (index in 1..2) { // If index in PC or laptop
                val cpu = parameters["inputCPU"] ?: ""
                val hdd = parameters["inputHDD"]?.toIntOrNull() ?: 0
                val ram = parameters["inputRAM"]?.toFloatOrNull() ?: 0.0f
                val os = parameters["inputOS"] ?: ""
                val inch = if (index == 2) parameters["inputInch"]?.toFloatOrNull() else null // if index is not laptop, set inch to null for indicate as Desktop
                val pc = PC(
                    id, cabinetNumber, mgmtNumber, modelName, mfrDate, serial, cpu, hdd, ram, os, inch, lastUser, importDate, status, memo, (index == 2))
                if (!Validation.validateEquipment(pc)) return@post // If any values in newly generated equipment object, reject and cancel request
                message = DatabaseHandler.insertNewEquipment(index, pc)
            } else {
                val ratio = parameters["inputRatio"] ?: ""
                val resolution = parameters["inputResolution"] ?: ""
                val cable = parameters["inputCable"] ?: ""
                val inch = parameters["inputInch"]?.toFloatOrNull() ?: 0.0f
                val monitor = Monitor(id, cabinetNumber, mgmtNumber, modelName, mfrDate, serial, ratio, resolution,inch, cable, lastUser, importDate, status, memo)
                if (!Validation.validateEquipment(monitor)) return@post
                message = DatabaseHandler.insertNewEquipment(index, monitor)
            }
            call.respond(AjaxResponse(message == null, message)) // Respond with success, message
            //Save changes to worksheet or database
        }

        post("/delete") { // Delete equipment. Request from deleteConfirmModal
            val parameters = call.receive<Map<String, String>>()
            val index = parameters["inputIndex"]?.toIntOrNull() ?: -1
            //val id = parameters["inputId"]?.toIntOrNull() ?: -1
            val mgmtNumber = parameters["inputMgmtNumber"] ?: ""
            val lastUser = parameters["inputLastUser"] ?: ""
            val modelName = parameters["inputModelName"] ?: ""
            val message = DatabaseHandler.deleteEquipment(index, mgmtNumber, lastUser, modelName)
            call.respond(AjaxResponse(message == null, message))
        }

        /**
         * Request object for filtering
         * @property mfr Manufacturer
         * @property modelName Model name
         * @property status Status of equipment
         * @property lastUser Last user's name
         * @property query Query string
         * @property queryType Query Type (Memo, CPU or something)
         * @property index Index number of equipment type
         * @property andOr AND or OR
         */
        @Serializable
        data class FilterRequest(
            val mfr: String,
            val modelName: String,
            val status: String,
            val lastUser: String,
            val query: String,
            val queryType: String,
            val index: Int,
            val andOr: String,
        )
        post("/filter") { // To filter equipments. Return array of ids of matching equipments
            val param = call.receive<FilterRequest>()
            val originalList = DatabaseHandler.getList(param.index) // Get full original list
            /* If field is empty or not passed, In AND operator, It should make True default. To only given parameters cause result.
                In OR operator, False as a default. So if operator is AND, make default true or false if OR
                "-" is default value in input Form.
            */
            val isAnd = param.andOr == "and"
            if (originalList.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val filteredList = originalList.filter {
                var mfrMatch = false
                if (param.mfr == "-" ) {
                    mfrMatch = isAnd
                } else {
                    Constants.staticData?.mfr?.get(param.mfr)?.forEach { m -> // Get manufacturer model list and find with model name
                        if (it.modelName.startsWith(m)) {
                            mfrMatch = true
                        }
                    }
                }
                val modelMatch = if (param.modelName != "-") it.modelName == param.modelName else isAnd
                val statusMatch = if (param.status != "-") it.status.value == param.status else isAnd
                val lastUserMatch = if (param.lastUser != "-") it.lastUser == param.lastUser else isAnd
                var queryMatch = isAnd
                if (param.query.trim() !in arrayOf("", "-", "전체")) {
                    queryMatch = when (param.queryType) {
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
                    }.contains(param.query)
                    if (!queryMatch) {
                        if (param.index in 1..2) {
                            it as PC
                            queryMatch = when (param.queryType) {
                                "CPU" -> it.cpu
                                "HDD" -> it.hdd.toString()
                                "RAM" -> it.ram.toString()
                                "OS" -> it.OS
                                "인치" -> it.inch?.toString() ?: ""
                                else -> ""
                            }.contains(param.query)
                        } else {
                            it as Monitor
                            queryMatch = when (param.queryType) {
                                "화면비율" -> it.ratio
                                "해상도" -> it.resolution
                                "인치" -> it.inch.toString()
                                "케이블종류" -> it.cable
                                else -> ""
                            }.contains(param.query)
                        }
                    }
                }
                if (isAnd) {
                    modelMatch and statusMatch and lastUserMatch and queryMatch and mfrMatch
                } else {
                    modelMatch or statusMatch or lastUserMatch or queryMatch or mfrMatch
                }
            }
            call.respond(filteredList.map { it.cabinetNumber }) // return cabinet numbers
        }

        get("/print/{index}/{mgmtNumber}") { // Print single equipment. Request from AddModal
            val index = call.parameters["index"]?.toIntOrNull()
            val mgmtNumber = call.parameters["mgmtNumber"]
            if (index == null || mgmtNumber == null || !Validation.validateMgmtNumber(mgmtNumber)) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val list = DatabaseHandler.getList(index)
            val found = list.find { it.mgmtNumber == mgmtNumber } ?: return@get
            call.respondHtmlTemplate(LabelTemplate(listOf(found))) { }
            //call.respond(LabelTableTemplate())
       //     println(number + index)
        }

        post("/print") { // Multiple print selections. Request from index page
            // Print multiple Equipments at one time.
            val parameter = call.receiveParameters()
            val payload = parameter["data"] ?: "{}" // Request data should be in raw JSON string. {"pc": [1,2,3], "laptop": [1,2,3], "monitor": [1,2,3]} like this. To prevent error, Empty json body when null
            val data = Json.decodeFromString<PrintPayload>(payload) // Parse to PrintPayload
            val pc = data.pc ?: emptyList()
            val laptop = data.laptop ?: emptyList()
            val monitor = data.monitor ?: emptyList()
            arrayOf(pc, laptop, monitor).forEach { lst -> // check any invalid mgmt number is included.
                for (mgmtNumber in lst) {
                    if (!Validation.validateMgmtNumber(mgmtNumber)) return@post
                }
            }
            val targetList = DatabaseHandler.getList(1).filter { pc.contains(it.mgmtNumber) } +
                    DatabaseHandler.getList(2).filter { laptop.contains(it.mgmtNumber) } +
                    DatabaseHandler.getList(3).filter { monitor.contains(it.mgmtNumber) } // Filter every list, and merge to it.
            call.respondHtmlTemplate(LabelTemplate(targetList)) { }
        }

        /**
         * Import spreadsheet file and save it to database
         * Both ERP file, exported spreadsheet file can be used
         * @param part PartData from Multipart binary
         * @param isERP True if spreadsheet file is from ERP3
         * @return Result of import job as AjaxResponse
         */
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

        post("/import") { // import spreadsheet file
            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                call.respond(importData(part)) //import Data will return AjaxResponse object directly
            }
            call.respond(Constants.badRequest)
        }

        post("/erp") { // import ERP Data
            val multipart = call.receiveMultipart()
            multipart.forEachPart {
                call.respond(importData(it, true)) // Re-use import spreadsheet
            }
            call.respond(Constants.badRequest) // if received data is not part of file.
        }

        delete("/erp") { // Delete ERP data. Request from admin page
            val result = DatabaseHandler.cleanERPData()
            val success = result == null // If message is null, it's success
            call.respond(AjaxResponse(success, if (success) "ERP data has been cleaned." else result))
        }

        post("/autofill") { // Autofill by management number. Request from AddModal, Enter key hit in management number field.
            val parameter = call.receiveParameters()
            val mgmtNumber = parameter["query"] // only get raw mgmtnumber as payload
            if (mgmtNumber == null || mgmtNumber == "") {
                call.respond(HttpStatusCode.BadRequest, AjaxResponse( false, "Invalid mgmtNumber"))
                return@post
            }
            if (!Validation.validateMgmtNumber(mgmtNumber ?: "")) { // Validate mgmtnumber, If its invalid, return bad request
                call.respond(HttpStatusCode.BadRequest, AjaxResponse(false, "올바르지 않은 관리번호입니다."))
                return@post
            }
            val requestIndex = when (call.request.header(HttpHeaders.Referrer)?.split("/")?.last()) { // Determine index by Referrer url
                "pc" -> 1
                "laptop" -> 2
                "monitor" -> 3
                else -> -1
            }
            val erpData = DatabaseHandler.getERPDataByMgmtNumber(mgmtNumber, requestIndex)
            if (erpData == null) {
                call.respond(HttpStatusCode.BadRequest, AjaxResponse( false, "관리 번호를 찾을 수 없습니다."))
                return@post
            }
            if (erpData.index in 1..2) { // Process variables for easy-to-use.
                val cpu = DatabaseHandler.getCPUByModelName(erpData.modelName) ?: erpData.var1 // try to get proper cpu name instead of original data. Original data does not contain cpu model name.
                erpData.var1 = cpu
                var hdd = erpData.var3.trim()  // ERPData stores data as String with capacity units. Remove unit letters and convert to int type
                val unit = hdd.takeLast(2) // Get unit letters whether GB or TB
                hdd = hdd.dropLast(2) // Remove unit letters
                val hddValue = if (unit == "TB") { // If unit is TB, Need to multiply 1024
                    (hdd.replace(".0", "").toIntOrNull() ?: 0) * 1024
                } else hdd.toIntOrNull() ?: 0  // hddValue is converted hdd value
                erpData.var3 = if (hddValue == 0) "256" else hddValue.toString() // var3 == HDD
                erpData.var2 = erpData.var2.trim().dropLast(2) // var2 == RAM. Also drop unit letters
                erpData.var4 = Constants.getInchByLaptopModelName(erpData.modelName).toString() // Additionally, Send inch when it requested from laptop page.
            } else if (erpData.index == 3) { // monitor
                val inch = erpData.var2.take(2).toIntOrNull() ?: 24
                erpData.var1 = erpData.var1.replace(Regex("[\uAC00-\uD7A3]"), "").trim() // Remove korean characters in cable value
                erpData.var2 = inch.toString() // var2 is String so need to convert to string
                erpData.var4 = when (inch) { // Guess resolution by inch
                    in 17..19 -> "1280x1024"
                    in 24..32 -> "1920x1080"
                    else -> "1920x1080"
                }
                erpData.var5 = when (inch) { // Guess ratio by inch
                    in 17..19 -> "4:3"
                    in 24..32 -> "16:9"
                    else -> "16:9"
                }
            }
            call.respond(erpData)
        }

        get("/export") { // Export to spreadsheet file
            if (WorkSheetHandler.lock) {
                call.respond("Server is busy. Try again later.")
            }
            WorkSheetHandler.lock = true
            val stream = WorkSheetHandler.export() // Wait for output stream
            if (stream == null) { // If stream is null, There are errors found during export.
                call.respond(HttpStatusCode.InternalServerError)
                return@get
            }
            WorkSheetHandler.lock = false
            call.respondBytes(stream.toByteArray(), contentType = ContentType.parse("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) // when Outputstream arrived, send it to download in browser
        }

        get("/admin") { // Admin page
            call.respondHtmlTemplate(LayoutTemplate()) {
                content {
                    insert(ControlPanelTemplate()) {}
                }
            }
        }

        post("/rebuild") { // Destroy and rebuild cache. Request from admin page
            DatabaseHandler.isBusy = true
            val result = DatabaseHandler.rebuild() // Wait for result
            DatabaseHandler.isBusy = false
            call.respond(AjaxResponse(result == null, result))
        }

        post("/cabinetNumber") { // Get empty cabinet number. Return empty cabinet number from list
            val parameter = call.receive<Map<String, String>>()
            val index = parameter["index"]?.toIntOrNull()
            if (index != null) {
                call.respond(AjaxResponse(true, DatabaseHandler.getEmptyCabinetNumber(index).toString()))
            } else {
                call.respond(AjaxResponse(false, "Invalid index."))
            }
        }

        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
    }*/
}
