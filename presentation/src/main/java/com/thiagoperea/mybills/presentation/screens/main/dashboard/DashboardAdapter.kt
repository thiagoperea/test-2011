package com.thiagoperea.mybills.presentation.screens.main.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thiagoperea.mybills.core.converter.DateConverter
import com.thiagoperea.mybills.core.extension.formatMonetary
import com.thiagoperea.mybills.datasource.model.Bill
import com.thiagoperea.mybills.presentation.databinding.ItemDashboardListBinding

class DashboardAdapter(
    private val billList: List<Bill>,
) : RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDashboardListBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(billList[position])
    }

    override fun getItemCount() = billList.size

    class ViewHolder(
        private val binding: ItemDashboardListBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(bill: Bill) {
            binding.description.text = bill.description
            binding.dueDate.text = DateConverter.getStringFromDate(bill.dueDate, DateConverter.FORMAT_DD_MM_YYYY)
            binding.totalValue.text = bill.totalValue?.formatMonetary()
        }
    }
}
