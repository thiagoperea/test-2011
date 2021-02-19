package com.thiagoperea.mybills.business.usecase.bills

import com.google.firebase.auth.FirebaseUser
import com.thiagoperea.mybills.datasource.repository.BillsRepository
import com.thiagoperea.mybills.datasource.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Test

class EditBillUseCaseTest {

    private data class Fields(
        val userRepository: UserRepository,
        val billsRepository: BillsRepository,
    )

    private fun createMocks(): Pair<EditBillUseCase, Fields> {
        val userRepository = mockk<UserRepository>(relaxed = true)
        val billsRepository = mockk<BillsRepository>(relaxed = true)
        val fields = Fields(userRepository, billsRepository)

        val usecase = EditBillUseCase(userRepository, billsRepository)
        return Pair(usecase, fields)
    }

    @Test
    fun `test execute success`() {
        //Arrange
        val (usecase, fields) = createMocks()
        val user = mockk<FirebaseUser>(relaxed = true)
        val slot = slot<Map<String, Any?>>()

        every { fields.userRepository.getCurrentUser() } returns user

        //Act
        usecase.execute("id", true, true, "desc", 1.0, null, {}, {})

        //Assert
        verify {
            fields.billsRepository.updateBill(eq("id"), capture(slot), any(), any(), any())
        }

        assertEquals("desc", slot.captured["description"])
        assertEquals(null, slot.captured["dueDate"])
        assertEquals(true, slot.captured["isDone"])
        assertEquals(true, slot.captured["isOverdue"])
        assertEquals(1.0, slot.captured["totalValue"])
    }

    @Test
    fun `test execute error`() {
        //Arrange
        val (usecase, fields) = createMocks()

        //Act
        usecase.execute("", false, false, "", 0.0, null, {}, {

            //Assert
            assertTrue(it is SecurityException)
        })
    }
}