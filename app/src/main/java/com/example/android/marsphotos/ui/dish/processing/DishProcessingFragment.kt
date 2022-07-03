package com.example.android.marsphotos.ui.dish.processing

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.marsphotos.App
import com.example.android.marsphotos.MainActivity
import com.example.android.marsphotos.databinding.FragmentDishProcessingBinding
import com.example.android.marsphotos.pojo.DishProcessingItem
import com.example.android.marsphotos.util.SharedPreferencesUtil

class DishProcessingFragment : Fragment() {

    private val viewModel: DishProcessingViewModel by viewModels {
        DishProcessingViewModelFactory(
            App.myUserID
        )
    }
    private lateinit var viewDataBinding: FragmentDishProcessingBinding
    private lateinit var listAdapter: DishProcessingListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentDishProcessingBinding.inflate(inflater, container, false)
            .apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupListAdapter()
        setupViewModelObservers()
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = DishProcessingListAdapter(viewModel)
            viewDataBinding.recyclerViewQrCodeStudio.adapter = listAdapter
        } else {
            throw Exception("The viewmodel is not initialized")
        }
    }

    private fun setupViewModelObservers() {
        viewModel.dishProcessingList.observe(requireActivity()) {
            val dishMap = viewModel.foodMap.value
            var tempList: MutableList<DishProcessingItem> = mutableListOf()

            viewModel.dishProcessingList.value?.forEach {
                val item = dishMap?.get(it.dishId)
                    ?.let { it1 -> DishProcessingItem(it1, it.quantity) }
                item?.let { it1 -> tempList.add(it1) }
            }
            viewModel.setDishProcessingItems(tempList)
        }
    }
}