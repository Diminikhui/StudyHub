package com.secondbrain.mobile.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secondbrain.mobile.auth.SessionManager
import com.secondbrain.mobile.data.RawNoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminActionAuthViewModel(private val repository: RawNoteRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isAuthorized = MutableStateFlow(false)
    val isAuthorized: StateFlow<Boolean> = _isAuthorized.asStateFlow()

    fun confirmAdmin(password: String) {
        // Чтобы подтвердить права администратора, мы пытаемся войти под системным аккаунтом 'admin'
        val adminUsername = "admin"

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Пытаемся получить токен администратора
                val response = repository.login(adminUsername, password)
                
                if (response.role == "ADMIN") {
                    // Сохраняем новую сессию (теперь токен в SessionManager будет администраторским)
                    SessionManager.saveSession(response.token, response.username, response.role)
                    _isAuthorized.value = true
                } else {
                    _errorMessage.value = "Этот аккаунт не является администратором"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Неверный пароль администратора"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun reset() {
        _isLoading.value = false
        _errorMessage.value = null
        _isAuthorized.value = false
    }
}
