package com.thiagoperea.mybills.presentation.screens.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thiagoperea.mybills.business.usecase.user.LoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashViewModel(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private val _splashState = MutableLiveData<SplashState>()
    val splashState: LiveData<SplashState> = _splashState

    fun verifyUserLogged() {
        viewModelScope.launch(Dispatchers.Main) {
            delay(1000)

            withContext(Dispatchers.IO) {
                loginUseCase.isUserLogged(
                    onSuccess = { _splashState.postValue(SplashState.LoginValid) },
                    onError = { _splashState.postValue(SplashState.LoginInvalid) }
                )
            }
        }
    }

    fun createLoginIntent() = loginUseCase.createLoginIntent()
}

sealed class SplashState {
    object LoginValid : SplashState()
    object LoginInvalid : SplashState()
}