package com.thiagoperea.mybills.presentation.screens.main.to_pay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.thiagoperea.mybills.presentation.R
import com.thiagoperea.mybills.presentation.databinding.FragmentToPayBinding

class ToPayFragment : Fragment() {

    private lateinit var binding: FragmentToPayBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentToPayBinding.inflate(inflater, container, false)
        setupTabs()
        return binding.root
    }

    private fun setupTabs() {
        binding.toPayViewPager.adapter = ToPayAdapter(requireActivity())
        TabLayoutMediator(binding.toPayTabLayout, binding.toPayViewPager) { tab, position ->
            if (position == 0) {
                tab.text = getString(R.string.open)
            } else {
                tab.text = getString(R.string.finished)
            }
        }.attach()
    }
}