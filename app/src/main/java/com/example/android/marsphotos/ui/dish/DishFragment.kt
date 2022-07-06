package com.example.android.marsphotos.ui.dish

import android.os.Bundle
import android.util.Log
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
import com.example.android.marsphotos.databinding.FragmentDishBinding
import com.example.android.marsphotos.pojo.DishItem

class DishFragment : Fragment() {

    private val viewModel: DishViewModel by viewModels {
        DishViewModelFactory(
            App.myUserID
        )
    }
    private lateinit var viewDataBinding: FragmentDishBinding
    private lateinit var listAdapter: DishListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentDishBinding.inflate(inflater, container, false)
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
        viewModel.loadDishs()
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = DishListAdapter(viewModel,
                ItemDishCanceledListener { item ->
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
                    "Hủy món thành công"
                )
                viewModel.resetResponseType()
            }else if (viewModel.response.value === RESPONSE_TYPE.fail) {
                (activity as MainActivity).showErrorNotify(
                    "Hủy món thất bại"
                )
                viewModel.resetResponseType()
            }
        }

        viewModel.dishList.observe(requireActivity()) {
            val dishMap = viewModel.foodMap.value
            var tempList: MutableList<DishItem> = mutableListOf()

            viewModel.dishList.value?.forEach {
                val item = dishMap?.get(it.dishId)
                    ?.let { it1 -> DishItem(it1, it.note.toString(),it.quantity,it.updatedAt) }
                item?.let { it1 -> tempList.add(it1) }
            }
            viewModel.setDishItems(tempList)
        }
    }

    private fun setupViewEvent() {
        viewDataBinding.apply {
            radioGroupDish.check(R.id.radio_selected)
            radioGroupDish.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radio_selected -> {
                        viewModel.switchDishListType(TYPE_DISH_LIST.dishRequests)
                    }
                    R.id.radio_processing -> {
                        viewModel.switchDishListType(TYPE_DISH_LIST.dishProcessings)
                    }
                    R.id.radio_done -> {
                        viewModel.switchDishListType(TYPE_DISH_LIST.dishDones)
                    }
                }
            }
        }
    }
}