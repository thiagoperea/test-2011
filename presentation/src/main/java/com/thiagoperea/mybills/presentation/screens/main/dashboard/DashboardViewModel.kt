package com.thiagoperea.mybills.presentation.screens.main.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thiagoperea.mybills.business.usecase.bills.GetDashboardBillsUseCase
import com.thiagoperea.mybills.business.usecase.user.GetUserUseCase
import com.thiagoperea.mybills.business.usecase.user.LogoutUseCase
import com.thiagoperea.mybills.datasource.model.Bill
import com.thiagoperea.mybills.datasource.model.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val getDashboardBillsUseCase: GetDashboardBillsUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _currentUserData = MutableLiveData<DashboardUserState>()
    val currentUserData: LiveData<DashboardUserState> = _currentUserData

    private val _dashboardLoadState = MutableLiveData<DashboardBillState>()
    val dashboardLoadState: LiveData<DashboardBillState> = _dashboardLoadState

    private val _expiredBillLoadState = MutableLiveData<DashboardBillState>()
    val expiredBillLoadState: LiveData<DashboardBillState> = _expiredBillLoadState

    private val _toPayBillLoadState = MutableLiveData<DashboardBillState>()
    val toPayBillLoadState: LiveData<DashboardBillState> = _toPayBillLoadState

    private val _toReceiveBillLoadState = MutableLiveData<DashboardBillState>()
    val toReceiveBillLoadState: LiveData<DashboardBillState> = _toReceiveBillLoadState

    fun loadDashboard() {
        viewModelScope.launch(Dispatchers.IO) {
            _dashboardLoadState.postValue(DashboardBillState.Loading)

            getDashboardBillsUseCase.execute(
                onSuccess = { type, list ->
                    doOnLoadDashboardSuccess(type, list)
                },
                onError = {
                    doOnLoadDashboardError(it)
                }
            )
        }
    }

    private fun doOnLoadDashboardError(error: Exception) {
        if (error is SecurityException) {
            logoutUseCase.execute()
            _currentUserData.postValue(DashboardUserState.UserNotFound)
        } else {
            _dashboardLoadState.postValue(DashboardBillState.Error)
        }
    }

    private fun doOnLoadDashboardSuccess(billType: Int, billList: List<Bill>) {
        val state = DashboardBillState.OnBillsLoad(billList)

        when (billType) {
            GetDashboardBillsUseCase.EXPIRED_BILLS -> _expiredBillLoadState.postValue(state)
            GetDashboardBillsUseCase.BILLS_TO_PAY -> _toPayBillLoadState.postValue(state)
            GetDashboardBillsUseCase.BILLS_TO_RECEIVE -> _toReceiveBillLoadState.postValue(state)
            else -> throw UnsupportedOperationException()
        }
    }

    fun getCurrentUserData() {
        try {
            val userData = getUserUseCase.execute()
            _currentUserData.postValue(DashboardUserState.UserFound(userData))
        } catch (error: SecurityException) {
            _currentUserData.postValue(DashboardUserState.UserNotFound)
        }
    }
}

sealed class DashboardUserState {
    class UserFound(val user: UserData) : DashboardUserState()
    object UserNotFound : DashboardUserState()
}

sealed class DashboardBillState {
    object Loading : DashboardBillState()
    class OnBillsLoad(val billList: List<Bill>) : DashboardBillState()
    object Error : DashboardBillState()
}