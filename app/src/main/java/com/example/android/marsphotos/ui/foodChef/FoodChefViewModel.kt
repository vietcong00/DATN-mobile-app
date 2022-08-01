package com.example.android.marsphotos.ui.foodChef

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

class FoodChefViewModelFactory(private val myUserID: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FoodChefViewModel(myUserID) as T
    }
}

class FoodChefViewModel(private val myUserID: String) : DefaultViewModel() {
    var foodListType = TYPE_DISH_LIST.foodRequests
    private val dbRepository: DatabaseRepository = DatabaseRepository()

    private val _foodItemList = MutableLiveData<MutableList<FoodItem>>()
    var foodItemList: LiveData<MutableList<FoodItem>> = _foodItemList

    private val _foodList = MutableLiveData<MutableList<FoodInfo>>()
    var foodList: LiveData<MutableList<FoodInfo>> = _foodList

    private val _foods = MutableLiveData<MutableList<Food>>()
    val foods: LiveData<MutableList<Food>> = _foods

    private val _foodMap = MutableLiveData<MutableMap<Int, Food>>()
    val foodMap: LiveData<MutableMap<Int, Food>> = _foodMap

    private val fbRefFoodProcessingObserver = FirebaseReferenceValueObserver()

    init {
        getProductList()
        foodListType = TYPE_DISH_LIST.foodRequests
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
        Log.i("tesss","foodItem: "+ food)
        viewModelScope.launch {
            try {
                val bodyFoodListRemove = _foodList.value?.filter {
                    it.billingId === food.billingId && (it.foodId !== food.food.id || it.updatedAt !== food.updatedAt)
                }

                var foodTypeRemove = foodListType
                var foodTypeAdd = TYPE_DISH_LIST.foodProcessings
                when (foodListType) {
                    TYPE_DISH_LIST.foodRequests -> foodTypeAdd =
                        TYPE_DISH_LIST.foodProcessings
                    TYPE_DISH_LIST.foodProcessings -> foodTypeAdd =
                        TYPE_DISH_LIST.foodDones
                }

                dbRepository.loadFoodOfBillingsByType(
                    food.billingId,
                    foodTypeAdd,
                ) { resultGetData: Result<MutableList<FoodInfo>> ->
                    onResult(_foodList, resultGetData)
                    if (resultGetData is Result.Success) {
                        var bodyFoodListAdd = mutableListOf<FoodInfo>()
                        if (!resultGetData.data.isNullOrEmpty()) {
                            bodyFoodListAdd = resultGetData.data
                        }
                        bodyFoodListAdd.add(
                            FoodInfo(
                                foodId = food.food.id,
                                food.billingId,
                                quantity = food.quantity,
                                tableName = food.tableName,
                                singlePrice = food.singlePrice,
                                note = food.note,
                                updatedAt = Date().time
                            )
                        )
                        dbRepository.updateFoodOfBilling(
                            foodTypeAdd,
                            food.billingId,
                            bodyFoodListAdd
                        ) { resultAdd: Result<String> ->
                            if (resultAdd is Result.Success) {
                                dbRepository.updateFoodOfBilling(
                                    foodTypeRemove,
                                    food.billingId,
                                    bodyFoodListRemove as MutableList<FoodInfo>
                                ) { resultRemove: Result<String> ->
                                    onResult(null, resultRemove)
                                    if (resultRemove is Result.Success) {
                                        _message.value = "Success!"
                                        _response.value = RESPONSE_TYPE.success
                                    } else if (resultRemove is Result.Error) {
                                        _message.value = "Error when remove data!"
                                        _response.value = RESPONSE_TYPE.fail
                                    }
                                }
                            } else if (resultAdd is Result.Error) {
                                _message.value = "Error when add data!"
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
                        when (foodListType) {
                            TYPE_DISH_LIST.foodRequests -> {
                                result.data?.forEach {
                                    it.foods?.foodRequests?.let { it1 -> foodResult.addAll(it1) }
                                }
                            }
                            TYPE_DISH_LIST.foodProcessings -> {
                                result.data?.forEach {
                                    it.foods?.foodProcessings?.let { it1 -> foodResult.addAll(it1) }
                                }
                            }
                            TYPE_DISH_LIST.foodDones -> {
                                result.data?.forEach {
                                    it.foods?.foodDones?.let { it1 -> foodResult.addAll(it1) }
                                }
                            }
                        }
                        _foodList.value = foodResult
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

    fun switchFoodListType(type: TYPE_DISH_LIST) {
        foodListType = type
        loadFoods()
    }
}