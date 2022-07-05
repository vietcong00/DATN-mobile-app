package com.example.android.marsphotos

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.android.marsphotos.data.EventObserver
import com.example.android.marsphotos.data.db.remote.FirebaseDataSource
import com.example.android.marsphotos.databinding.ActivityMainBinding
import com.example.android.marsphotos.util.SharedPreferencesUtil
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var notificationsBadge = 0
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAnalytics = Firebase.analytics
        // TODO: Retrieve NavController from the NavHostFragment

        navView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.startFragment -> navView.visibility = View.GONE
                R.id.loginFragment -> navView.visibility = View.GONE
                else -> navView.visibility = View.VISIBLE
            }
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_menu,
                R.id.navigation_processing,
                R.id.navigation_billing,
                R.id.startFragment
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.getMenu().clear();
        navView.inflateMenu(R.menu.bottom_nav_menu_employee);
    }

    override fun onResume() {
        FirebaseDataSource.dbInstance.goOnline()
        setupViewModelObservers()
        super.onResume()
    }

    private fun setupViewModelObservers() {
        viewModel.userNotificationsList.observe(this) {
            if (it.size > 0) {
                notificationsBadge = it.size
            }
        }

        viewModel.foods.observe(this) {
            viewModel.foods.value?.let { it1 -> SharedPreferencesUtil.saveFoodList(this, it1) }
            viewModel.foodMap.value?.let { it1 -> SharedPreferencesUtil.saveFoodMap(this, it1) }
        }
    }

}