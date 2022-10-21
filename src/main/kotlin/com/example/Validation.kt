package com.example

import java.time.LocalDate

class Validation {
    companion object {
        fun validateEquipment(equip: Equipment): Boolean {
            return validateMgmtNumber(equip.mgmtNumber) and validateDate(equip.mfrDate)
        }

        private fun validateDate(date: LocalDate): Boolean {
            return true
        }

        fun validateMgmtNumber(mgmtNumber: String): Boolean {
            val chunks = mgmtNumber.chunked(2)
            if (chunks.size != 6) return false
            return when {
                chunks[0] != "EQ" -> false
                chunks[1].toIntOrNull() != 20 -> false // Not 20th or 22nd century...
                chunks[2].toIntOrNull() !in 8..25 -> false // Third chunk is Year
                chunks[3].toIntOrNull() !in 1..12 -> false // Fourth chunk is Month
                else -> true
            }
        }

    }
}