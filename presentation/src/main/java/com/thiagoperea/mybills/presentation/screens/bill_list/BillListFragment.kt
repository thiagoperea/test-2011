package com.thiagoperea.mybills.presentation.screens.bill_list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thiagoperea.mybills.core.extension.setHide
import com.thiagoperea.mybills.core.extension.setVisible
import com.thiagoperea.mybills.core.extension.showToast
import com.thiagoperea.mybills.presentation.R
import com.thiagoperea.mybills.presentation.databinding.FragmentBillListBinding
import com.thiagoperea.mybills.presentation.screens.add_bill.AddBillActivity
import org.koin.android.ext.android.inject

class BillListFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE = 123
        private const val EXTRA_BILL_RECEIVABLE = "extra.bill.receivable"
        private const val EXTRA_BILL_DONE = "extra.bill.done"

        fun getInstance(
            isReceivable: Boolean,
            isDone: Boolean,
        ): BillListFragment {

            return BillListFragment().apply {
                val extras = Bundle()
                extras.putBoolean(EXTRA_BILL_RECEIVABLE, isReceivable)
                extras.putBoolean(EXTRA_BILL_DONE, isDone)
                this.arguments = extras
            }
        }
    }

    private lateinit var binding: FragmentBillListBinding

    private val viewModel: BillListViewModel by inject()

    private val isReceivable by lazy { arguments?.getBoolean(EXTRA_BILL_RECEIVABLE) ?: false }
    private val isDone by lazy { arguments?.getBoolean(EXTRA_BILL_DONE) ?: false }

    private var adapter: BillListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBillListBinding.inflate(inflater, container, false)
        setupObservers()
        setupListeners()
        setupList()
        loadBillList()
        return binding.root
    }

    private fun setupListeners() {
        binding.listaAtivosFAB.setOnClickListener {
            val intent = Intent(requireContext(), AddBillActivity::class.java).apply {
                putExtra(AddBillActivity.EXTRA_IS_RECEIVABLE_BILL, isReceivable)
                putExtra(AddBillActivity.EXTRA_IS_REGISTER_BILL, true)
            }

            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            loadBillList()
        }
    }

    private fun setupList() {
        adapter = BillListAdapter { bill ->
            val intent = Intent(requireContext(), AddBillActivity::class.java).apply {
                putExtra(AddBillActivity.EXTRA_IS_REGISTER_BILL, false)
                putExtra(AddBillActivity.EXTRA_BILL_DATA, bill)
            }

            startActivityForResult(intent, REQUEST_CODE)
        }

        binding.listaAtivosList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@BillListFragment.adapter
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        binding.listaAtivosFAB.hide()
                    } else if (dy < 0) {
                        binding.listaAtivosFAB.show()
                    }
                }
            })
        }

        binding.listaAtivosRefreshLayout.setOnRefreshListener { loadBillList() }
    }

    private fun loadBillList() {
        adapter?.clearData()
        viewModel.loadBillList(isReceivable, isDone)
    }

    private fun setupObservers() {
        viewModel.getBillListState.observe(viewLifecycleOwner, { state ->
            when (state) {
                is BillListState.Loading -> showLoading()
                is BillListState.Success -> onSuccess(state.list)
                is BillListState.Error -> onError()
            }
        })
    }

    private fun onError() {
        requireContext().showToast(getString(R.string.error_default))
    }

    private fun onSuccess(list: List<BillItemList>) {
        binding.listaAtivosList.setVisible()
        binding.listaAtivosRefreshLayout.isRefreshing = false
        adapter?.setData(list)
    }

    private fun showLoading() {
        binding.listaAtivosList.setHide()
        binding.listaAtivosRefreshLayout.isRefreshing = true
    }
}