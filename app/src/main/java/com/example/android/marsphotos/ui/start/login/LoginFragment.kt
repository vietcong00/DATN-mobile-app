package com.example.android.marsphotos.ui.start.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.marsphotos.MainActivity
import com.example.android.marsphotos.R
import com.example.android.marsphotos.data.EventObserver
import com.example.android.marsphotos.data.constant.POSITION_TYPE
import com.example.android.marsphotos.databinding.FragmentLoginBinding
import com.example.android.marsphotos.util.SharedPreferencesUtil
import com.example.android.marsphotos.util.forceHideKeyboard
import com.example.android.marsphotos.util.showSnackBar

class LoginFragment : Fragment() {

    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var viewDataBinding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentLoginBinding.inflate(inflater, container, false)
            .apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupObservers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupObservers() {
        viewModel.dataLoading.observe(viewLifecycleOwner,
            EventObserver { (activity as MainActivity).showGlobalProgressBar(it) })

        viewModel.snackBarText.observe(viewLifecycleOwner,
            EventObserver { text ->
                view?.showSnackBar(text)
                view?.forceHideKeyboard()
            })

        viewModel.isLoggedInEvent.observe(viewLifecycleOwner, EventObserver {
            SharedPreferencesUtil.saveUserID(requireContext(), it.uid)
        })

        viewModel.position.observe(requireActivity()) {
//            when (viewModel.position.value) {
//                POSITION_TYPE.chef -> navigateDirectlyToFoodChef()
//                POSITION_TYPE.waiter -> navigateDirectlyToFoodWaiter()
//                POSITION_TYPE.table -> navigateDirectlyToMenu()
//            }
            navigateDirectlyToStart()
        }
    }

    private fun navigateDirectlyToStart() {
        findNavController().navigate(R.id.action_loginFragment_to_startFragment)
    }

    private fun navigateDirectlyToMenu() {
        (activity as MainActivity).changeNavCustomer()
        findNavController().navigate(R.id.action_loginFragment_to_startSelectFoodFragment)
    }

    private fun navigateDirectlyToFoodChef() {
        (activity as MainActivity).changeNavChef()
        findNavController().navigate(R.id.action_loginFragment_to_navigation_food_chef)
    }

    private fun navigateDirectlyToFoodWaiter() {
        (activity as MainActivity).changeNavWaiter()
        findNavController().navigate(R.id.action_loginFragment_to_navigation_food_waiter)
    }
}