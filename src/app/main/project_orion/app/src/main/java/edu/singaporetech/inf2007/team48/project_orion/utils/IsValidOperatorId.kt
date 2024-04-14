package edu.singaporetech.inf2007.team48.project_orion.utils

import edu.singaporetech.inf2007.team48.project_orion.enums.IsValidOperatorIdResult

/**
 * Perform Client-side only checks to see if its a valid Operator ID.
 *
 * @return Status result of the check.
 */
fun isValidOperatorId(operatorId: String): IsValidOperatorIdResult {
    if (operatorId.isBlank()) {
        return IsValidOperatorIdResult.EMPTY_ID
    }
    if (operatorId.toIntOrNull() == null) {
        return IsValidOperatorIdResult.NOT_NUMBERS
    }
    return IsValidOperatorIdResult.SUCCESS
}