package com.example.android.marsphotos.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.marsphotos.R
import com.example.android.marsphotos.data.EventObserver
import com.example.android.marsphotos.databinding.FragmentSettingBinding
import com.example.android.marsphotos.util.SharedPreferencesUtil

class SettingFragment : Fragment() {

    private val viewModel by viewModels<SettingViewModel>()
    private lateinit var viewDataBinding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        viewDataBinding =
            FragmentSettingBinding.inflate(inflater, container, false)
                .apply {
                    settingViewmodel = viewModel
                }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setHasOptionsMenu(false)

        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.apply {
            userInfo = viewModel.getUser()?.info
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModelObservers()
    }

    private fun setupViewModelObservers() {
        viewModel.logoutEvent.observe(viewLifecycleOwner,
            EventObserver {
                SharedPreferencesUtil.logout(requireContext())
                navigateToStart()
            })
    }

    private fun navigateToStart() {
        findNavController().navigate(R.id.action_navigation_setting_to_startFragment)
    }
}