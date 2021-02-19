package com.thiagoperea.mybills.business.usecase.bills

import com.thiagoperea.mybills.datasource.model.Bill
import com.thiagoperea.mybills.datasource.repository.BillsRepository
import com.thiagoperea.mybills.datasource.repository.UserRepository

class GetBillsUseCase(
    private val userRepository: UserRepository,
    private val billsRepository: BillsRepository,
) {

    fun execute(
        receivable: Boolean,
        done: Boolean,
        onSuccess: (List<Bill>) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        val currentUser = userRepository.getCurrentUser()
        if (currentUser == null) {
            onError(SecurityException())
            return
        }

        billsRepository.getBills(receivable, done, currentUser.uid, onSuccess, onError)
    }

}
