package com.thiagoperea.mybills.presentation.screens.main.to_receive

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.thiagoperea.mybills.presentation.screens.bill_list.BillListFragment

class ToReceiveAdapter(
    activity: FragmentActivity,
) : FragmentStateAdapter(activity) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        val state = when (position) {
            0 -> false
            else -> true
        }

        return BillListFragment.getInstance(true, state)
    }
}