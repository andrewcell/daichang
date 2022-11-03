package com.example

import com.example.database.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import java.time.LocalDate

object TestPreparation {
    val database = Database.connect("jdbc:sqlite:file:test?mode=memory&cache=shared", "org.sqlite.JDBC")

    init {
        SchemaUtils.createMissingTablesAndColumns(EquipmentTable, ERPDataTable, LogTable, MonitorTable, PCTable)
    }

    val testPC = listOf(
        PC(-1, 1, "EQ2005090001", "B80GV", LocalDate.of(2020, 12, 1), "202TSEX6X4444",
        "Intel(R) Core(TM) i5-10400 CPU @ 2.90GHz", 256, 8.0f, "Win 10", null, "마이클", LocalDate.of(2022, 6, 25), Status.AVAILABLE, ""),
        PC(-1, 2, "EQ2015120011", "B80FV", LocalDate.of(2020, 12, 1), "202TSEX6X4423",
            "Intel(R) Core(TM) i5-9500 CPU @ 3.00GHz", 256, 16.0f, "Win 10", null, "트레버", LocalDate.of(2021, 4, 3), Status.AVAILABLE, ""),
        PC(-1, 53, "EQ2021130051", "B80GV", LocalDate.of(2021, 6, 25), "203TSEX624444",
            "Intel(R) Core(TM) i5-10400 CPU @ 2.90GHz", 512, 8.0f, "Win 10", null, "프랭클린", LocalDate.of(2021, 8, 15), Status.NOT_AVAILABLE, "MEMO!"),
        PC(-1, 43, "EQ2022100001", "DB400T3A", LocalDate.of(2014, 5, 5), "F0CK69BJ0974",
            "Intel(R) Pentium(R) CPU G620 @ 2.60Ghz", 500, 4.0f, "Win 10", null, "아서", LocalDate.of(2022, 7, 4), Status.TO_BE_DISPOSE, ""),
        PC(-1, 128, "EQ2020110001", "DB400T6B", LocalDate.of(2017, 11, 11), "PKJS69BJ0974",
            "Intel(R) Core(TM) i5-6500 CPU @ 3.20GHz", 1024, 8.0f, "Win 10", null, "존", LocalDate.of(2022, 10, 30), Status.AVAILABLE, ""),
    )

    val testLaptop = listOf(
        PC(-1, 56, "EQ2005090001", "THINKPAD T15 GEN 2", LocalDate.of(2021, 12, 1), "202TSEX6X5544",
            "Intel(R) Core(TM) i5-1135G7 CPU @ 2.40GHz", 256, 8.0f, "Win 10", null, "마이클", LocalDate.of(2022, 6, 25), Status.AVAILABLE, ""),
        PC(-1, 123, "EQ2015120021", "THINKPAD T15 GEN1", LocalDate.of(2019, 12, 1), "202TSEX6X1123",
            "Intel(R) Core(TM) i5-10210U CPU @ 1.60GHz", 256, 32.0f, "Win 10", null, "트레버", LocalDate.of(2021, 4, 3), Status.AVAILABLE, ""),
        PC(-1, 1, "EQ2021050061", "THINKPAD T580", LocalDate.of(2021, 6, 25), "203TSEX62466",
            "Intel(R) Core(TM) i5-8250U CPU @ 1.6GHz", 512, 8.0f, "Win 10", null, "프랭클린", LocalDate.of(2021, 8, 15), Status.NOT_AVAILABLE, "MEMO!"),
        PC(-1, 2, "EQ2022100031", "THINKPAD X260", LocalDate.of(2014, 5, 5), "F0CK69BJ0975",
            "Intel(R) Core(TM) i5-7200U CPU @ 2.50GHzz", 500, 8.0f, "Win 10", null, "아서", LocalDate.of(2022, 7, 4), Status.TO_BE_DISPOSE, ""),
        PC(-1, 3, "EQ2020110041", "THINKPAD T14 GEN 1", LocalDate.of(2017, 11, 11), "PKJS69BJ1974",
            "Intel(R) Core(TM) i5-1135G7 CPU @ 2.40GHz", 1024, 8.0f, "Win 10", null, "존", LocalDate.of(2022, 10, 30), Status.AVAILABLE, ""),
    )

    val testMonitor = listOf(
        Monitor(-1, 1, "EQ2005100001", "LS19C45KMRSKR", LocalDate.of(2019, 12, 1), "202TSEX6X4444",
            "16:9", "1920x1080", 24.0f, "HDMI/RGB", "마이클", LocalDate.of(2022, 6, 25), Status.AVAILABLE, ""),
        Monitor(-1, 2, "EQ2015010011", "LS19E45KBRSKR", LocalDate.of(2022, 12, 1), "202TSEX6X4423",
            "4:3", "1280x1024", 19.0f, "DVI/RGB", "트레버", LocalDate.of(2021, 4, 4), Status.AVAILABLE, ""),
        Monitor(-1, 3, "EQ2021020051", "24EN430H", LocalDate.of(2021, 6, 25), "203TSEX324444",
            "16:9", "1920x1080", 24.0f, "HDMI/RGB", "프랭클린", LocalDate.of(2021, 8, 15), Status.NOT_AVAILABLE, "MEMO!"),
        Monitor(-1, 4, "EQ2022110001", "24MK430H", LocalDate.of(2014, 5, 5), "F0CK79BJ0974",
            "16:9", "1920x1080", 24.0f, "HDMI/RGB", "아서", LocalDate.of(2022, 7, 4), Status.TO_BE_DISPOSE, ""),
        Monitor(-1, 5, "EQ2020120001", "LS19E45KBRSKR", LocalDate.of(2017, 11, 11), "PKJ59BJ0974",
            "4:3", "1280x1024", 19.0f, "HDMI/RGB", "존", LocalDate.of(2022, 10, 30), Status.AVAILABLE, ""),
    )
}