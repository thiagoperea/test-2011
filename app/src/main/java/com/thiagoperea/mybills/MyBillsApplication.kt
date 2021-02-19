package com.thiagoperea.mybills

import android.app.Application
import com.thiagoperea.mybills.business.businessModule
import com.thiagoperea.mybills.datasource.datasourceModule
import com.thiagoperea.mybills.presentation.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

class MyBillsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyBillsApplication)

            modules(
                presentationModule,
                businessModule,
                datasourceModule,
                module {
                    single(named("APP_STYLE_RES")) { R.style.Theme_MyBills }
                }
            )
        }
    }
}