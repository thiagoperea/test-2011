package com.thiagoperea.mybills.presentation.screens.main.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.thiagoperea.mybills.core.extension.setHide
import com.thiagoperea.mybills.core.extension.setVisible
import com.thiagoperea.mybills.datasource.model.Bill
import com.thiagoperea.mybills.datasource.model.UserData
import com.thiagoperea.mybills.presentation.R
import com.thiagoperea.mybills.presentation.databinding.FragmentDashboardBinding
import com.thiagoperea.mybills.presentation.screens.settings.SettingsActivity
import com.thiagoperea.mybills.presentation.screens.splash.SplashActivity
import org.koin.android.ext.android.inject

class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by inject()
    private lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDashboardBinding.inflate(layoutInflater, container, false)
        observeFields()
        setupClicks()
        setupLists()
        viewModel.getCurrentUserData()
        viewModel.loadDashboard()
        return binding.root
    }

    private fun setupLists() {
        listOf(
            binding.expiredList,
            binding.toPayList,
            binding.toReceiveList
        ).forEach {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.setHasFixedSize(false)
        }
    }

    private fun setupClicks() {
        binding.optionsButton.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeFields() {
        viewModel.currentUserData.observe(viewLifecycleOwner, { state ->
            when (state) {
                is DashboardUserState.UserFound -> setupUserData(state.user)
                is DashboardUserState.UserNotFound -> goToSplashScreen()
            }
        })

        viewModel.dashboardLoadState.observe(viewLifecycleOwner, { state ->
            if (state is DashboardBillState.Loading) {
                showLoading()
            }
        })

        viewModel.expiredBillLoadState.observe(viewLifecycleOwner, { state ->
            if (state is DashboardBillState.OnBillsLoad) {
                showExpiredBills(state.billList)
            }
        })

        viewModel.toPayBillLoadState.observe(viewLifecycleOwner, { state ->
            if (state is DashboardBillState.OnBillsLoad) {
                showBillsToPay(state.billList)
            }
        })

        viewModel.toReceiveBillLoadState.observe(viewLifecycleOwner, { state ->
            if (state is DashboardBillState.OnBillsLoad) {
                showBillsToReceive(state.billList)
            }
        })
    }

    private fun showLoading() {
        binding.loading.setVisible()
        binding.expiredCard.setHide()
        binding.toReceiveCard.setHide()
        binding.toPayCard.setHide()
    }

    private fun showExpiredBills(billList: List<Bill>) {
        binding.loading.setHide()
        binding.expiredCard.setVisible()
        if (billList.isNotEmpty()) {
            binding.expiredNone.setHide()
            binding.expiredList.setVisible()
            binding.expiredList.adapter = DashboardAdapter(billList)
        } else {
            binding.expiredNone.setVisible()
            binding.expiredList.setHide()
        }
    }

    private fun showBillsToPay(billList: List<Bill>) {
        binding.loading.setHide()
        binding.toPayCard.setVisible()
        if (billList.isNotEmpty()) {
            binding.toPayNone.setHide()
            binding.toPayList.setVisible()
            binding.toPayList.adapter = DashboardAdapter(billList)
        } else {
            binding.toPayNone.setVisible()
            binding.toPayList.setHide()
        }
    }

    private fun showBillsToReceive(billList: List<Bill>) {
        binding.loading.setHide()
        binding.toReceiveCard.setVisible()
        if (billList.isNotEmpty()) {
            binding.toReceiveNone.setHide()
            binding.toReceiveList.setVisible()
            binding.toReceiveList.adapter = DashboardAdapter(billList)
        } else {
            binding.toReceiveNone.setVisible()
            binding.toReceiveList.setHide()
        }
    }

    private fun setupUserData(user: UserData) {
        binding.userGreetings.text = getString(R.string.dashboard_greetings, user.displayName)

        Glide.with(binding.root.context)
            .load(user.photoUrl)
            .placeholder(R.drawable.ic_person)
            .circleCrop()
            .into(binding.userImage)
    }

    private fun goToSplashScreen() {
        val intent = Intent(requireContext(), SplashActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}