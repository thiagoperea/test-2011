package com.thiagoperea.mybills.business.usecase.bills

import com.thiagoperea.mybills.datasource.repository.BillsRepository
import com.thiagoperea.mybills.datasource.repository.UserRepository
import java.util.*

class EditBillUseCase(
    private val userRepository: UserRepository,
    private val billsRepository: BillsRepository,
) {

    fun execute(
        billId: String,
        isDone: Boolean,
        isOverdue: Boolean,
        description: String,
        value: Double,
        dueDate: Date?,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit,
    ) {
        val currentUser = userRepository.getCurrentUser()
        if (currentUser == null) {
            onError(SecurityException())
            return
        }

        val updateFields = mapOf<String, Any?>(
            "description" to description,
            "dueDate" to dueDate,
            "isDone" to isDone,
            "isOverdue" to isOverdue,
            "totalValue" to value
        )

        billsRepository.updateBill(billId, updateFields, currentUser.uid, onSuccess, onError)
    }

}
