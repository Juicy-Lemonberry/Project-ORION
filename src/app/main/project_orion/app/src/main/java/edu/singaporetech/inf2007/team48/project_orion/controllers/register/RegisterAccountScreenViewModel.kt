package edu.singaporetech.inf2007.team48.project_orion.controllers.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.singaporetech.inf2007.team48.project_orion.consts.UserRestrictions
import edu.singaporetech.inf2007.team48.project_orion.enums.IsValidOperatorIdResult
import edu.singaporetech.inf2007.team48.project_orion.enums.IsValidOperatorNameResult
import edu.singaporetech.inf2007.team48.project_orion.models.api.post.OrionApiPostUser
import edu.singaporetech.inf2007.team48.project_orion.services.api.OrionApiClient.orionApi
import edu.singaporetech.inf2007.team48.project_orion.utils.isValidOperatorId
import edu.singaporetech.inf2007.team48.project_orion.utils.isValidOperatorName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class RegisterAccountScreenViewModel : ViewModel() {
    // StateFlows for UI elements' states and content
    private val _titleText = MutableStateFlow("")
    private val _helpText = MutableStateFlow("")
    private val _operatorId = MutableStateFlow("")
    private val _operatorName = MutableStateFlow("")
    private val _operatorIdTextFieldLabel = MutableStateFlow("Operator ID")
    private val _operatorNameTextFieldLabel = MutableStateFlow("Operator Name")
    private val _CreateAccountButtonLabel = MutableStateFlow("Create Account")
    private val _isOperatorIdTextFieldError = MutableStateFlow(false)
    private val _isOperatorNameTextFieldError = MutableStateFlow(false)
    private val _isBusy = MutableStateFlow(false)
    private val _registrationSuccess = MutableStateFlow(false)

    // Publicly exposed immutable state flows
    val titleText: StateFlow<String> = _titleText.asStateFlow()
    val subtitleText: StateFlow<String> = _helpText.asStateFlow()
    val operatorId: StateFlow<String> = _operatorId.asStateFlow()
    val operatorName: StateFlow<String> = _operatorName.asStateFlow()
    val operatorIdTextFieldLabel: StateFlow<String> = _operatorIdTextFieldLabel.asStateFlow()
    val operatorNameTextFieldLabel: StateFlow<String> = _operatorNameTextFieldLabel.asStateFlow()
    val isOperatorIdTextFieldError: StateFlow<Boolean> = _isOperatorIdTextFieldError.asStateFlow()
    val createAccountButtonLabel: StateFlow<String> = _CreateAccountButtonLabel.asStateFlow()
    val isOperatorNameTextFieldError: StateFlow<Boolean> =
        _isOperatorNameTextFieldError.asStateFlow()
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()
    val registrationSuccess: StateFlow<Boolean> = _registrationSuccess.asStateFlow()

    // Function to reset the ViewModel to its initial state
    fun reset() {
        _titleText.value = "Register Account"
        _helpText.value = "Enter an Operator ID and Name to register a new account"
        _operatorId.value = ""
        _operatorName.value = ""
        _operatorIdTextFieldLabel.value = "Operator ID"
        _operatorNameTextFieldLabel.value = "Operator Name"
        _CreateAccountButtonLabel.value = "Create Account"
        _isOperatorIdTextFieldError.value = false
        _isOperatorNameTextFieldError.value = false
        _isBusy.value = false
        _registrationSuccess.value = false
    }

    // Sets the operator ID after user input
    fun setOperatorId(id: String) {
        _operatorId.value = id
    }

    // Sets the operator name after user input
    fun setOperatorName(name: String) {
        _operatorName.value = name
    }

    // Updates the operator ID text field label
    private fun setOperatorIdTextFieldLabel(label: String) {
        _operatorIdTextFieldLabel.value = label
    }

    // Updates the operator name text field label
    private fun setOperatorNameTextFieldLabel(label: String) {
        _operatorNameTextFieldLabel.value = label
    }

    // Validates the operator ID and sets the appropriate error state and label
    private fun setIsOperatorIdTextFieldError(isError: Boolean) {
        _isOperatorIdTextFieldError.value = isError
    }

    // Validates the operator name and sets the appropriate error state and label
    private fun setIsOperatorNameTextFieldError(isError: Boolean) {
        _isOperatorNameTextFieldError.value = isError
    }
    // Updates the title text displayed on the UI
    private fun setTitleText(text: String) {
        _titleText.value = text
    }
    // Updates the subtitle/help text displayed on the UI
    private fun setSubtitleText(text: String) {
        _helpText.value = text
    }

    // Triggers when the "register account" button is clicked
    fun registerAccountButtonClicked(
        // ngl, the name of this function sounds like one of those modern day japanese light novels
        onRegisterAccountButtonClickedAfterSuccessfulCreation: () -> Unit,
    ) {
        // If the account is successfully registered, navigate to the login screen instead
        if (_registrationSuccess.value) {
            onRegisterAccountButtonClickedAfterSuccessfulCreation()
            return
        }
        // Returns immediately if the view model is already busy registering an account
        if (_isBusy.value) {
            return
        }
        // Set isBusy to true to prevent multiple clicks
        _isBusy.value = true

        // Remove leading and trailing whitespaces before validation
        // And also copy the values to local variables to prevent race conditions down the line
        val _localOperatorId = _operatorId.value.trim()
        val _localOperatorName = _operatorName.value.trim()

        // Set the operator ID and operator name to the trimmed values
        _operatorId.value = _localOperatorId
        _operatorName.value = _localOperatorName


        // Validate the operator ID and operator name
        val operatorIdError = isValidOperatorId(_localOperatorId)
        val operatorNameError = isValidOperatorName(_localOperatorName)
        setIsOperatorIdTextFieldError(operatorIdError != IsValidOperatorIdResult.SUCCESS)
        setIsOperatorNameTextFieldError(operatorNameError != IsValidOperatorNameResult.SUCCESS)

        // Set the error message based on the error code
        when (operatorIdError) {
            IsValidOperatorIdResult.SUCCESS -> {
                setOperatorIdTextFieldLabel("Operator ID")
            }

            IsValidOperatorIdResult.EMPTY_ID -> {
                setOperatorIdTextFieldLabel("Operator ID cannot be empty")
            }

            IsValidOperatorIdResult.NOT_NUMBERS -> {
                setOperatorIdTextFieldLabel("Operator ID must be a number")
            }
        }

        // Set the error message based on the error code
        when (operatorNameError) {
            IsValidOperatorNameResult.SUCCESS -> {
                setOperatorNameTextFieldLabel("Operator Name")
            }

            IsValidOperatorNameResult.EMPTY_NAME -> {
                setOperatorNameTextFieldLabel("Operator Name cannot be empty")
            }

            IsValidOperatorNameResult.TOO_LONG -> {
                setOperatorNameTextFieldLabel("Operator Name must not exceed ${UserRestrictions.MAX_NAME_LENGTH} characters")
            }

            IsValidOperatorNameResult.SPECIAL_CHARACTERS -> {
                setOperatorNameTextFieldLabel("Operator Name must not contain special characters")
            }
        }
        // If there are errors, do not proceed, set isBusy to false and return
        if (_isOperatorIdTextFieldError.value || _isOperatorNameTextFieldError.value) {
            _isBusy.value = false
            return
        }
        // If there are no errors, proceed to register the account
        viewModelScope.launch {
            val users = orionApi
                .getService
                .getUsers(userId = _localOperatorId.toInt(), userName = null)

            if (users.isEmpty()) {
                val response = registerNewAccount(_localOperatorId.toInt(), _localOperatorName)
                _isOperatorIdTextFieldError.value = response == 0 || response == -1
                when (response) {
                    0 -> {
                        setOperatorIdTextFieldLabel("An error occurred. Please try again later.")
                    }

                    -1 -> {
                        setOperatorIdTextFieldLabel("Network Error Occurred. Please try again.")
                    }

                    else -> {
                        setTitleText("Account successfully registered.")
                        setSubtitleText("Your Assigned Operator ID is ${response}.\n" +
                                "Please login using this ID from now on")
                        _CreateAccountButtonLabel.value = "Back to login"
                        _registrationSuccess.value = true
                    }
                }
            } else {
                setOperatorIdTextFieldLabel("Operator Id (That Id is already taken)")
                _isOperatorIdTextFieldError.value = true
            }
            _isBusy.value = false
        }
        return
    }

    // Returns:
    // user_id: if the user is successfully registered
    // 0: Either a bad request or the server encountered an error
    // -1: if retrofit throws an exception
    private suspend fun registerNewAccount(userId: Int, userName: String): Int {
        val newUserRequest = OrionApiPostUser.Request(user_id = userId, user_name = userName)
        //requested, then in the text box put in the userId and userName

        return try {

            // Directly calling the API which returns a custom response object
            val response = orionApi.postService.addNewUser(newUserRequest)  //return the post request from retrofit
                                                                            //pass the parsed json string(object form),
                                                                            //

            // API returns a user_id of 0 if the registration failed
            if (response.user_id != 0) {
                response.user_id // Successful registration, return user_id
            } else {
                Log.e("RegisterAccountViewModel", "Registration failed with user_id 0.")
                0 // Registration failed but no exception was thrown, likely a business logic failure
            }
        } catch (e: Exception) {
            Log.e("RegisterAccountViewModel", "Exception when trying to register user: ${e.localizedMessage}", e)
            -1 // An exception occurred, indicating a potential network or serialization issue
        }
    }

}