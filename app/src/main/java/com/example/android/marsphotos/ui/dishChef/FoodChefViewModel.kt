package com.example.android.marsphotos.ui.dishChef

import androidx.lifecycle.*
import com.example.android.marsphotos.App
import com.example.android.marsphotos.data.Result
import com.example.android.marsphotos.data.constant.RESPONSE_TYPE
import com.example.android.marsphotos.data.constant.TYPE_DISH_LIST
import com.example.android.marsphotos.data.db.entity.BillingInfo
import com.example.android.marsphotos.data.db.entity.DishInfo
import com.example.android.marsphotos.data.db.entity.DishItem
import com.example.android.marsphotos.data.db.entity.Food
import com.example.android.marsphotos.data.db.remote.FirebaseReferenceValueObserver
import com.example.android.marsphotos.data.db.repository.DatabaseRepository
import com.example.android.marsphotos.network.ProductApi
import com.example.android.marsphotos.ui.DefaultViewModel
import com.example.android.marsphotos.util.SharedPreferencesUtil
import kotlinx.coroutines.launch
import java.util.*

class DishChefViewModelFactory(private val myUserID: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DishChefViewModel(myUserID) as T
    }
}

class DishChefViewModel(private val myUserID: String) : DefaultViewModel() {
    var dishListType = TYPE_DISH_LIST.dishRequests
    private val dbRepository: DatabaseRepository = DatabaseRepository()

    private val _dishItemList = MutableLiveData<MutableList<DishItem>>()
    var dishItemList: LiveData<MutableList<DishItem>> = _dishItemList

    private val _dishList = MutableLiveData<MutableList<DishInfo>>()
    var dishList: LiveData<MutableList<DishInfo>> = _dishList

    private val _foods = MutableLiveData<MutableList<Food>>()
    val foods: LiveData<MutableList<Food>> = _foods

    private val _foodMap = MutableLiveData<MutableMap<Int, Food>>()
    val foodMap: LiveData<MutableMap<Int, Food>> = _foodMap

    private val fbRefDishProcessingObserver = FirebaseReferenceValueObserver()

    init {
        loadDishs()
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

    fun changeStatusDish(dish: DishItem) {
        val bodyDishListRemove = _dishList.value?.filter {
            it.billingId === dish.billingId && it.dishId !== dish.dish.id && it.updatedAt !== dish.updatedAt
        }
        val billing = SharedPreferencesUtil.getBilling(App.application.applicationContext)
        if (billing != null) {
            dbRepository.loadAndObserveDishsOfBillings(
                billing.id,
                dishListType,
                fbRefDishProcessingObserver
            ) { resultGetData: Result<MutableList<DishInfo>> ->
                onResult(_dishList, resultGetData)
                if (resultGetData is Result.Success) {
                    var bodyDishListAdd = mutableListOf<DishInfo>()
                    if(!resultGetData.data.isNullOrEmpty()){
                        bodyDishListAdd = resultGetData.data
                    }
                    bodyDishListAdd.add( DishInfo(
                        dishId = dish.dish.id,
                        billing.id,
                        quantity = dish.quantity,
                        note = dish.note,
                        updatedAt = Date().time
                    ))
                    var dishTypeAdd= TYPE_DISH_LIST.dishRequests
                    when (dishListType){
                        TYPE_DISH_LIST.dishRequests->dishTypeAdd=TYPE_DISH_LIST.dishProcessings
                        TYPE_DISH_LIST.dishProcessings->dishTypeAdd=TYPE_DISH_LIST.dishDones
                    }
                    dbRepository.updateDishOfBilling(
                        dishTypeAdd,
                        billing.id,
                        bodyDishListAdd
                    ) {
                        dbRepository.updateDishOfBilling(
                            dishListType,
                            billing.id,
                            bodyDishListRemove as MutableList<DishInfo>
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
        loadDishs()
    }

    override fun onCleared() {
        super.onCleared()
        fbRefDishProcessingObserver.clear()
    }

    fun loadDishs() {
        dbRepository.loadAndObserveAllDish(
            fbRefDishProcessingObserver
        ) { result: Result<MutableList<BillingInfo>> ->
            onResult(null, result)
            if (result is Result.Success) {
                var dishResult = arrayListOf<DishInfo>()
                when (dishListType) {
                    TYPE_DISH_LIST.dishRequests -> {
                        result.data?.forEach {
                            it.dishs?.dishRequests?.let { it1 -> dishResult.addAll(it1) }
                        }
                    }
                    TYPE_DISH_LIST.dishProcessings -> {
                        result.data?.forEach {
                            it.dishs?.dishProcessings?.let { it1 -> dishResult.addAll(it1) }
                        }
                    }
                    TYPE_DISH_LIST.dishDones -> {
                        result.data?.forEach {
                            it.dishs?.dishDones?.let { it1 -> dishResult.addAll(it1) }
                        }
                    }
                }
                _dishList.value = dishResult
            }
        }
    }

    fun setDishItems(list: MutableList<DishItem>) {
        _dishItemList.value = list
    }

    fun switchDishListType(type: TYPE_DISH_LIST) {
        dishListType = type
        loadDishs()
    }
}