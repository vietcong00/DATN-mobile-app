package com.example.android.marsphotos.ui.startSelectDish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.marsphotos.MainActivity
import com.example.android.marsphotos.R
import com.example.android.marsphotos.data.constant.RESPONSE_TYPE
import com.example.android.marsphotos.databinding.FragmentStartSelectFoodBinding

/**
 * This fragment shows the the status of the Mars photos web services transaction.
 */
class StartSelectDishFragment : Fragment() {
    private lateinit var binding: FragmentStartSelectFoodBinding
    private val viewModel: StartSelectDishViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartSelectFoodBinding.inflate(inflater)
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewmodel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModelObservers()
    }

    private fun setupViewModelObservers() {
        viewModel.response.observe(requireActivity()) {
            if (viewModel.response.value === RESPONSE_TYPE.success) {
                findNavController().navigate(R.id.action_startSelectDishFragment_to_navigation_menu)
                viewModel.resetResponseType()
            }else if (viewModel.response.value === RESPONSE_TYPE.fail) {
                (activity as MainActivity).showErrorNotify(
                    "Đặt món thất bại"
                )
                viewModel.resetResponseType()
            }
        }
    }
}
