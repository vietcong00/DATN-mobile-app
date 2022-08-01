package com.example.android.marsphotos.ui.foodWaiter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.marsphotos.App
import com.example.android.marsphotos.data.db.entity.FoodItem
import com.example.android.marsphotos.databinding.FragmentFoodWaiterBinding
import com.example.android.marsphotos.util.SharedPreferencesUtil

class FoodWaiterFragment : Fragment() {

    private val viewModel: FoodWaiterViewModel by viewModels {
        FoodWaiterViewModelFactory(
            App.myUserID
        )
    }
    private lateinit var viewDataBinding: FragmentFoodWaiterBinding
    private lateinit var listAdapter: FoodWaiterListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentFoodWaiterBinding.inflate(inflater, container, false)
            .apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListAdapter()
        setupViewModelObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFoods()
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = FoodWaiterListAdapter(viewModel,
                ItemFoodBringListener { item ->
                    viewModel.changeStatusFood(item)
                })
            viewDataBinding.recyclerViewQrCodeStudio.adapter = listAdapter
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
}