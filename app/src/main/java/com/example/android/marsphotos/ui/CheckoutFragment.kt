/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.marsphotos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.marsphotos.R
import com.example.android.marsphotos.databinding.FragmentCheckoutBinding
import com.example.android.marsphotos.ui.menu.OverviewViewModel

/**
 * [CheckoutFragment] allows people to apply a coupon to their order, submit order, or cancel order.
 */
class CheckoutFragment : Fragment() {
    private lateinit var binding: FragmentCheckoutBinding
    private val viewModel: OverviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckoutBinding.inflate(inflater)
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            // TODO: initialize the OrderViewModel and CheckoutFragment variables
//            viewModel = viewModel
//            submitButton.setOnClickListener {
//                submitOrder()
//            }
            cancelButton.setOnClickListener{
                cancelOrder()
            }
        }
    }

    /**
     * Cancel the order and start over.
     */
    fun cancelOrder() {
        // TODO: Reset order in view model
        // TODO: Navigate back to the [StartFragment] to start over
        findNavController().navigate(R.id.action_checkoutFragment_to_mainFragment)
    }

    /**
     * Submit order and navigate to home screen.
     */
    fun submitOrder() {
        // Show snackbar to "confirm" order
//        Snackbar.make(binding.root, R.string.submit_order, Snackbar.LENGTH_SHORT).show()
//        findNavController().navigate(R.id.action_checkoutFragment_to_startOrderFragment)
//        sharedViewModel.resetOrder()
        // TODO: Reset order in view model
        // TODO: Navigate back to the [StartFragment] to start over
    }

    /**
     * This fragment lifecycle method is called when the view hierarchy associated with the fragment
     * is being removed. As a result, clear out the binding object.
     */
    override fun onDestroyView() {
        super.onDestroyView()
//        _binding = null
    }
}
