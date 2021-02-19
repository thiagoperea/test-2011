package com.thiagoperea.mybills.business.usecase.user

import com.thiagoperea.mybills.datasource.repository.UserRepository

class LogoutUseCase(
    private val userRepository: UserRepository,
) {
    fun execute() = userRepository.doLogout()
}