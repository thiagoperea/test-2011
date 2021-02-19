package com.thiagoperea.mybills.datasource.repository

import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class UserRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseUI: AuthUI,
    private val firebaseLoginProviders: List<AuthUI.IdpConfig>,
    private val appTheme: Int,
) {
    fun isUserLogged() = firebaseAuth.currentUser != null

    fun doLogout() = firebaseAuth.signOut()

    fun createLoginIntent() = firebaseUI.createSignInIntentBuilder()
        .setAvailableProviders(firebaseLoginProviders)
        .setLockOrientation(true)
        .setTheme(appTheme)
        .build()

    fun getCurrentUser() = firebaseAuth.currentUser
}
