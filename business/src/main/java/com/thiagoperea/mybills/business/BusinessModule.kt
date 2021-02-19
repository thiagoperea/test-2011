package com.thiagoperea.mybills.business

import com.thiagoperea.mybills.business.usecase.bills.*
import com.thiagoperea.mybills.business.usecase.notification.NotificationScheduleUseCase
import com.thiagoperea.mybills.business.usecase.settings.GetSettingsUseCase
import com.thiagoperea.mybills.business.usecase.settings.UpdateSettingsUseCase
import com.thiagoperea.mybills.business.usecase.user.GetUserUseCase
import com.thiagoperea.mybills.business.usecase.user.LoginUseCase
import com.thiagoperea.mybills.business.usecase.user.LogoutUseCase
import org.koin.dsl.module

val businessModule = module {

    factory { EditBillUseCase(get(), get()) }
    factory { GetBillsUseCase(get(), get()) }
    factory { GetDashboardBillsUseCase(get(), get()) }
    factory { GetSettingsUseCase(get()) }
    factory { GetUserUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { NotificationScheduleUseCase(get(), get()) }
    factory { SaveBillUseCase(get(), get()) }
    factory { UpdateSettingsUseCase(get()) }
    factory { UploadAttachmentUseCase(get(), get(), get()) }
}