package com.example.android.marsphotos.ui.dishChef

import android.util.Log
import androidx.lifecycle.*
import com.example.android.marsphotos.data.db.entity.UserInfo
import com.example.android.marsphotos.data.db.entity.UserNotification
import com.example.android.marsphotos.data.Result
import com.example.android.marsphotos.data.constant.TYPE_DISH_LIST
import com.example.android.marsphotos.data.db.entity.BillingInfo
import com.example.android.marsphotos.data.db.entity.DishInfo
import com.example.android.marsphotos.data.db.remote.FirebaseReferenceValueObserver
import com.example.android.marsphotos.data.db.repository.DatabaseRepository
import com.example.android.marsphotos.network.ProductApi
import com.example.android.marsphotos.pojo.DishItem
import com.example.android.marsphotos.pojo.Food
import com.example.android.marsphotos.ui.DefaultViewModel
import com.example.android.marsphotos.util.addNewItem
import com.example.android.marsphotos.util.removeItem
import kotlinx.coroutines.launch

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

    fun canceled(index: Int) {
//        _dishList.value?.removeAt(index)
//        dbRepository.updateDishProcessing(1, _dishList.value)
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
            if (result is Result.Success) {
                var dishResult = arrayListOf<DishInfo>()
                when (dishListType){
                    TYPE_DISH_LIST.dishRequests->{
                        result.data?.forEach {
                            it.dishs?.dishRequests?.let { it1 -> dishResult.addAll(it1) }
                        }
                    }
                    TYPE_DISH_LIST.dishProcessings->{
                        result.data?.forEach {
                            it.dishs?.dishProcessings?.let { it1 -> dishResult.addAll(it1) }
                        }
                    }
                    TYPE_DISH_LIST.dishDones->{
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