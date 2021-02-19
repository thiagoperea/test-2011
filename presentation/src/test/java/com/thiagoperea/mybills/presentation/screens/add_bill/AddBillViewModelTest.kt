package com.thiagoperea.mybills.presentation.screens.add_bill

import androidx.lifecycle.Observer
import com.thiagoperea.mybills.business.usecase.bills.EditBillUseCase
import com.thiagoperea.mybills.business.usecase.bills.SaveBillUseCase
import com.thiagoperea.mybills.business.usecase.bills.UploadAttachmentUseCase
import com.thiagoperea.mybills.business.usecase.user.LogoutUseCase
import com.thiagoperea.mybills.core.converter.DateConverter
import com.thiagoperea.mybills.presentation.BaseCoroutinesTest
import io.mockk.coVerifySequence
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import junit.framework.Assert
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
class AddBillViewModelTest : BaseCoroutinesTest() {

    data class Fields(
        val saveBillUseCase: SaveBillUseCase,
        val editBillUseCase: EditBillUseCase,
        val uploadAttachmentUseCase: UploadAttachmentUseCase,
        val logoutUseCase: LogoutUseCase,
    )

    private fun createMocks(): Pair<AddBillViewModel, Fields> {
        val saveBillUseCase: SaveBillUseCase = mockk(relaxed = true)
        val editBillUseCase: EditBillUseCase = mockk(relaxed = true)
        val uploadAttachmentUseCase: UploadAttachmentUseCase = mockk(relaxed = true)
        val logoutUseCase: LogoutUseCase = mockk(relaxed = true)
        val fields = Fields(saveBillUseCase, editBillUseCase, uploadAttachmentUseCase, logoutUseCase)

        val viewModel = AddBillViewModel(
            saveBillUseCase,
            editBillUseCase,
            uploadAttachmentUseCase,
            logoutUseCase
        )

        return Pair(viewModel, fields)
    }

    @Test
    fun `test saveBill error fields`() {
        //Arrange
        val (viewModel, fields) = createMocks()
        val observerMock = mockk<Observer<AddBillState>>(relaxed = true)
        viewModel.saveState.observeForever(observerMock)

        //Act
        viewModel.saveBill(true, true, "description", "", "02/01/2000")
        viewModel.saveBill(true, true, "", "1010", "02/01/2000")
        viewModel.saveBill(true, true, "description", "1010", "")
        viewModel.saveBill(true, true, "description", "2020", "02/01/2000")

        //Assert
        verify(exactly = 3) {
            observerMock.onChanged(ofType(AddBillState.ErrorFieldsNotFilled::class))
        }
    }

    @Test
    fun `test saveBill success`() {
        //Arrange
        val (viewModel, fields) = createMocks()
        val observerMock = mockk<Observer<AddBillState>>(relaxed = true)
        viewModel.saveState.observeForever(observerMock)

        //Act
        viewModel.saveBill(true, true, "description", "2020", "02/01/2000")

        //Assert
        coVerifySequence {
            observerMock.onChanged(ofType(AddBillState.Loading::class))

            fields.saveBillUseCase.execute(
                eq(true),
                eq(true),
                any(),
                eq("description"),
                eq(20.2),
                eq(DateConverter.getDateFromString("02-01-2000")!!),
                any(),
                any()
            )
        }
    }

    @Test
    fun `test doOnSuccess`() {
        //Arrange
        val (viewModel, fields) = createMocks()
        val observerMock = mockk<Observer<AddBillState>>(relaxed = true)
        viewModel.saveState.observeForever(observerMock)

        //Act
        viewModel.attachmentUri = mockk(relaxed = true)
        viewModel.doOnSuccess("123")

        //Assert
        verifySequence {
            fields.uploadAttachmentUseCase.execute(any(), eq("123"))
            observerMock.onChanged(ofType(AddBillState.Success::class))
        }
    }

    @Test
    fun `test doOnError security`() {
        //Arrange
        val (viewModel, fields) = createMocks()
        val observerMock = mockk<Observer<AddBillState>>(relaxed = true)
        viewModel.saveState.observeForever(observerMock)

        //Act
        viewModel.doOnError(SecurityException())

        //Assert
        verifySequence {
            fields.logoutUseCase.execute()
            observerMock.onChanged(ofType(AddBillState.ErrorLoginInvalid::class))
        }
    }

    @Test
    fun `test doOnError any other`() {
        //Arrange
        val (viewModel, fields) = createMocks()
        val observerMock = mockk<Observer<AddBillState>>(relaxed = true)
        viewModel.saveState.observeForever(observerMock)

        //Act
        viewModel.doOnError(RuntimeException())

        //Assert
        verify {
            observerMock.onChanged(ofType(AddBillState.ErrorBackend::class))
        }
    }

    @Test
    fun `test isOverDue`() {
        //Arrange
        val (viewModel, fields) = createMocks()
        val dateUpper = DateConverter.getDateFromString("01-01-2050")
        val dateLower = DateConverter.getDateFromString("01-01-1950")

        //Act
        val caseUpper = viewModel.isOverdue(dateUpper)
        val caseLower = viewModel.isOverdue(dateLower)

        //Assert
        Assert.assertFalse(caseUpper)
        Assert.assertTrue(caseLower)
    }

    @Test
    fun `test editBill error fields`() {
        //Arrange
        val (viewModel, fields) = createMocks()
        val observerMock = mockk<Observer<AddBillState>>(relaxed = true)
        viewModel.saveState.observeForever(observerMock)

        //Act
        viewModel.editBill("123", true, "description", "", "02/01/2000")
        viewModel.editBill("123", true, "", "1010", "02/01/2000")
        viewModel.editBill("123", true, "description", "1010", "")
        viewModel.editBill("123", true, "description", "2020", "02/01/2000")

        //Assert
        verify(exactly = 3) {
            observerMock.onChanged(ofType(AddBillState.ErrorFieldsNotFilled::class))
        }
    }

    @Test
    fun `test editBill success`() {
        //Arrange
        val (viewModel, fields) = createMocks()
        val observerMock = mockk<Observer<AddBillState>>(relaxed = true)
        viewModel.saveState.observeForever(observerMock)

        //Act
        viewModel.editBill("123", true, "description", "2020", "02/01/2000")

        //Assert
        coVerifySequence {
            observerMock.onChanged(ofType(AddBillState.Loading::class))

            fields.editBillUseCase.execute(
                eq("123"),
                eq(true),
                any(),
                eq("description"),
                eq(20.2),
                eq(DateConverter.getDateFromString("02-01-2000")!!),
                any(),
                any()
            )
        }
    }
}