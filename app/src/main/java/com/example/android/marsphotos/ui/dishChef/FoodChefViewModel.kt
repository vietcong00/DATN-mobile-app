package com.example.android.marsphotos.ui.foodChef

import androidx.lifecycle.*
import com.example.android.marsphotos.App
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
import com.example.android.marsphotos.util.SharedPreferencesUtil
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
        loadFoods()
        getProductList()
    }

    private fun getProductList() {
        viewModelScope.launch {
            try {
                _foods.value = ProductApi.retrofitService.getProduct().data.items
                _foodMap.value = mutableMapOf()
                (_foods.value as ArrayList<Food>).forEach { _foodMap.value!![it.id] = it }
            } catch (e: Exception) {
                _foods.value = mutableListOf()
                _foodMap.value = mutableMapOf()
            }
        }
    }

    fun changeStatusFood(food: FoodItem) {
        val bodyFoodListRemove = _foodList.value?.filter {
            it.billingId === food.billingId && it.foodId !== food.food.id && it.updatedAt !== food.updatedAt
        }
        val billing = SharedPreferencesUtil.getBilling(App.application.applicationContext)
        if (billing != null) {
            dbRepository.loadAndObserveFoodsOfBillings(
                billing.id,
                foodListType,
                fbRefFoodProcessingObserver
            ) { resultGetData: Result<MutableList<FoodInfo>> ->
                onResult(_foodList, resultGetData)
                if (resultGetData is Result.Success) {
                    var bodyFoodListAdd = mutableListOf<FoodInfo>()
                    if(!resultGetData.data.isNullOrEmpty()){
                        bodyFoodListAdd = resultGetData.data
                    }
                    bodyFoodListAdd.add( FoodInfo(
                        foodId = food.food.id,
                        billing.id,
                        quantity = food.quantity,
                        note = food.note,
                        updatedAt = Date().time
                    ))
                    var foodTypeAdd= TYPE_DISH_LIST.foodRequests
                    when (foodListType){
                        TYPE_DISH_LIST.foodRequests->foodTypeAdd=TYPE_DISH_LIST.foodProcessings
                        TYPE_DISH_LIST.foodProcessings->foodTypeAdd=TYPE_DISH_LIST.foodDones
                    }
                    dbRepository.updateFoodOfBilling(
                        foodTypeAdd,
                        billing.id,
                        bodyFoodListAdd
                    ) {
                        dbRepository.updateFoodOfBilling(
                            foodListType,
                            billing.id,
                            bodyFoodListRemove as MutableList<FoodInfo>
                        ) { resultRemove: Result<String> ->
                            onResult(null, resultRemove)
                            if (resultRemove is Result.Success) {
                                _response.value = RESPONSE_TYPE.success
                            } else if (resultRemove is Result.Error){
                                _response.value = RESPONSE_TYPE.fail
                            }
                        }
                    }
                }
            }
        }
        loadFoods()
    }

    override fun onCleared() {
        super.onCleared()
        fbRefFoodProcessingObserver.clear()
    }

    fun loadFoods() {
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