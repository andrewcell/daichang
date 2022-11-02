package com.example

import java.time.LocalDate

/**
 * Validate which is acceptable value is passed.
 * @author Seungyeon Choi {@literal <git@vxz.me>}
 */
class Validation {
    companion object {
        /**
         * Validate values equipment has
         * @param equip Equipment to validate
         * @return Is it valid or invalid
         */
        fun validateEquipment(equip: Equipment): Boolean {
            return validateMgmtNumber(equip.mgmtNumber) and validateDate(equip.mfrDate)
        }

        /**
         * Check mfrDate and importDate is valid.
         * NOT-IMPLEMENTED return true for now.
         * @return Is it valid or invalid
         */
        private fun validateDate(date: LocalDate): Boolean {
            return true
        }

        /**
         * Check management number is valid.
         * mgmtNumber have  rules to generate new number:
         * Every mgmtNumber start with "EQ"
         * After "EQ", 4 letters are year of date of number generated. IF number is generated at December 31st, 2022, is 2022.
         * After "EQyyyy", 2 letters are month. These number must be in 1 to 12.
         * After "EQyyyyMM", is just number
         * @param mgmtNumber mgmtNumber to validate
         * @return Is it valid or invalid
         */
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