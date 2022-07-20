package com.example.android.marsphotos.ui.startSelectFood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.marsphotos.MainActivity
import com.example.android.marsphotos.R
import com.example.android.marsphotos.data.EventObserver
import com.example.android.marsphotos.data.constant.RESPONSE_TYPE
import com.example.android.marsphotos.databinding.FragmentStartSelectFoodBinding
import com.example.android.marsphotos.util.SharedPreferencesUtil

class StartSelectFoodFragment : Fragment() {
    private lateinit var binding: FragmentStartSelectFoodBinding
    private val viewModel: StartSelectFoodViewModel by viewModels()

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
        val billing = SharedPreferencesUtil.getBilling(requireContext())
        if (billing !== null) {
            navigateDirectlyToMenu()
        }
        setupViewModelObservers()
    }

    private fun setupViewModelObservers() {
        viewModel.dataLoading.observe(viewLifecycleOwner,
            EventObserver { (activity as MainActivity).showGlobalProgressBar(it) })
        viewModel.response.observe(requireActivity()) {
            if (viewModel.response.value === RESPONSE_TYPE.success) {
                navigateDirectlyToMenu()
                viewModel.resetResponseType()
            } else if (viewModel.response.value === RESPONSE_TYPE.fail) {
                (activity as MainActivity).showErrorNotify(
                    viewModel.message.value.toString()
                )
                viewModel.resetResponseType()
            }
        }
    }

    private fun navigateDirectlyToMenu() {
        findNavController().navigate(R.id.action_startSelectFoodFragment_to_navigation_menu)
    }
}
