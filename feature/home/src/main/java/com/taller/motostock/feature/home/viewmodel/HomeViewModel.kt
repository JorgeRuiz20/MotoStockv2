package com.taller.motostock.feature.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.taller.motostock.core.common.mvi.MviViewModel
import com.taller.motostock.core.domain.repository.AuthRepository
import com.taller.motostock.feature.home.HomeContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : MviViewModel<HomeContract.State, HomeContract.Intent, HomeContract.Effect>(
    initialState = HomeContract.State()
) {

    init {
        onIntent(HomeContract.Intent.LoadSession)
    }

    override fun onIntent(intent: HomeContract.Intent) {
        when (intent) {
            HomeContract.Intent.LoadSession -> loadSession()
            HomeContract.Intent.Logout -> logout()
        }
    }

    private fun loadSession() {
        viewModelScope.launch {
            val role = authRepository.getRolUsuarioActual()
            updateState { copy(role = role, isLoading = false) }
        }
    }

    private fun logout() {
        authRepository.logout()
        sendEffect(HomeContract.Effect.NavigateToLogin)
    }
}
