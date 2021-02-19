package com.thiagoperea.mybills.presentation

import com.thiagoperea.mybills.presentation.screens.add_bill.AddBillViewModel
import com.thiagoperea.mybills.presentation.screens.bill_list.BillListViewModel
import com.thiagoperea.mybills.presentation.screens.main.dashboard.DashboardViewModel
import com.thiagoperea.mybills.presentation.screens.settings.SettingsViewModel
import com.thiagoperea.mybills.presentation.screens.splash.SplashViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    viewModel { AddBillViewModel(get(), get(), get(), get()) }
    viewModel { BillListViewModel(get()) }
    viewModel { DashboardViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get(), get()) }
    viewModel { SplashViewModel(get()) }
}