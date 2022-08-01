package com.example.android.marsphotos.ui.menu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsphotos.App
import com.example.android.marsphotos.data.Result
import com.example.android.marsphotos.data.constant.RESPONSE_TYPE
import com.example.android.marsphotos.data.db.entity.Billing
import com.example.android.marsphotos.data.db.entity.FoodInfo
import com.example.android.marsphotos.data.db.entity.Food
import com.example.android.marsphotos.data.db.repository.DatabaseRepository
import com.example.android.marsphotos.network.*
import com.example.android.marsphotos.ui.DefaultViewModel
import com.example.android.marsphotos.util.SharedPreferencesUtil
import kotlinx.coroutines.launch

enum class MarsApiStatus { LOADING, ERROR, DONE }

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
                _products.value = ProductApi.retrofitService.getAllFoods().data.items
            } catch (e: Exception) {
                _products.value = listOf()
                _message.value = "Error when get data!"
                _response.value = RESPONSE_TYPE.fail
            }
        }
    }

    fun createFoodRequestsOfBilling(foodInfo: FoodInfo) {
        viewModelScope.launch {
            try {
                val billing = SharedPreferencesUtil.getBilling(App.application.applicationContext)
                Log.i("tesss","billing: "+ billing)
                if (billing != null) {
                    dbRepository.loadFoodRequestsOfBillings(billingID = billing.id) { result: Result<MutableList<FoodInfo>> ->
                        onResult(null, result)
                        if (result is Result.Success) {
                            var body = mutableListOf<FoodInfo>()
                            if (result.data !== null) {
                                body = result.data
                            }
                            body.add(foodInfo)
                            dbRepository.updateFoodRequestsOfBilling(
                                billing.id,
                                body
                            ) { resultUpdate: Result<String> ->
                                onResult(null, resultUpdate)
                                if (resultUpdate is Result.Success) {
                                    _message.value = "Success!"
                                    _response.value = RESPONSE_TYPE.success
                                } else if (resultUpdate is Result.Error) {
                                    _message.value = "Error when choosing food!"
                                    _response.value = RESPONSE_TYPE.fail
                                }
                            }

                        } else if (result is Result.Error) {
                            _message.value = "Error when get data!"
                            _response.value = RESPONSE_TYPE.fail
                        }
                    }
                } else {
                    _message.value = "Billing does not exist!"
                    _response.value = RESPONSE_TYPE.fail
                }
            } catch (e: Exception) {
                Log.e("error", e.toString())
                _message.value = e.toString()
                _response.value = RESPONSE_TYPE.fail
            }
        }
    }
}
