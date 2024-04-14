package edu.singaporetech.inf2007.team48.project_orion.enums

enum class IsValidOperatorIdResult(val code: Int) {
    SUCCESS(0),
    EMPTY_ID(1),
    NOT_NUMBERS(2);

    companion object {
        private val map = entries.associateBy(IsValidOperatorIdResult::code)
        fun fromInt(type: Int) = map[type]
    }
}
