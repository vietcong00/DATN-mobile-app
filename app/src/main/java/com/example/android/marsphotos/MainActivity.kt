package com.example.android.marsphotos

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.android.marsphotos.data.constant.POSITION_TYPE
import com.example.android.marsphotos.data.constant.TIME_DISPLAY_NOTIFY
import com.example.android.marsphotos.data.db.remote.FirebaseDataSource
import com.example.android.marsphotos.databinding.ActivityMainBinding
import com.example.android.marsphotos.util.SharedPreferencesUtil
import com.example.android.marsphotos.util.forceHideKeyboard
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView
    private lateinit var mainProgressBar: ProgressBar
    private lateinit var notifySuccessResponse: Button
    private lateinit var notifyErrorResponse: Button
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var notificationsBadge = 0
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAnalytics = Firebase.analytics
        // TODO: Retrieve NavController from the NavHostFragment

        notifySuccessResponse = findViewById(R.id.notify_success_response)
        notifySuccessResponse.visibility = View.GONE

        notifyErrorResponse = findViewById(R.id.notify_error_response)
        notifyErrorResponse.visibility = View.GONE

        navView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        mainProgressBar = findViewById(R.id.main_progressBar)

        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.startFragment -> navView.visibility = View.GONE
                R.id.loginFragment -> navView.visibility = View.GONE
                R.id.startSelectFoodFragment -> navView.visibility = View.GONE
                else -> navView.visibility = View.VISIBLE
            }
            showGlobalProgressBar(false)
            currentFocus?.rootView?.forceHideKeyboard()
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_menu,
                R.id.navigation_processing,
                R.id.navigation_billing,
                R.id.startFragment,
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onResume() {
        FirebaseDataSource.dbInstance.goOnline()
        checkPosition()

        setupViewModelObservers()
        super.onResume()
    }

    private fun setupViewModelObservers() {
        viewModel.foods.observe(this) {
            viewModel.foods.value?.let { it1 -> SharedPreferencesUtil.saveFoodList(this, it1) }
            viewModel.foodMap.value?.let { it1 -> SharedPreferencesUtil.saveFoodMap(this, it1) }
        }
    }

    fun showGlobalProgressBar(show: Boolean) {
        if (show) mainProgressBar.visibility = View.VISIBLE
        else mainProgressBar.visibility = View.GONE
    }

    fun showSuccessNotify(response: String) {
        notifySuccessResponse.text = response
        notifySuccessResponse.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            notifySuccessResponse.visibility = View.GONE
        }, TIME_DISPLAY_NOTIFY)
    }

    fun showErrorNotify(response: String) {
        notifyErrorResponse.text = response
        notifyErrorResponse.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            notifyErrorResponse.visibility = View.GONE
        }, TIME_DISPLAY_NOTIFY)
    }

    fun changeNavCustomer() {
        navView.menu.clear()
        navView.inflateMenu(R.menu.bottom_nav_menu_customer)
    }

    fun changeNavChef() {
        navView.menu.clear()
        navView.inflateMenu(R.menu.bottom_nav_menu_chef)
    }

    fun changeNavWaiter() {
        navView.menu.clear()
        navView.inflateMenu(R.menu.bottom_nav_menu_waiter)
    }

    private fun checkPosition() {
        val position = SharedPreferencesUtil.getUser(applicationContext)?.info?.position ?: ""

        if (position !== "") {
            when (position) {
                POSITION_TYPE.chef -> changeNavChef()
                POSITION_TYPE.waiter -> changeNavWaiter()
                POSITION_TYPE.table -> changeNavCustomer()
            }
        }
    }
}