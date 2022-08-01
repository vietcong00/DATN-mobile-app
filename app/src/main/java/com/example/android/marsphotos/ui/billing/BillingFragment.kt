package com.example.android.marsphotos.ui.billing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.marsphotos.App
import com.example.android.marsphotos.R
import androidx.navigation.fragment.findNavController
import com.example.android.marsphotos.MainActivity
import com.example.android.marsphotos.data.constant.RESPONSE_TYPE
import com.example.android.marsphotos.data.db.entity.FoodItem
import com.example.android.marsphotos.databinding.FragmentBillingBinding
import com.example.android.marsphotos.util.SharedPreferencesUtil

class BillingFragment : Fragment() {

    private val viewModel: BillingViewModel by viewModels {
        BillingViewModelFactory(
            App.myUserID
        )
    }
    private lateinit var viewDataBinding: FragmentBillingBinding
    private lateinit var listAdapter: BillingListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentBillingBinding.inflate(inflater, container, false)
            .apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListAdapter()
        setupViewModelObservers()
        viewDataBinding.apply {
            prepareToPayBtn.setOnClickListener {
                viewmodel!!.confirmPrepareToPay(requireContext())
            }
        }
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = BillingListAdapter(
                viewModel,
            )
            viewDataBinding.billingsRecyclerView.adapter = listAdapter
        } else {
            throw Exception("The viewmodel is not initialized")
        }
    }

    private fun setupViewModelObservers() {
        viewModel.foodList.observe(requireActivity()) {
            val foodMap = viewModel.foodMap.value
            var tempList: MutableList<FoodItem> = mutableListOf()

            viewModel.foodList.value?.forEach {
                val item = foodMap?.get(it.foodId)
                    ?.let { it1 ->
                        FoodItem(
                            it1,
                            billingId = it.billingId ,
                            note = it.note.toString(),
                            tableName = (SharedPreferencesUtil.getTable(requireContext())?.name ?: ""),
                            quantity = it.quantity,
                            singlePrice = it.singlePrice,
                            updatedAt = it.updatedAt,
                            isBring = it.isBring
                        )
                    }
                item?.let { it1 -> tempList.add(it1) }
            }
            viewModel.setFoodItems(tempList)
        }
        viewModel.response.observe(requireActivity()) {
            if (viewModel.response.value === RESPONSE_TYPE.success) {
                SharedPreferencesUtil.removeBilling(requireContext())
                navigateDirectlyToStartSelectFood()
                viewModel.resetResponseType()
            } else if (viewModel.response.value === RESPONSE_TYPE.fail) {
                (activity as MainActivity).showErrorNotify(
                    viewModel.message.value.toString()
                )
                viewModel.resetResponseType()
            }
        }
    }

    private fun navigateDirectlyToStartSelectFood() {
        findNavController().navigate(R.id.action_navigation_billing_to_startSelectFoodFragment)
    }
}