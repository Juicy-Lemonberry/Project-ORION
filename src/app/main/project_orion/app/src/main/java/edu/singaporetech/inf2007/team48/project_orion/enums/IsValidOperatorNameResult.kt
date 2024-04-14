package edu.singaporetech.inf2007.team48.project_orion.enums

enum class IsValidOperatorNameResult(val code: Int) {
    SUCCESS(0),
    EMPTY_NAME(1),
    TOO_LONG(2),
    SPECIAL_CHARACTERS(3);

    companion object {
        private val map = entries.associateBy(IsValidOperatorNameResult::code)
        fun fromInt(type: Int) = map[type]
    }
}
