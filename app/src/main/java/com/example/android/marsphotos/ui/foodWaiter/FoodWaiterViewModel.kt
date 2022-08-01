package com.example.android.marsphotos.ui.foodWaiter

import android.util.Log
import androidx.lifecycle.*
import com.example.android.marsphotos.data.Result
import com.example.android.marsphotos.data.constant.RESPONSE_TYPE
import com.example.android.marsphotos.data.constant.TYPE_DISH_LIST
import com.example.android.marsphotos.data.db.entity.BillingInfo
import com.example.android.marsphotos.data.db.entity.FoodInfo
import com.example.android.marsphotos.data.db.entity.FoodItem
import com.example.android.marsphotos.data.db.entity.Food
import com.example.android.marsphotos.data.db.remote.FirebaseReferenceValueObserver
import com.example.android.marsphotos.data.db.repository.DatabaseRepository
import com.example.android.marsphotos.network.ProductApi
import com.example.android.marsphotos.ui.DefaultViewModel
import kotlinx.coroutines.launch
import java.util.*

class FoodWaiterViewModelFactory(private val myUserID: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FoodWaiterViewModel(myUserID) as T
    }
}

class FoodWaiterViewModel(private val myUserID: String) : DefaultViewModel() {
    var foodListType = TYPE_DISH_LIST.foodDones
    private val dbRepository: DatabaseRepository = DatabaseRepository()

    private val _foodItemList = MutableLiveData<MutableList<FoodItem>>()
    var foodItemList: LiveData<MutableList<FoodItem>> = _foodItemList

    private val _foodList = MutableLiveData<MutableList<FoodInfo>>()
    var foodList: LiveData<MutableList<FoodInfo>> = _foodList

    private val _foods = MutableLiveData<MutableList<Food>>()

    private val _foodMap = MutableLiveData<MutableMap<Int, Food>>()
    val foodMap: LiveData<MutableMap<Int, Food>> = _foodMap

    private val fbRefFoodProcessingObserver = FirebaseReferenceValueObserver()

    init {
        getProductList()
        foodListType = TYPE_DISH_LIST.foodDones
        loadFoods()
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
                Log.e("error", e.toString())
                _message.value = e.toString()
                _response.value = RESPONSE_TYPE.fail
            }
        }
    }

    fun changeStatusFood(food: FoodItem) {
        viewModelScope.launch {
            try {
                dbRepository.loadFoodOfBillingsByType(
                    food.billingId,
                    foodListType,
                ) { resultGetData: Result<MutableList<FoodInfo>> ->
                    onResult(_foodList, resultGetData)
                    if (resultGetData is Result.Success) {
                        var foodInfo = resultGetData.data?.find {
                            it.billingId === food.billingId && it.foodId === food.food.id && it.updatedAt === food.updatedAt
                        }
                        var bodyFoodList = resultGetData.data
                        if (foodInfo != null) {
                            bodyFoodList?.remove(foodInfo)
                            foodInfo.isBring = true
                            foodInfo.updatedAt = Date().time
                            bodyFoodList?.add(foodInfo)
                        }

                        dbRepository.updateFoodOfBilling(
                            TYPE_DISH_LIST.foodDones,
                            food.billingId,
                            bodyFoodList
                        ) { resultAdd: Result<String> ->
                            if (resultAdd is Result.Success) {

                            } else if (resultAdd is Result.Error) {
                                _message.value = "Error when change data!"
                                _response.value = RESPONSE_TYPE.fail
                            }
                        }
                    } else if (resultGetData is Result.Error) {
                        _message.value = "Error when get data!"
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
                dbRepository.loadAndObserveAllFood(
                    fbRefFoodProcessingObserver
                ) { result: Result<MutableList<BillingInfo>> ->
                    onResult(null, result)
                    if (result is Result.Success) {
                        var foodResult = arrayListOf<FoodInfo>()
                        result.data?.forEach {
                            it.foods?.foodDones?.let { it1 -> foodResult.addAll(it1) }
                        }
                        _foodList.value = foodResult.filter {
                            it.isBring === false
                        } as MutableList<FoodInfo>
                    } else if (result is Result.Error) {
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
}