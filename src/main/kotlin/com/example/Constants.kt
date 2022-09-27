package com.example

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream
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
    }
}