package com.example.android.marsphotos.ui.dish

import android.util.Log
import androidx.lifecycle.*
import com.example.android.marsphotos.data.Result
import com.example.android.marsphotos.data.constant.RESPONSE_TYPE
import com.example.android.marsphotos.data.constant.TYPE_DISH_LIST
import com.example.android.marsphotos.data.db.entity.DishInfo
import com.example.android.marsphotos.data.db.remote.FirebaseReferenceValueObserver
import com.example.android.marsphotos.data.db.repository.DatabaseRepository
import com.example.android.marsphotos.network.ProductApi
import com.example.android.marsphotos.pojo.DishItem
import com.example.android.marsphotos.pojo.Food
import com.example.android.marsphotos.ui.DefaultViewModel
import kotlinx.coroutines.launch

class DishViewModelFactory(private val myUserID: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DishViewModel(myUserID) as T
    }
}

class DishViewModel(private val myUserID: String) : DefaultViewModel() {
    var dishListType = TYPE_DISH_LIST.dishRequests
    private val dbRepository: DatabaseRepository = DatabaseRepository()

    private val _dishItemList = MutableLiveData<MutableList<DishItem>>()
    var dishItemList: LiveData<MutableList<DishItem>> = _dishItemList

    private val _dishList = MutableLiveData<MutableList<DishInfo>>()
    var dishList: LiveData<MutableList<DishInfo>> = _dishList

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
                var response = ProductApi.retrofitService.getProduct().data.items
                _foodMap.value = mutableMapOf()
                response.forEach { _foodMap.value!![it.id] = it }
            } catch (e: Exception) {
                _foodMap.value = mutableMapOf()
            }
        }
    }

    fun canceled(index: Int) {
        _dishList.value?.removeAt(index)
        dbRepository.updateDishRequestsOfBilling(
            1,
            _dishList.value
        ) { result: Result<String> ->
            onResult(null, result)
            if (result is Result.Success) {
                _response.value = RESPONSE_TYPE.success
            } else if (result is Result.Error){
                _response.value = RESPONSE_TYPE.fail
            }
        }
        loadDishs()
    }

    override fun onCleared() {
        super.onCleared()
        fbRefDishProcessingObserver.clear()
    }

    fun loadDishs() {
        dbRepository.loadAndObserveDishsOfBillings(
            1,
            dishListType,
            fbRefDishProcessingObserver
        ) { result: Result<MutableList<DishInfo>> ->
            onResult(_dishList, result)
            if (result is Result.Success) {
                _dishList.value = result.data
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