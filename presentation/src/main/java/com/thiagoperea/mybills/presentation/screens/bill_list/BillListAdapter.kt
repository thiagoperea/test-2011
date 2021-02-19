package com.thiagoperea.mybills.presentation.screens.bill_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thiagoperea.mybills.core.extension.formatMonetary
import com.thiagoperea.mybills.core.extension.setHide
import com.thiagoperea.mybills.core.extension.setVisible
import com.thiagoperea.mybills.datasource.model.Bill
import com.thiagoperea.mybills.presentation.R
import com.thiagoperea.mybills.presentation.databinding.ItemBillListBinding
import com.thiagoperea.mybills.presentation.databinding.ItemBillListChildBinding

class BillListAdapter(
    private val onBillClick: (Bill) -> Unit,
) : RecyclerView.Adapter<BillListViewHolder>() {

    private val dataset = mutableListOf<BillItemList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBillListBinding.inflate(inflater, parent, false)
        return BillListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BillListViewHolder, position: Int) {
        holder.bind(dataset[position], onBillClick)
    }

    override fun getItemCount() = dataset.size

    fun clearData() {
        dataset.clear()
        notifyDataSetChanged()
    }

    fun setData(list: List<BillItemList>) {
        dataset.addAll(list)
        notifyDataSetChanged()
    }

}

/**
 * ViewHolders
 */

class BillListViewHolder(
    private val binding: ItemBillListBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: BillItemList, onBillClick: (Bill) -> Unit) {
        // setup header
        binding.itemBillListHeaderTitle.text = item.headerTitle
        binding.itemBillListHeaderTotal.text = item.headerTotalValue.formatMonetary()
        if (item.isOverdue == true) {
            binding.itemBillListHeaderAlertImage.setVisible()
        } else {
            binding.itemBillListHeaderAlertImage.setHide()
        }

        // setup expand
        binding.itemBillListHeaderExpandButton.setOnClickListener {
            item.isExpanded = !item.isExpanded
            inflateChildView(item, onBillClick)
            updateExpandedStatus(item)
        }

        // inflate children
        inflateChildView(item, onBillClick)

        // handle with already expanded
        updateExpandedStatus(item)
    }

    private fun updateExpandedStatus(item: BillItemList) {
        if (item.isExpanded) {
            val arrowUp = ContextCompat.getDrawable(itemView.context, R.drawable.ic_arrow_up)
            binding.itemBillListHeaderExpandButton.setImageDrawable(arrowUp)
            binding.itemBillListHeaderChildren.setVisible()
        } else {
            val arrowDown = ContextCompat.getDrawable(itemView.context, R.drawable.ic_arrow_down)
            binding.itemBillListHeaderExpandButton.setImageDrawable(arrowDown)
            binding.itemBillListHeaderChildren.setHide()
        }
    }

    /**
     * Remove every child and add items from list
     */
    private fun inflateChildView(item: BillItemList, onBillClick: (Bill) -> Unit) {
        binding.itemBillListHeaderChildren.removeAllViews()

        item.children.forEachIndexed { index, bill ->
            val hasOnlyOneItem = item.children.size == 1
            val isFirst = index == 0
            val isLast = index == item.children.size - 1
            inflateChildItem(bill, isFirst, isLast, hasOnlyOneItem, onBillClick)
        }
    }

    /**
     * Inflate the children layout and add to parent's viewgroup
     */
    private fun inflateChildItem(
        bill: Bill,
        isFirst: Boolean,
        isLast: Boolean,
        hasOnlyOneItem: Boolean,
        onBillClick: (Bill) -> Unit,
    ) {
        val inflater = LayoutInflater.from(itemView.context)
        val childBinding = ItemBillListChildBinding.inflate(inflater, binding.itemBillListHeaderChildren, false)

        setupMarker(childBinding, isFirst, isLast, hasOnlyOneItem)
        setupChildData(childBinding, bill)
        childBinding.root.setOnClickListener { onBillClick(bill) }

        binding.itemBillListHeaderChildren.addView(childBinding.root)
    }

    /**
     * Update the side markers
     */
    private fun setupMarker(
        childBinding: ItemBillListChildBinding,
        isFirst: Boolean,
        isLast: Boolean,
        hasOnlyOneItem: Boolean,
    ) {
        when {
            hasOnlyOneItem -> {
                childBinding.itemBillListChildMarkerUpper.setHide()
                childBinding.itemBillListChildMarker.setVisible()
                childBinding.itemBillListChildMarkerLower.setHide()
            }
            isFirst -> {
                childBinding.itemBillListChildMarkerUpper.setHide()
                childBinding.itemBillListChildMarker.setVisible()
                childBinding.itemBillListChildMarkerLower.setVisible()
            }
            isLast -> {
                childBinding.itemBillListChildMarkerUpper.setVisible()
                childBinding.itemBillListChildMarker.setVisible()
                childBinding.itemBillListChildMarkerLower.setHide()
            }
            else -> {
                childBinding.itemBillListChildMarkerUpper.setVisible()
                childBinding.itemBillListChildMarker.setVisible()
                childBinding.itemBillListChildMarkerLower.setVisible()
            }
        }
    }

    /**
     * Fill with bill data
     */
    private fun setupChildData(
        childBinding: ItemBillListChildBinding,
        bill: Bill,
    ) {
        childBinding.itemBillListChildDescription.text = bill.description
        childBinding.itemBillListChildTotalValue.text = bill.totalValue?.formatMonetary()

        val stateColor = if (bill.isOverdue == true) {
            R.color.red_900
        } else {
            R.color.gray_900
        }

        childBinding.itemBillListChildMarker.setColorFilter(
            ContextCompat.getColor(itemView.context, stateColor)
        )

        childBinding.itemBillListChildDescription.setTextColor(
            ContextCompat.getColor(itemView.context, stateColor)
        )
    }
}

/**
 * ItemView
 */

data class BillItemList(
    val headerTitle: String = "",
    var headerTotalValue: Double = 0.0,
    val children: MutableList<Bill> = mutableListOf(),
    var isExpanded: Boolean = false,
    val isOverdue: Boolean? = null,
)
