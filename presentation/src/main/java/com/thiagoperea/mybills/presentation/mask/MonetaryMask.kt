package com.thiagoperea.mybills.presentation.mask

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.thiagoperea.mybills.core.extension.convertToDouble
import com.thiagoperea.mybills.core.extension.formatMonetary

class MonetaryMask(
    private val editText: EditText,
) : TextWatcher {

    override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {
        // nothing to do
    }

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        // nothing to do
    }

    override fun afterTextChanged(editable: Editable?) {
        editText.removeTextChangedListener(this)

        editText.setText(editable.convertToDouble().formatMonetary())
        editText.setSelection(editText.length())

        editText.addTextChangedListener(this)
    }

}