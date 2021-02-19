package com.thiagoperea.mybills.business.usecase.user

import android.content.Intent
import com.thiagoperea.mybills.datasource.repository.UserRepository

class LoginUseCase(
    private val userRepository: UserRepository,
) {

    fun isUserLogged(
        onSuccess: () -> Unit,
        onError: () -> Unit,
    ) {
        if (userRepository.isUserLogged()) {
            onSuccess()
        } else {
            onError()
        }
    }

    fun createLoginIntent(): Intent {
        return userRepository.createLoginIntent()
    }
}