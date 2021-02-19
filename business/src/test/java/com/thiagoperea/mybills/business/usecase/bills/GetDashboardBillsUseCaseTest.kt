package com.thiagoperea.mybills.business.usecase.bills

import com.google.firebase.auth.FirebaseUser
import com.thiagoperea.mybills.datasource.model.Bill
import com.thiagoperea.mybills.datasource.repository.BillsRepository
import com.thiagoperea.mybills.datasource.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verifySequence
import org.junit.Assert
import org.junit.Test

class GetDashboardBillsUseCaseTest {

    private data class Fields(
        val userRepository: UserRepository,
        val billsRepository: BillsRepository,
    )

    private fun createMocks(): Pair<GetDashboardBillsUseCase, Fields> {
        val userRepository = mockk<UserRepository>(relaxed = true)
        val billsRepository = mockk<BillsRepository>(relaxed = true)
        val fields = Fields(userRepository, billsRepository)

        val usecase = GetDashboardBillsUseCase(userRepository, billsRepository)
        return Pair(usecase, fields)
    }

    @Test
    fun `test execute success`() {
        //Arrange
        val (usecase, fields) = createMocks()
        val user = mockk<FirebaseUser>(relaxed = true)
        val onSuccessMock = mockk<(Int, List<Bill>) -> Unit>(relaxed = true)

        val slotExpired = slot<(List<Bill>) -> Unit>()
        val slotToPay = slot<(List<Bill>) -> Unit>()
        val slotToReceive = slot<(List<Bill>) -> Unit>()

        every { fields.userRepository.getCurrentUser() } returns user

        //Act
        usecase.execute(onSuccessMock, {})

        //Assert
        verifySequence {
            fields.billsRepository.getExpiredBillsList(any(), capture(slotExpired), any())
            fields.billsRepository.getNotExpiredBillsList(any(), eq(false), capture(slotToPay), any())
            fields.billsRepository.getNotExpiredBillsList(any(), eq(true), capture(slotToReceive), any())
        }

        slotExpired.captured.invoke(listOf())
        slotToPay.captured.invoke(listOf())
        slotToReceive.captured.invoke(listOf())

        verifySequence {
            onSuccessMock.invoke(GetDashboardBillsUseCase.EXPIRED_BILLS, any())
            onSuccessMock.invoke(GetDashboardBillsUseCase.BILLS_TO_PAY, any())
            onSuccessMock.invoke(GetDashboardBillsUseCase.BILLS_TO_RECEIVE, any())
        }
    }

    @Test
    fun `test execute error`() {
        //Arrange
        val (usecase, fields) = createMocks()

        //Act
        usecase.execute({ _, _ -> }, {

            //Assert
            Assert.assertTrue(it is SecurityException)
        })
    }
}