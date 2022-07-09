package com.example.android.marsphotos.ui.billing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.marsphotos.App
import com.example.android.marsphotos.data.db.entity.DishItem
import com.example.android.marsphotos.databinding.FragmentBillingBinding

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
        viewModel.dishList.observe(requireActivity()) {
            val dishMap = viewModel.foodMap.value
            var tempList: MutableList<DishItem> = mutableListOf()

            viewModel.dishList.value?.forEach {
                val item = dishMap?.get(it.dishId)
                    ?.let { it1 ->
                        DishItem(
                            it1,
                            it.billingId,
                            it.note.toString(),
                            it.quantity,
                            it.updatedAt
                        )
                    }
                item?.let { it1 -> tempList.add(it1) }
            }
            viewModel.setDishItems(tempList)
        }
    }
}