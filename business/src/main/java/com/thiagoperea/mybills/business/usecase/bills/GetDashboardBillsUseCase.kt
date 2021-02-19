package com.thiagoperea.mybills.business.usecase.bills

import com.thiagoperea.mybills.datasource.model.Bill
import com.thiagoperea.mybills.datasource.repository.BillsRepository
import com.thiagoperea.mybills.datasource.repository.UserRepository

class GetDashboardBillsUseCase(
    private val userRepository: UserRepository,
    private val billsRepository: BillsRepository,
) {

    companion object {
        const val EXPIRED_BILLS = 0
        const val BILLS_TO_PAY = 1
        const val BILLS_TO_RECEIVE = 2
    }

    private var errorWasThrown = false

    fun execute(
        onSuccess: (Int, List<Bill>) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        val currentUser = userRepository.getCurrentUser()
        if (currentUser == null) {
            onError(SecurityException())
            return
        }

        billsRepository.getExpiredBillsList(
            currentUser.uid,
            { onSuccess(EXPIRED_BILLS, it) },
            throwError(onError)
        )

        billsRepository.getNotExpiredBillsList(
            currentUser.uid,
            false,
            { onSuccess(BILLS_TO_PAY, it) },
            throwError(onError)
        )

        billsRepository.getNotExpiredBillsList(
            currentUser.uid,
            true,
            { onSuccess(BILLS_TO_RECEIVE, it) },
            throwError(onError)
        )
    }

    private fun throwError(onError: (Exception) -> Unit): (Exception) -> Unit {
        if (!errorWasThrown) {
            errorWasThrown = true
            return onError
        } else {
            return {}
        }
    }
}