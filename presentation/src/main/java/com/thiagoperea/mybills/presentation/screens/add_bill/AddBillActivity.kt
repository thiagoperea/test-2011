package com.thiagoperea.mybills.presentation.screens.add_bill

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.thiagoperea.mybills.core.converter.DateConverter
import com.thiagoperea.mybills.core.extension.formatMonetary
import com.thiagoperea.mybills.core.extension.setHide
import com.thiagoperea.mybills.core.extension.setVisible
import com.thiagoperea.mybills.core.extension.showToast
import com.thiagoperea.mybills.datasource.model.Bill
import com.thiagoperea.mybills.presentation.R
import com.thiagoperea.mybills.presentation.databinding.ActivityAddBillBinding
import com.thiagoperea.mybills.presentation.databinding.DialogLoadingBinding
import com.thiagoperea.mybills.presentation.mask.MonetaryMask
import org.koin.android.ext.android.inject
import java.util.*

class AddBillActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IS_REGISTER_BILL = "extra.is.register.bill"
        const val EXTRA_IS_RECEIVABLE_BILL = "extra.is.receivable.bill"
        const val EXTRA_BILL_DATA = "extra.bill.data"

        const val REQUEST_CODE_ATTACHMENT = 1
    }

    private lateinit var binding: ActivityAddBillBinding
    private val viewModel: AddBillViewModel by inject()

    private var loadingView: DialogLoadingBinding? = null
    private var loadingDialog: AlertDialog? = null

    private val isRegisterBill by lazy { intent.getBooleanExtra(EXTRA_IS_REGISTER_BILL, false) }
    private val isReceivableBill by lazy { intent.getBooleanExtra(EXTRA_IS_RECEIVABLE_BILL, false) }
    private val billToEdit by lazy { intent.getParcelableExtra(EXTRA_BILL_DATA) as Bill? }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBillBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupConditionalFields()
        setupObservers()
        setupListeners()
        setupMask()
        setupEditMode()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_ATTACHMENT && resultCode == Activity.RESULT_OK) {
            viewModel.attachmentUri = data?.data
            binding.attachmentPicker.setText(getString(R.string.attachment_selected))
        }
    }

    private fun setupEditMode() {
        if (!isRegisterBill) {
            binding.description.editText?.setText(billToEdit?.description)
            binding.totalValue.editText?.setText(billToEdit?.totalValue?.formatMonetary())
            binding.doneBill.isChecked = billToEdit?.isDone == true
            binding.dueDate.setText(DateConverter.getStringFromDate(billToEdit?.dueDate, DateConverter.FORMAT_DD_MM_YYYY))

            if (!billToEdit?.attachmentUrl.isNullOrBlank()) {
                binding.addAttachment.isChecked = true
                binding.attachmentDownloadButton.setVisible()
                binding.attachmentPicker.setText(getString(R.string.attachment_selected))
                binding.attachmentDownloadButton.setOnClickListener {
                    val downloadIntent = Intent(Intent.ACTION_VIEW)
                    downloadIntent.data = Uri.parse(billToEdit?.attachmentUrl)
                    startActivity(downloadIntent)
                }
            } else {
                binding.attachmentDownloadButton.setHide()
            }
        }
    }

    private fun setupConditionalFields() {
        val (toolbarText, buttonText) = if (isRegisterBill) {
            Pair(R.string.new_bill, R.string.save)
        } else {
            Pair(R.string.edit_bill, R.string.edit)
        }

        val doneBillText = if (isReceivableBill) {
            R.string.is_received_bill
        } else {
            R.string.is_paid_bill
        }

        binding.saveButton.text = getString(buttonText)
        binding.toolbar.title = getString(toolbarText)
        binding.doneBill.text = getString(doneBillText)
    }

    private fun setupMask() {
        binding.totalValue.editText?.let {
            it.addTextChangedListener(MonetaryMask(it))
        }
    }

    private fun setupObservers() {
        viewModel.saveState.observe(this, { state ->
            when (state) {
                is AddBillState.Loading -> onSaveLoading(state.description)
                is AddBillState.Success -> onSaveSuccess()
                is AddBillState.ErrorBackend -> onSaveError()
                is AddBillState.ErrorFieldsNotFilled -> onSaveErrorNotFilled()
                is AddBillState.ErrorLoginInvalid -> onSaveErrorAuth()
            }
        })
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        binding.dueDate.setOnClickListener { showDueDateSelector() }

        binding.addAttachment.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.attachmentPickerLayout.setVisible()
            } else {
                binding.attachmentPickerLayout.setHide()
            }
        }
        binding.attachmentPicker.setOnClickListener { showAttachmentSelector() }

        binding.saveButton.setOnClickListener {
            if (isRegisterBill) {
                viewModel.saveBill(
                    isReceivableBill,
                    binding.doneBill.isChecked,
                    binding.description.editText?.text.toString(),
                    binding.totalValue.editText?.text.toString(),
                    binding.dueDate.text.toString()
                )
            } else {
                viewModel.editBill(
                    billToEdit?.id ?: "",
                    binding.doneBill.isChecked,
                    binding.description.editText?.text.toString(),
                    binding.totalValue.editText?.text.toString(),
                    binding.dueDate.text.toString()
                )
            }
        }
    }

    private fun showAttachmentSelector() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_ATTACHMENT)
    }

    private fun showDueDateSelector() {
        MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.due_date))
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build()
            .apply {
                addOnPositiveButtonClickListener { dateLong ->
                    val correctFromUtc = dateLong - TimeZone.getDefault().getOffset(dateLong)
                    val dateDate = Date(correctFromUtc)
                    val formattedDate = DateConverter.getStringFromDate(dateDate, DateConverter.FORMAT_DD_MM_YYYY)
                    binding.dueDate.setText(formattedDate)
                }
            }
            .show(supportFragmentManager, null)
    }

    private fun onSaveLoading(@StringRes description: Int) {
        if (loadingView == null || loadingDialog == null) {
            showSaveLoading()
        }

        loadingView?.dialogLoadingDescription?.text = getString(description)
    }

    private fun showSaveLoading() {
        loadingView = DialogLoadingBinding.inflate(layoutInflater)
        loadingDialog = MaterialAlertDialogBuilder(this)
            .setView(loadingView?.root)
            .setCancelable(false)
            .show()
    }

    private fun hideSaveLoading() {
        loadingDialog?.dismiss()
        loadingDialog = null
        loadingView = null
    }

    private fun onSaveSuccess() {
        hideSaveLoading()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun onSaveError() {
        hideSaveLoading()
        showToast(getString(R.string.error_default))
    }

    private fun onSaveErrorNotFilled() {
        hideSaveLoading()
        showToast(getString(R.string.error_fields))
    }

    private fun onSaveErrorAuth() {
        hideSaveLoading()
        showToast(getString(R.string.error_auth))
    }
}
