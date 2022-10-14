package com.example

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.nio.charset.Charset

class Constants {
    companion object {
        const val worksheetPath = "d:/!/전산장비_PC_노트북_모니터.xlsx"
        private val colsStart = listOf("순번", "관리번호", "모델명", "제조일자", "S/N")
        private val colsEnd = listOf("최종사용자", "입고일자", "상태", "비고")
        val colsPC = colsStart + listOf("CPU", "HDD", "RAM", "OS") + colsEnd
        val colsLaptop = colsStart + listOf("CPU", "HDD", "RAM", "OS", "인치") + colsEnd
        val colsMonitor = colsStart + listOf("화면비율", "해상도", "인치", "케이블종류") + colsEnd
        val staticData = this::class.java.classLoader.getResource("data.json")
            ?.let { Json.decodeFromString<StaticData>(it.readText(Charset.defaultCharset())) }
        val badRequest = AjaxResponse(false, "잘못된 요청입니다.")

        /**
         * Return inch (15.6 or 12.5 or 14.0 by laptop) for autofill routing.
         * Only supports Thinkpad models. Other model will return as 15.6 roughly.
         *
         * @param modelName Model name of Laptop. If it not starts with "THINKPAD", will not handle.
         * @return Inch value as Double.
         */
        fun getInchByLaptopModelName(modelName: String): Double {
            if (!modelName.uppercase().startsWith("THINKPAD")) return 15.6 //
            return when (modelName.replace("THINKPAD", "").replace("GEN", "").trim().substring(0, 3)) {
                "X26", "X27", "X28", "X29" -> 12.5
                "E48", "E49", "T14" -> 14.0
                "E58", "E59", "T57", "T58", "T59", "T15" -> 15.6
                else -> 15.6
            }
        }
    }
}