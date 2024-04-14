package edu.singaporetech.inf2007.team48.project_orion.consts

object UserRestrictions {
    /**
     * The maximum length of any operator's name.
     */
    const val MAX_NAME_LENGTH = 50

    /**
     * The only allowed characters for any operator's name.
     */
    const val ALLOWED_CHARACTERS_IN_NAME = "^[a-zA-Z0-9 ]+\$"
}