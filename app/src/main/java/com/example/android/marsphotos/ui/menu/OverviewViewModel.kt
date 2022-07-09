package com.example.android.marsphotos.ui.menu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsphotos.App
import com.example.android.marsphotos.MainActivity
import com.example.android.marsphotos.data.Result
import com.example.android.marsphotos.data.constant.RESPONSE_TYPE
import com.example.android.marsphotos.data.db.entity.Billing
import com.example.android.marsphotos.data.db.entity.DishInfo
import com.example.android.marsphotos.data.db.entity.Food
import com.example.android.marsphotos.data.db.repository.DatabaseRepository
import com.example.android.marsphotos.network.*
import com.example.android.marsphotos.ui.DefaultViewModel
import com.example.android.marsphotos.util.SharedPreferencesUtil
import kotlinx.coroutines.launch

enum class MarsApiStatus { LOADING, ERROR, DONE }

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : DefaultViewModel() {
    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<MarsApiStatus>()
    private val _products = MutableLiveData<List<Food>>()
    var products: LiveData<List<Food>> = _products

    private val _billing = MutableLiveData<Billing>()
    val billing: LiveData<Billing> = _billing

    val status: LiveData<MarsApiStatus> = _status

    private val dbRepository: DatabaseRepository = DatabaseRepository()

    init {
        getProductList()
    }

    private fun getProductList() {
        viewModelScope.launch {
            try {
                _products.value = ProductApi.retrofitService.getProduct().data.items
            } catch (e: Exception) {
                _products.value = listOf()
            }
        }
    }

    fun createDishRequestsOfBilling(dishInfo: DishInfo): Boolean {
        var isSuccess = false
        val billing = SharedPreferencesUtil.getBilling(App.application.applicationContext)
        if (billing != null) {
            dbRepository.loadDishRequestsOfBillings(billingID = billing.id) { result: Result<MutableList<DishInfo>> ->
                onResult(null, result)
                if (result is Result.Success) {
                    var body = mutableListOf<DishInfo>()
                    if (result.data !== null) {
                        body = result.data
                    }
                    body.add(dishInfo)
                    dbRepository.updateDishRequestsOfBilling(
                        billing.id,
                        body
                    ) { resultUpdate: Result<String> ->
                        onResult(null, resultUpdate)
                        if (resultUpdate is Result.Success) {
                            _response.value = RESPONSE_TYPE.success
                        } else if (resultUpdate is Result.Error) {
                            _response.value = RESPONSE_TYPE.fail
                        }
                    }

                } else if (result is Result.Error) {
                    _response.value = RESPONSE_TYPE.fail
                }

            }
        }
        return isSuccess
    }
}
