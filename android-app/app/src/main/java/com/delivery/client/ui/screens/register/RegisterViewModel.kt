package com.delivery.client.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delivery.client.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterState(
    val email: String = "",
    val password: String = "",
    val fullName: String = "",
    val phone: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onEmailChange(v: String) { _state.value = _state.value.copy(email = v, error = null) }
    fun onPasswordChange(v: String) { _state.value = _state.value.copy(password = v, error = null) }
    fun onFullNameChange(v: String) { _state.value = _state.value.copy(fullName = v, error = null) }
    fun onPhoneChange(v: String) { _state.value = _state.value.copy(phone = v, error = null) }

    fun register() {
        val current = _state.value

        if (current.email.isBlank() || current.password.isBlank() ||
            current.fullName.isBlank() || current.phone.isBlank()) {
            _state.value = current.copy(error = "Popunite sva polja")
            return
        }

        if (current.password.length < 6) {
            _state.value = current.copy(error = "Lozinka mora imati najmanje 6 karaktera")
            return
        }

        viewModelScope.launch {
            _state.value = current.copy(isLoading = true, error = null)
            val result = authRepository.register(
                email = current.email,
                password = current.password,
                fullName = current.fullName,
                phone = current.phone
            )

            result.fold(
                onSuccess = {
                    _state.value = _state.value.copy(isLoading = false, success = true)
                    viewModelScope.launch {
                        authRepository.registerFcmToken()
                    }
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Greska pri registraciji"
                    )
                }
            )
        }
    }
}