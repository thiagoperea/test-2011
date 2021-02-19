package com.thiagoperea.mybills.business.usecase.user

import com.thiagoperea.mybills.datasource.model.UserData
import com.thiagoperea.mybills.datasource.repository.UserRepository

class GetUserUseCase(
    private val userRepository: UserRepository,
) {

    fun execute(): UserData {
        val firebaseUser = userRepository.getCurrentUser() ?: throw SecurityException()

        return UserData(
            firebaseUser.uid,
            firebaseUser.displayName,
            firebaseUser.email,
            firebaseUser.photoUrl
        )
    }
}