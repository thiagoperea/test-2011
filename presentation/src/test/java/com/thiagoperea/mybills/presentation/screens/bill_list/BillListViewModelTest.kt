package com.thiagoperea.mybills.presentation.screens.bill_list

import androidx.lifecycle.Observer
import com.thiagoperea.mybills.business.usecase.bills.GetBillsUseCase
import com.thiagoperea.mybills.core.converter.DateConverter
import com.thiagoperea.mybills.datasource.model.Bill
import com.thiagoperea.mybills.presentation.BaseCoroutinesTest
import io.mockk.coVerifySequence
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Test

@ExperimentalCoroutinesApi
class BillListViewModelTest : BaseCoroutinesTest() {

    private fun createMocks(): Pair<BillListViewModel, GetBillsUseCase> {
        val getBillsUseCase = mockk<GetBillsUseCase>(relaxed = true)

        val viewModel = BillListViewModel(getBillsUseCase)
        return Pair(viewModel, getBillsUseCase)
    }

    @Test
    fun `test loadBillList success`() {
        //Arrange
        val (viewModel, usecase) = createMocks()
        val observerMock = mockk<Observer<BillListState>>(relaxed = true)
        viewModel.getBillListState.observeForever(observerMock)

        //Act
        viewModel.loadBillList(true, true)

        //Assert
        coVerifySequence {
            observerMock.onChanged(ofType(BillListState.Loading::class))
            usecase.execute(eq(true), eq(true), any(), any())
        }
    }


    @Test
    fun `test mapToListItem`() {
        //Arrange
        val (viewModel, usecase) = createMocks()
        val slotList = slot<BillListState>()
        val observerMock = mockk<Observer<BillListState>>(relaxed = true)
        viewModel.getBillListState.observeForever(observerMock)

        val list = listOf(
            Bill(dueDate = DateConverter.getDateFromString("01-03-2000"), totalValue = 400.0),
            Bill(dueDate = DateConverter.getDateFromString("05-03-2000"), totalValue = 150.0),
            Bill(dueDate = DateConverter.getDateFromString("20-03-2000"), totalValue = 600.0),
            Bill(dueDate = DateConverter.getDateFromString("01-05-2000"), totalValue = 2500.0),
            Bill(dueDate = DateConverter.getDateFromString("10-08-2000"), totalValue = 7000.0),
        )

        //Act
        viewModel.mapToListItem(list, true)

        //Assert
        verify {
            observerMock.onChanged(capture(slotList))
        }

        val listItem = (slotList.captured as BillListState.Success).list

        Assert.assertEquals(3, listItem.size)

        Assert.assertEquals(1150.0, listItem[0].headerTotalValue, 0.0)
        Assert.assertEquals(3, listItem[0].children.size)

        Assert.assertEquals(2500.0, listItem[1].headerTotalValue, 0.0)
        Assert.assertEquals(1, listItem[1].children.size)

        Assert.assertEquals(7000.0, listItem[2].headerTotalValue, 0.0)
        Assert.assertEquals(1, listItem[2].children.size)
    }

    @Test
    fun `test isOverDue`() {
        //Arrange
        val (viewModel, usecase) = createMocks()
        val bill = Bill(dueDate = DateConverter.getDateFromString("01-01-1950"))

        //Act
        val isOverdue = viewModel.isOverdue(bill)

        //Assert
        Assert.assertTrue(isOverdue)
    }
}