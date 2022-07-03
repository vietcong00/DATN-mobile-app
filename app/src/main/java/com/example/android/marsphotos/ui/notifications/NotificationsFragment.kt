package com.example.android.marsphotos.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.marsphotos.App
import com.example.android.marsphotos.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private val viewModel: NotificationsViewModel by viewModels { NotificationsViewModelFactory(App.myUserID) }
    private lateinit var viewDataBinding: FragmentNotificationsBinding
    private lateinit var listAdapter: NotificationsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentNotificationsBinding.inflate(inflater, container, false)
            .apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupListAdapter()
//        setupViewModelObservers()
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = NotificationsListAdapter(viewModel)
            viewDataBinding.usersRecyclerView.adapter = listAdapter
        } else {
            throw Exception("The viewmodel is not initialized")
        }
    }

//    private fun setupViewModelObservers() {
//        viewModel.userNotificationsList.observe(viewLifecycleOwner) {
//            if (it.size > 0) {
//                viewModel.loadNotifications()
//            }
//        }
//    }
}