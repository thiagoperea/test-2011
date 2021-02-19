package com.thiagoperea.mybills.presentation.screens.add_bill

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thiagoperea.mybills.business.usecase.bills.EditBillUseCase
import com.thiagoperea.mybills.business.usecase.bills.SaveBillUseCase
import com.thiagoperea.mybills.business.usecase.bills.UploadAttachmentUseCase
import com.thiagoperea.mybills.business.usecase.user.LogoutUseCase
import com.thiagoperea.mybills.core.converter.DateConverter
import com.thiagoperea.mybills.core.extension.convertToDouble
import com.thiagoperea.mybills.presentation.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class AddBillViewModel(
    private val saveBillUseCase: SaveBillUseCase,
    private val editBillUseCase: EditBillUseCase,
    private val uploadAttachmentUseCase: UploadAttachmentUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    var attachmentUri: Uri? = null

    private val _saveState = MutableLiveData<AddBillState>()
    val saveState: LiveData<AddBillState> = _saveState

    fun saveBill(
        isReceivable: Boolean,
        isDone: Boolean,
        description: String,
        totalValue: String,
        dueDate: String,
    ) {
        if (description.isEmpty() || totalValue.isEmpty() || dueDate.isEmpty()) {
            _saveState.postValue(AddBillState.ErrorFieldsNotFilled)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _saveState.postValue(AddBillState.Loading(R.string.creating_bill))

            val valueAsDouble = totalValue.convertToDouble()
            val dueDateAsDate = DateConverter.getDateFromString(dueDate, DateConverter.FORMAT_DD_MM_YYYY)

            saveBillUseCase.execute(
                isReceivable,
                isDone,
                isOverdue(dueDateAsDate),
                description,
                valueAsDouble,
                dueDateAsDate,
                onSuccess = this@AddBillViewModel::doOnSuccess,
                onError = this@AddBillViewModel::doOnError
            )
        }
    }

    private fun doOnSuccess(billId: String) {
        if (attachmentUri != null) {
            uploadAttachmentUseCase.execute(attachmentUri, billId)
        }

        _saveState.postValue(AddBillState.Success)
    }

    private fun doOnError(error: Exception) {
        if (error is SecurityException) {
            logoutUseCase.execute()
            _saveState.postValue(AddBillState.ErrorLoginInvalid)
        } else {
            _saveState.postValue(AddBillState.ErrorBackend)
        }
    }

    private fun isOverdue(dueDateAsDate: Date?): Boolean {
        if (dueDateAsDate == null) {
            return false
        }

        val now = DateConverter.getTodayAsCalendar().time

        return dueDateAsDate.before(now)
    }

    fun editBill(
        billId: String,
        isDone: Boolean,
        description: String,
        totalValue: String,
        dueDate: String,
    ) {
        if (description.isEmpty() || totalValue.isEmpty() || dueDate.isEmpty()) {
            _saveState.postValue(AddBillState.ErrorFieldsNotFilled)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _saveState.postValue(AddBillState.Loading(R.string.editing_bill))

            val valueAsDouble = totalValue.convertToDouble()
            val dueDateAsDate = DateConverter.getDateFromString(dueDate, DateConverter.FORMAT_DD_MM_YYYY)

            editBillUseCase.execute(
                billId,
                isDone,
                isOverdue(dueDateAsDate),
                description,
                valueAsDouble,
                dueDateAsDate,
                onSuccess = { doOnSuccess(billId) },
                onError = this@AddBillViewModel::doOnError
            )
        }
    }
}

sealed class AddBillState {
    data class Loading(@StringRes val description: Int) : AddBillState()

    object Success : AddBillState()

    object ErrorBackend : AddBillState()
    object ErrorFieldsNotFilled : AddBillState()
    object ErrorLoginInvalid : AddBillState()
}