package com.delivery.client.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delivery.client.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val fullName: String = "",
    val role: String = "",
    val userId: Long = 0,
    val phone: String = "",
    val fcmToken: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            val fullName = userPreferences.getFullName() ?: "-"
            val role = userPreferences.getRole() ?: "-"
            val userId = userPreferences.getUserId() ?: 0
            val phone = userPreferences.getPhone() ?: "-"

            _state.value = ProfileState(
                fullName = fullName,
                role = role,
                userId = userId,
                phone = phone,
                isLoading = false
            )
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            userPreferences.clear()
            onDone()
        }
    }
}