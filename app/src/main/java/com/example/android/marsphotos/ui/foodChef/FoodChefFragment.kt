package com.example.android.marsphotos.ui.foodChef

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.marsphotos.App
import com.example.android.marsphotos.R
import com.example.android.marsphotos.data.constant.TYPE_DISH_LIST
import com.example.android.marsphotos.data.db.entity.FoodItem
import com.example.android.marsphotos.databinding.FragmentFoodChefBinding
import com.example.android.marsphotos.util.SharedPreferencesUtil

class FoodChefFragment : Fragment() {

    private val viewModel: FoodChefViewModel by viewModels {
        FoodChefViewModelFactory(
            App.myUserID
        )
    }
    private lateinit var viewDataBinding: FragmentFoodChefBinding
    private lateinit var listAdapter: FoodChefListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentFoodChefBinding.inflate(inflater, container, false)
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
            listAdapter = FoodChefListAdapter(viewModel,
                ItemFoodActionListener { item ->
                    viewModel.changeStatusFood(item)
                })
            viewDataBinding.recyclerViewFoodChef.adapter = listAdapter
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
                            it.billingId,
                            note = it.note,
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