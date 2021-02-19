package com.thiagoperea.mybills.presentation.screens.bill_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thiagoperea.mybills.business.usecase.bills.GetBillsUseCase
import com.thiagoperea.mybills.core.converter.DateConverter
import com.thiagoperea.mybills.datasource.model.Bill
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class BillListViewModel(
    private val getBillsUseCase: GetBillsUseCase,
) : ViewModel() {

    private val _getBillListState = MutableLiveData<BillListState>()
    val getBillListState: LiveData<BillListState> = _getBillListState

    fun loadBillList(isReceivable: Boolean, isDone: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            _getBillListState.postValue(BillListState.Loading)

            getBillsUseCase.execute(
                isReceivable,
                isDone,
                onSuccess = { list ->
                    mapToListItem(list, isDone)
                },
                onError = {
                    _getBillListState.postValue(BillListState.Error)
                }
            )
        }
    }

    fun mapToListItem(billList: List<Bill>, isDone: Boolean) {
        val itemList = mutableListOf<BillItemList>()

        var lastDate = ""
        billList.sortedBy { it.dueDate }.forEach { bill ->
            val formattedDate = DateConverter.getStringFromDate(bill.dueDate, DateConverter.FORMAT_MMMM_YYYY)

            bill.isOverdue = if (!isDone) {
                isOverdue(bill)
            } else {
                false
            }

            if (formattedDate != lastDate) {
                lastDate = formattedDate
                itemList.add(BillItemList(formattedDate, isOverdue = bill.isOverdue))
            }

            itemList.lastOrNull()?.apply {
                this.children.add(bill)
                this.headerTotalValue += bill.totalValue ?: 0.0
            }
        }

        _getBillListState.postValue(BillListState.Success(itemList))
    }

    /**
     * Verify if current bill is on overdue
     */
    fun isOverdue(bill: Bill): Boolean {
        val now = DateConverter.getTodayAsCalendar()

        val billDate = Calendar.getInstance().apply { time = bill.dueDate }

        return billDate.before(now)
    }
}

sealed class BillListState {
    object Loading : BillListState()
    data class Success(val list: List<BillItemList>) : BillListState()
    object Error : BillListState()
}