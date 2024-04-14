package edu.singaporetech.inf2007.team48.project_orion.utils

import edu.singaporetech.inf2007.team48.project_orion.consts.UserRestrictions
import edu.singaporetech.inf2007.team48.project_orion.enums.IsValidOperatorNameResult

/**
 * Perform client-side only validation on Operator's name.
 */
fun isValidOperatorName(operatorName: String): IsValidOperatorNameResult {
    if (operatorName.isEmpty()) {
        return IsValidOperatorNameResult.EMPTY_NAME
    }
    if (operatorName.length > UserRestrictions.MAX_NAME_LENGTH) {
        return IsValidOperatorNameResult.TOO_LONG
    }
    if (!operatorName.matches(Regex(UserRestrictions.ALLOWED_CHARACTERS_IN_NAME))) {
        return IsValidOperatorNameResult.SPECIAL_CHARACTERS
    }
    return IsValidOperatorNameResult.SUCCESS
}