package com.thiagoperea.mybills.datasource

import android.content.Context
import androidx.work.WorkManager
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.thiagoperea.mybills.datasource.repository.BillsRepository
import com.thiagoperea.mybills.datasource.repository.UserRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val datasourceModule = module {

    // workmanager
    single { WorkManager.getInstance(get()) }

    // preferences
    single {
        get<Context>().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    // repositories
    single { BillsRepository(get()) }
    single { UserRepository(get(), get(), get(), get(named("APP_STYLE_RES"))) }

    // firebase
    factory { FirebaseAuth.getInstance() }
    factory { AuthUI.getInstance() }
    factory {
        listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
    }
    factory { FirebaseFirestore.getInstance() }
    factory { FirebaseStorage.getInstance() }
}