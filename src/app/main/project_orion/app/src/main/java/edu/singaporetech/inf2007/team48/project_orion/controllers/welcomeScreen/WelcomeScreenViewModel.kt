package edu.singaporetech.inf2007.team48.project_orion.controllers.welcomeScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.singaporetech.inf2007.team48.project_orion.enums.IsValidOperatorIdResult
import edu.singaporetech.inf2007.team48.project_orion.services.api.OrionApiClient.orionApi
import edu.singaporetech.inf2007.team48.project_orion.utils.isValidOperatorId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel managing the state for the welcome screen, particularly around operator ID input and login functionality.
class WelcomeScreenViewModel : ViewModel() {
    private val _operatorIdInput = MutableStateFlow("")
    val operatorIdInput: StateFlow<String> = _operatorIdInput.asStateFlow()

    private val _operatorIdTextFieldLabel = MutableStateFlow("")
    val operatorIdTextFieldLabel: StateFlow<String> = _operatorIdTextFieldLabel.asStateFlow()

    private val _operatorIdTextFieldError = MutableStateFlow(false)
    val operatorIdTextFieldError: StateFlow<Boolean> = _operatorIdTextFieldError.asStateFlow()

    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()

    fun reset() {
        _operatorIdInput.value = ""
        _operatorIdTextFieldLabel.value = "Operator ID"
        _operatorIdTextFieldError.value = false
        _isBusy.value = false
    }

    fun setOperatorId(id: String) {
        _operatorIdInput.value = id
    }

    private fun setOperatorIdTextFieldLabel(label: String) {
        _operatorIdTextFieldLabel.value = label
    }

    private fun setOperatorIdTextFieldError(isError: Boolean) {
        _operatorIdTextFieldError.value = isError
    }

    private fun setOperatorIdTextFieldLabelAndError(label: String, isError: Boolean) {
        setOperatorIdTextFieldLabel(label)
        setOperatorIdTextFieldError(isError)
    }

    fun loginAsGuest(onLoginSuccess: (String) -> Unit) {
        onLoginSuccess("Guest")
    }

    fun login(onLoginSuccess: (String) -> Unit) {
        if (_isBusy.value) {
            return
        }
        _isBusy.value = true

        _operatorIdInput.value = _operatorIdInput.value.trim()
        val operatorIdError = isValidOperatorId(_operatorIdInput.value)
        setOperatorIdTextFieldError(operatorIdError.code > 0)

        when (operatorIdError) {
            IsValidOperatorIdResult.EMPTY_ID -> {
                setOperatorIdTextFieldLabelAndError("Operator ID cannot be empty", true)
                _isBusy.value = false // For synchronous error handling, reset _isBusy here
            }

            IsValidOperatorIdResult.NOT_NUMBERS -> {
                setOperatorIdTextFieldLabelAndError("Operator ID must be a number", true)
                _isBusy.value = false // For synchronous error handling, reset _isBusy here
            }

            else -> {
                setOperatorIdTextFieldLabelAndError("Operator ID", false)
                viewModelScope.launch {
                    val operatorName = loginWithOperatorId(_operatorIdInput.value)
                    if (operatorName != null) {
                        onLoginSuccess(operatorName)
                    } else {
                        setOperatorIdTextFieldLabelAndError("Invalid Operator ID", true)
                    }
                    _isBusy.value = false // Ensure _isBusy is reset after asynchronous operations
                }
            }
        }
    }

    private suspend fun loginWithOperatorId(operatorId: String): String? {
        return try {
            //use orionApi.getServe.? to do the API call
            val operator =
                orionApi.getService.getUsers(userId = operatorId.toInt(), userName = null)
                    .firstOrNull()
            operator?.user_name.takeIf { !it.isNullOrEmpty() }
        } catch (e: Exception) {
            Log.e("WelcomeScreenViewModel", "Error Logging in with operator ID: $operatorId", e)
            null
        }
    }

}