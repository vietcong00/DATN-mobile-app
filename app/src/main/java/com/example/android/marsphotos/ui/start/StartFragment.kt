package com.example.android.marsphotos.ui.start

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.marsphotos.MainActivity
import com.example.android.marsphotos.R
import com.example.android.marsphotos.data.EventObserver
import com.example.android.marsphotos.data.constant.POSITION_TYPE
import com.example.android.marsphotos.databinding.FragmentStartBinding
import com.example.android.marsphotos.util.SharedPreferencesUtil

class StartFragment : Fragment() {

    private val viewModel by viewModels<StartViewModel>()
    private lateinit var viewDataBinding: FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        viewDataBinding =
            FragmentStartBinding.inflate(inflater, container, false).apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setHasOptionsMenu(false)

        if (userIsAlreadyLoggedIn()) {
            viewModel.setupProfile()
        }else{
            viewModel.goToLoginPressed()
        }

        return viewDataBinding.root
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            if(userIsAlreadyLoggedIn()) {
                viewModel.goToLoginPressed()
            }
        }, 2000)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModelObservers()
    }

    private fun userIsAlreadyLoggedIn(): Boolean {
        return SharedPreferencesUtil.getUserID(requireContext()) != null
    }

    private fun setupViewModelObservers() {
        viewModel.loginEvent.observe(viewLifecycleOwner, EventObserver { navigateToLogin() })

        viewModel.position.observe(requireActivity()) {
            if (userIsAlreadyLoggedIn()) {
                when (viewModel.position.value) {
                    POSITION_TYPE.chef -> navigateDirectlyToFoodChef()
                    POSITION_TYPE.waiter -> navigateDirectlyToFoodWaiter()
                    POSITION_TYPE.table -> navigateDirectlyToMenu()
                }
            }
        }
    }

    private fun navigateDirectlyToMenu() {
        (activity as MainActivity).changeNavCustomer()
            findNavController().navigate(R.id.action_startFragment_to_startSelectFoodFragment)
    }

    private fun navigateDirectlyToFoodChef() {
        (activity as MainActivity).changeNavChef()
        findNavController().navigate(R.id.action_startFragment_to_navigation_food_chef)
    }

    private fun navigateDirectlyToFoodWaiter() {
        (activity as MainActivity).changeNavWaiter()
        findNavController().navigate(R.id.action_startFragment_to_navigation_food_waiter)
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_startFragment_to_loginFragment)
    }
}