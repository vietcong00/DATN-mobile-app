package com.example.android.marsphotos.ui.billing

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.*
import com.example.android.marsphotos.App
import com.example.android.marsphotos.R
import com.example.android.marsphotos.data.Result
import com.example.android.marsphotos.data.constant.RESPONSE_TYPE
import com.example.android.marsphotos.data.db.entity.BillingInfo
import com.example.android.marsphotos.data.db.entity.FoodInfo
import com.example.android.marsphotos.data.db.entity.FoodItem
import com.example.android.marsphotos.data.db.entity.Food
import com.example.android.marsphotos.data.db.remote.FirebaseReferenceValueObserver
import com.example.android.marsphotos.data.db.repository.DatabaseRepository
import com.example.android.marsphotos.network.PrepareToPayRequest
import com.example.android.marsphotos.network.ProductApi
import com.example.android.marsphotos.ui.DefaultViewModel
import com.example.android.marsphotos.util.SharedPreferencesUtil
import kotlinx.coroutines.launch

class BillingViewModelFactory(private val myUserID: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BillingViewModel(myUserID) as T
    }
}

class BillingViewModel(private val myUserID: String) : DefaultViewModel() {
    private val dbRepository: DatabaseRepository = DatabaseRepository()

    private val _foodItemList = MutableLiveData<MutableList<FoodItem>>()
    var foodItemList: LiveData<MutableList<FoodItem>> = _foodItemList

    private val _foodList = MutableLiveData<MutableList<FoodInfo>>()
    var foodList: LiveData<MutableList<FoodInfo>> = _foodList

    private val _foods = MutableLiveData<MutableList<Food>>()

    private val _foodMap = MutableLiveData<MutableMap<Int, Food>>()
    val foodMap: LiveData<MutableMap<Int, Food>> = _foodMap

    private val fbRefNotificationsObserver = FirebaseReferenceValueObserver()
    private val fbRefFoodProcessingObserver = FirebaseReferenceValueObserver()

    init {
        loadAllFoodOfBilling()
        getProductList()
    }

    private fun getProductList() {
        viewModelScope.launch {
            try {
                _foods.value = ProductApi.retrofitService.getAllFoods().data.items
                _foodMap.value = mutableMapOf()
                (_foods.value as ArrayList<Food>).forEach { _foodMap.value!![it.id] = it }
            } catch (e: Exception) {
                _foods.value = mutableListOf()
                _foodMap.value = mutableMapOf()
            }
        }
    }

    fun confirmPrepareToPay(context: Context) {
        AlertDialog.Builder(context, R.style.AlertDialogTheme)
            .setTitle("Notification")
            .setMessage("Do you want instant payment?")
            .setPositiveButton(
                "Confirm"
            ) { dialog, which ->
                prepareToPay()
            }
            .setNegativeButton("Cancel", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun prepareToPay() {
        viewModelScope.launch {
            try {
                val billing = SharedPreferencesUtil.getBilling(App.application.applicationContext)
                val prepareToPayRequest = PrepareToPayRequest(foodList = _foodList.value)
                var response =
                    ProductApi.retrofitService.prepareToPay(billing?.id ?: 0, prepareToPayRequest)
                if (response.isSuccess()) {
                    _response.value = RESPONSE_TYPE.success
                } else {
                    _message.value = "Error!"
                    _response.value = RESPONSE_TYPE.fail
                }
            } catch (e: Exception) {
                Log.e("error", e.toString())
                _message.value = e.toString()
                _response.value = RESPONSE_TYPE.fail
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        fbRefNotificationsObserver.clear()
        fbRefFoodProcessingObserver.clear()
    }

    private fun loadAllFoodOfBilling() {
        viewModelScope.launch {
            try {
                val billing = SharedPreferencesUtil.getBilling(App.application.applicationContext)
                if (billing != null) {
                    dbRepository.loadFoodOfBillings(billing.id) { result: Result<BillingInfo> ->
                        if (result is Result.Success) {
                            var foodTotal: ArrayList<FoodInfo>? = result.data?.foods?.foodDones
                            if (foodTotal == null) {
                                foodTotal = arrayListOf()
                            }
                            result.data?.foods?.foodProcessings?.forEach {
                                var found = false
                                for (index in foodTotal.indices) {
                                    if (foodTotal[index].foodId === it.foodId) {
                                        foodTotal[index].quantity += it.quantity
                                        found = true
                                        break
                                    }
                                }
                                if (!found) {
                                    foodTotal.add(it)
                                }
                            }
                            _foodList.value = foodTotal
                        } else if (result is Result.Error) {
                            _message.value = "Error when get data!"
                            _response.value = RESPONSE_TYPE.fail
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("error", e.toString())
                _message.value = e.toString()
                _response.value = RESPONSE_TYPE.fail
            }
        }
    }

    fun setFoodItems(list: MutableList<FoodItem>) {
        _foodItemList.value = list
    }
}