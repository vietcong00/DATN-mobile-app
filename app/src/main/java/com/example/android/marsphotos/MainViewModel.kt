package com.example.android.marsphotos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsphotos.data.db.remote.FirebaseAuthStateObserver
import com.example.android.marsphotos.data.db.repository.AuthRepository
import com.example.android.marsphotos.data.Result
import com.example.android.marsphotos.data.db.entity.Food
import com.example.android.marsphotos.data.db.remote.FirebaseReferenceConnectedObserver
import com.example.android.marsphotos.data.db.remote.FirebaseReferenceValueObserver
import com.example.android.marsphotos.data.db.repository.DatabaseRepository
import com.example.android.marsphotos.network.ProductApi
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val dbRepository = DatabaseRepository()
    private val authRepository = AuthRepository()

    private val fbRefNotificationsObserver = FirebaseReferenceValueObserver()
    private val fbAuthStateObserver = FirebaseAuthStateObserver()
    private val fbRefConnectedObserver = FirebaseReferenceConnectedObserver()
    private var userID = App.myUserID

    private val _foods = MutableLiveData<MutableList<Food>>()
    val foods: LiveData<MutableList<Food>> = _foods

    private val _foodMap = MutableLiveData<MutableMap<Int,Food>>()
    val foodMap: LiveData<MutableMap<Int,Food>> = _foodMap

    init {
        setupAuthObserver()
        getProductList()
    }

    private fun getProductList() {
        viewModelScope.launch {
            try {
                _foods.value = ProductApi.retrofitService.getAllFoods().data.items
                _foodMap.value = mutableMapOf();
                (_foods.value as ArrayList<Food>).forEach { _foodMap.value!![it.id] = it }
            } catch (e: Exception) {
                _foods.value = mutableListOf()
                _foodMap.value = mutableMapOf();
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        fbRefNotificationsObserver.clear()
        fbRefConnectedObserver.clear()
        fbAuthStateObserver.clear()
    }

    private fun setupAuthObserver() {
        authRepository.observeAuthState(fbAuthStateObserver) { result: Result<FirebaseUser> ->
            if (result is Result.Success) {
                userID = result.data!!.uid
                fbRefConnectedObserver.start(userID)
            } else {
                fbRefConnectedObserver.clear()
                stopObservingNotifications()
            }
        }
    }

    private fun stopObservingNotifications() {
        fbRefNotificationsObserver.clear()
    }

}
