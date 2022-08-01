package com.example.android.marsphotos.ui.foodCustomer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.marsphotos.App
import com.example.android.marsphotos.MainActivity
import com.example.android.marsphotos.R
import com.example.android.marsphotos.data.constant.RESPONSE_TYPE
import com.example.android.marsphotos.data.constant.TYPE_DISH_LIST
import com.example.android.marsphotos.data.db.entity.FoodItem
import com.example.android.marsphotos.databinding.FragmentFoodCustomerBinding

class FoodCustomerFragment : Fragment() {

    private val viewModel: FoodViewModel by viewModels {
        FoodViewModelFactory(
            App.myUserID
        )
    }
    private lateinit var viewDataBinding: FragmentFoodCustomerBinding
    private lateinit var listAdapter: FoodListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentFoodCustomerBinding.inflate(inflater, container, false)
            .apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListAdapter()
        setupViewModelObservers()
        setupViewEvent()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFoods()
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = FoodListAdapter(viewModel,
                ItemFoodCanceledListener { item ->
                    viewModel.canceled(item)
                })
            viewDataBinding.recyclerViewQrCodeStudio.adapter = listAdapter
        } else {
            throw Exception("The viewmodel is not initialized")
        }
    }

    private fun setupViewModelObservers() {
        viewModel.response.observe(requireActivity()) {
            if (viewModel.response.value === RESPONSE_TYPE.success) {
                (activity as MainActivity).showSuccessNotify(
                    viewModel.message.value.toString()                )
                viewModel.resetResponseType()
            } else if (viewModel.response.value === RESPONSE_TYPE.fail) {
                (activity as MainActivity).showErrorNotify(
                    viewModel.message.value.toString()                )
                viewModel.resetResponseType()
            }
        }

        viewModel.foodList.observe(requireActivity()) {
            val foodMap = viewModel.foodMap.value
            var tempList: MutableList<FoodItem> = mutableListOf()

            viewModel.foodList.value?.forEach {
                val item = foodMap?.get(it.foodId)
                    ?.let { it1 ->
                        FoodItem(
                            it1,
                            billingId = it.billingId,
                            note = it.note.toString(),
                            tableName = it.tableName,
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
    }

    private fun setupViewEvent() {
        viewDataBinding.apply {
            radioGroupFood.check(R.id.radio_selected)
            radioGroupFood.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radio_selected -> {
                        viewModel.switchFoodListType(TYPE_DISH_LIST.foodRequests)
                    }
                    R.id.radio_processing -> {
                        viewModel.switchFoodListType(TYPE_DISH_LIST.foodProcessings)
                    }
                    R.id.radio_done -> {
                        viewModel.switchFoodListType(TYPE_DISH_LIST.foodDones)
                    }
                }
            }
        }
    }
}