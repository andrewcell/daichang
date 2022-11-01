package com.example

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.nio.charset.Charset

/**
 * Constant values that use widely in project.
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
class Constants {
    /**
     * @property colsPC columns for list of PCs table
     * @property colsLaptop columns for list of laptops table
     * @property colsMonitor columns for list of monitors table
     * @property staticData data like Manufacturers, Model names read from data.json in resources folder
     * @property badRequest Commonly used bad request AjaxResponse for Routing
     */
    companion object {
        private val colsStart = listOf(
            "순번" to "cabinetNumber", "관리번호" to "mgmtNumber",
            "모델명" to "modelName", "제조일자" to "mfrDate", "S/N" to "serialNumber"
        )
        private val colsEnd = listOf(
            "최종사용자" to "lastUser", "입고일자" to "importDate",
            "상태" to "status", "비고" to "memo"
        )
        val colsPC = colsStart + listOf("CPU" to "CPU", "HDD" to "HDD", "RAM" to "RAM", "OS" to "OS") + colsEnd
        val colsLaptop = colsStart + listOf("CPU" to "CPU", "HDD" to "HDD", "RAM" to "RAM", "OS" to "OS", "인치" to "inch") + colsEnd
        val colsMonitor = colsStart + listOf("화면비율" to "ratio", "해상도" to "resolution", "인치" to "inch", "케이블종류" to "cable") + colsEnd

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