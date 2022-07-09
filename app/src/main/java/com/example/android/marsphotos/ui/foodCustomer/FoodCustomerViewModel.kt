package com.example.android.marsphotos.ui.foodCustomer

import android.util.Log
import androidx.lifecycle.*
import com.example.android.marsphotos.App
import com.example.android.marsphotos.data.Result
import com.example.android.marsphotos.data.constant.RESPONSE_TYPE
import com.example.android.marsphotos.data.constant.TYPE_DISH_LIST
import com.example.android.marsphotos.data.db.entity.FoodInfo
import com.example.android.marsphotos.data.db.entity.FoodItem
import com.example.android.marsphotos.data.db.entity.Food
import com.example.android.marsphotos.data.db.remote.FirebaseReferenceValueObserver
import com.example.android.marsphotos.data.db.repository.DatabaseRepository
import com.example.android.marsphotos.network.ProductApi
import com.example.android.marsphotos.ui.DefaultViewModel
import com.example.android.marsphotos.util.SharedPreferencesUtil
import kotlinx.coroutines.launch

class FoodViewModelFactory(private val myUserID: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FoodViewModel(myUserID) as T
    }
}

class FoodViewModel(private val myUserID: String) : DefaultViewModel() {
    var billingId = 0
    var foodListType = TYPE_DISH_LIST.foodRequests
    private val dbRepository: DatabaseRepository = DatabaseRepository()

    private val _foodItemList = MutableLiveData<MutableList<FoodItem>>()
    var foodItemList: LiveData<MutableList<FoodItem>> = _foodItemList

    private val _foodList = MutableLiveData<MutableList<FoodInfo>>()
    var foodList: LiveData<MutableList<FoodInfo>> = _foodList

    private val _foodMap = MutableLiveData<MutableMap<Int, Food>>()
    val foodMap: LiveData<MutableMap<Int, Food>> = _foodMap

    private val fbRefFoodProcessingObserver = FirebaseReferenceValueObserver()

    init {
        loadFoods()
        getProductList()
        val billing = SharedPreferencesUtil.getBilling(App.application.applicationContext)
        if (billing != null) {
            billingId = billing.id
        }
    }

    private fun getProductList() {
        viewModelScope.launch {
            try {
                var response = ProductApi.retrofitService.getAllFoods().data.items
                _foodMap.value = mutableMapOf()
                response.forEach { _foodMap.value!![it.id] = it }
            } catch (e: Exception) {
                _foodMap.value = mutableMapOf()
                Log.e("error", e.toString())
                _message.value = e.toString()
                _response.value = RESPONSE_TYPE.fail
            }
        }
    }

    fun canceled(index: Int) {
        viewModelScope.launch {
            try {
                _foodList.value?.removeAt(index)
                dbRepository.updateFoodRequestsOfBilling(
                    billingId,
                    _foodList.value
                ) { result: Result<String> ->
                    onResult(null, result)
                    if (result is Result.Success) {
                        _message.value = "Success!"
                        _response.value = RESPONSE_TYPE.success
                    } else if (result is Result.Error) {
                        _message.value = "Error when cancel!"
                        _response.value = RESPONSE_TYPE.fail
                    }
                }
                loadFoods()
            } catch (e: Exception) {
                Log.e("error", e.toString())
                _message.value = e.toString()
                _response.value = RESPONSE_TYPE.fail
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        fbRefFoodProcessingObserver.clear()
    }

    fun loadFoods() {
        viewModelScope.launch {
            try {
                dbRepository.loadAndObserveFoodsOfBillings(
                    billingId,
                    foodListType,
                    fbRefFoodProcessingObserver
                ) { result: Result<MutableList<FoodInfo>> ->
                    onResult(_foodList, result)
                    if (result is Result.Success) {
                        _foodList.value = result.data
                    } else if(result is Result.Error){
                        _message.value = "Error when get data!"
                        _response.value = RESPONSE_TYPE.fail
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

    fun switchFoodListType(type: TYPE_DISH_LIST) {
        foodListType = type
        loadFoods()
    }
}