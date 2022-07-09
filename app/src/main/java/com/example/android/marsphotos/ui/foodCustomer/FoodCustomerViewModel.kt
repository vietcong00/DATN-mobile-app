package com.example.android.marsphotos.ui.foodCustomer

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
                var response = ProductApi.retrofitService.getProduct().data.items
                _foodMap.value = mutableMapOf()
                response.forEach { _foodMap.value!![it.id] = it }
            } catch (e: Exception) {
                _foodMap.value = mutableMapOf()
            }
        }
    }

    fun canceled(index: Int) {
        _foodList.value?.removeAt(index)
        dbRepository.updateFoodRequestsOfBilling(
            billingId,
            _foodList.value
        ) { result: Result<String> ->
            onResult(null, result)
            if (result is Result.Success) {
                _response.value = RESPONSE_TYPE.success
            } else if (result is Result.Error){
                _response.value = RESPONSE_TYPE.fail
            }
        }
        loadFoods()
    }

    override fun onCleared() {
        super.onCleared()
        fbRefFoodProcessingObserver.clear()
    }

    fun loadFoods() {
        dbRepository.loadAndObserveFoodsOfBillings(
            billingId,
            foodListType,
            fbRefFoodProcessingObserver
        ) { result: Result<MutableList<FoodInfo>> ->
            onResult(_foodList, result)
            if (result is Result.Success) {
                _foodList.value = result.data
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