package com.thiagoperea.mybills.presentation.screens.main.to_receive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.thiagoperea.mybills.presentation.R
import com.thiagoperea.mybills.presentation.databinding.FragmentToReceiveBinding

class ToReceiveFragment : Fragment() {

    private lateinit var binding: FragmentToReceiveBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentToReceiveBinding.inflate(layoutInflater, container, false)
        setupTabs()
        return binding.root
    }

    private fun setupTabs() {
        binding.toReceiveViewPager.adapter = ToReceiveAdapter(requireActivity())
        TabLayoutMediator(binding.toReceiveTabLayout, binding.toReceiveViewPager) { tab, position ->
            if (position == 0) {
                tab.text = getString(R.string.open)
            } else {
                tab.text = getString(R.string.finished)
            }
        }.attach()
    }
}