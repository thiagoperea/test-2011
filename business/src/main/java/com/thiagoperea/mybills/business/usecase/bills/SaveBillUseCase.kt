package com.thiagoperea.mybills.business.usecase.bills

import com.thiagoperea.mybills.datasource.model.Bill
import com.thiagoperea.mybills.datasource.repository.BillsRepository
import com.thiagoperea.mybills.datasource.repository.UserRepository
import java.util.*

class SaveBillUseCase(
    private val userRepository: UserRepository,
    private val billsRepository: BillsRepository,
) {

    fun execute(
        isReceivable: Boolean,
        isDone: Boolean,
        isOverdue: Boolean,
        description: String,
        totalValue: Double,
        dueDate: Date?,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        val currentUser = userRepository.getCurrentUser()
        if (currentUser == null) {
            onError(SecurityException())
            return
        }

        billsRepository.saveBill(
            Bill(null, totalValue, description, dueDate, isReceivable, isDone, null, isOverdue),
            currentUser.uid,
            onSuccess,
            onError
        )
    }

}
