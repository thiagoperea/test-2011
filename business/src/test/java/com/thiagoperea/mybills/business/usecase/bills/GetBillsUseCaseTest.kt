package com.thiagoperea.mybills.business.usecase.bills

import com.google.firebase.auth.FirebaseUser
import com.thiagoperea.mybills.datasource.repository.BillsRepository
import com.thiagoperea.mybills.datasource.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Test

class GetBillsUseCaseTest {

    private data class Fields(
        val userRepository: UserRepository,
        val billsRepository: BillsRepository,
    )

    private fun createMocks(): Pair<GetBillsUseCase, Fields> {
        val userRepository = mockk<UserRepository>(relaxed = true)
        val billsRepository = mockk<BillsRepository>(relaxed = true)
        val fields = Fields(userRepository, billsRepository)

        val usecase = GetBillsUseCase(userRepository, billsRepository)
        return Pair(usecase, fields)
    }

    @Test
    fun `test execute success`() {
        //Arrange
        val (usecase, fields) = createMocks()
        val user = mockk<FirebaseUser>(relaxed = true)

        every { fields.userRepository.getCurrentUser() } returns user

        //Act
        usecase.execute(true, true, {}, {})

        //Assert
        verify {
            fields.billsRepository.getBills(eq(true), eq(true), any(), any(), any())
        }
    }

    @Test
    fun `test execute error`() {
        //Arrange
        val (usecase, fields) = createMocks()

        //Act
        usecase.execute(false, false, {}, {

            //Assert
            Assert.assertTrue(it is SecurityException)
        })
    }
}