package com.example.android.marsphotos.ui.billing

import androidx.lifecycle.*
import com.example.android.marsphotos.data.Result
import com.example.android.marsphotos.data.db.entity.BillingInfo
import com.example.android.marsphotos.data.db.entity.DishInfo
import com.example.android.marsphotos.data.db.remote.FirebaseReferenceValueObserver
import com.example.android.marsphotos.data.db.repository.DatabaseRepository
import com.example.android.marsphotos.network.ProductApi
import com.example.android.marsphotos.pojo.DishItem
import com.example.android.marsphotos.pojo.Food
import com.example.android.marsphotos.ui.DefaultViewModel
import kotlinx.coroutines.launch

class BillingViewModelFactory(private val myUserID: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BillingViewModel(myUserID) as T
    }
}

class BillingViewModel(private val myUserID: String) : DefaultViewModel() {
    private val dbRepository: DatabaseRepository = DatabaseRepository()

    private val _dishItemList = MutableLiveData<MutableList<DishItem>>()
    var dishItemList: LiveData<MutableList<DishItem>> = _dishItemList

    private val _dishList = MutableLiveData<MutableList<DishInfo>>()
    var dishList: LiveData<MutableList<DishInfo>> = _dishList

    private val _foods = MutableLiveData<MutableList<Food>>()
    val foods: LiveData<MutableList<Food>> = _foods

    private val _foodMap = MutableLiveData<MutableMap<Int, Food>>()
    val foodMap: LiveData<MutableMap<Int, Food>> = _foodMap

    private val fbRefNotificationsObserver = FirebaseReferenceValueObserver()
    private val fbRefDishProcessingObserver = FirebaseReferenceValueObserver()

    init {
        loadAllDishOfBilling()
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

    override fun onCleared() {
        super.onCleared()
        fbRefNotificationsObserver.clear()
        fbRefDishProcessingObserver.clear()
    }

    private fun loadAllDishOfBilling() {
        dbRepository.loadDishOfBillings(1) { result: Result<BillingInfo> ->
            if (result is Result.Success) {
                var dishTotal: ArrayList<DishInfo>? = result.data?.dishs?.dishDones
                if (dishTotal == null) {
                    dishTotal = arrayListOf()
                }
                result.data?.dishs?.dishProcessings?.forEach {
                    var found = false
                    for (index in dishTotal.indices) {
                        if (dishTotal[index].dishId === it.dishId) {
                            dishTotal[index].quantity += it.quantity
                            found = true
                            break
                        }
                    }
                    if (!found) {
                        dishTotal.add(it)
                    }
                }
                _dishList.value = dishTotal
            }
        }
    }

    fun setDishItems(list: MutableList<DishItem>) {
        _dishItemList.value = list
    }
}