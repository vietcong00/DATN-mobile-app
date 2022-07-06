package com.example.android.marsphotos.ui.menu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsphotos.data.Result
import com.example.android.marsphotos.data.constant.RESPONSE_TYPE
import com.example.android.marsphotos.data.db.entity.DishInfo
import com.example.android.marsphotos.data.db.repository.DatabaseRepository
import com.example.android.marsphotos.network.*
import com.example.android.marsphotos.pojo.*
import com.example.android.marsphotos.ui.DefaultViewModel
import kotlinx.coroutines.launch

enum class MarsApiStatus { LOADING, ERROR, DONE }

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : DefaultViewModel() {
    var billingId = 0

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<MarsApiStatus>()
    private val _products = MutableLiveData<List<Food>>()
    var products: LiveData<List<Food>> = _products

    private val _foodBilling = MutableLiveData<List<FoodBilling>>()

    private val _billing = MutableLiveData<Billing>()
    val billing: LiveData<Billing> = _billing

    // The external immutable LiveData for the request status
    val status: LiveData<MarsApiStatus> = _status

    private val dbRepository: DatabaseRepository = DatabaseRepository()

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        getBilling()
        getFoodBillingList()
        getProductList()
    }

    /**
     * Gets Mars photos information from the Mars API Retrofit service and updates the
     */

    private fun getProductList() {
        viewModelScope.launch {
            try {
                _products.value = ProductApi.retrofitService.getProduct().data.items
            } catch (e: Exception) {
                _products.value = listOf()
            }
        }
    }

    private fun getBilling() {
        viewModelScope.launch {
            try {
                _billing.value = ProductApi.retrofitService.getBilling(1).data
            } catch (e: Exception) {
                _products.value = listOf()
            }
        }
    }

    fun setFoods(foods: List<Food>) {
        _products.value = foods
    }

    fun updateProduct(idProduct: Int, updateProductRequest: UpdateProductRequest) {
        viewModelScope.launch {
            try {
                var response =
                    ProductApi.retrofitService.updateProduct(idProduct, updateProductRequest)

                if (response.isSuccess()) {
//                    getProductList()
                }
            } catch (e: Exception) {
                _products.value = listOf()
            }
        }
    }

    fun createProduct(createProductRequest: CreateProductRequest) {
        viewModelScope.launch {
            try {
                var response = ProductApi.retrofitService.createProduct(createProductRequest)

                if (response.isSuccess()) {
//                    getProductList()
                }
            } catch (e: Exception) {
                _products.value = listOf()
            }
        }
    }

    fun createDishRequestsOfBilling(dishInfo: DishInfo): Boolean {
        var isSuccess = false
        dbRepository.loadDishRequestsOfBillings(1) { result: Result<MutableList<DishInfo>> ->
            onResult(null, result)
            if (result is Result.Success) {
                var body = mutableListOf<DishInfo>()
                if (result.data !== null) {
                    body = result.data
                }
                body.add(dishInfo)
                dbRepository.updateDishRequestsOfBilling(
                    1,
                    body
                ) { resultUpdate: Result<String> ->
                    onResult(null, resultUpdate)
                    if (resultUpdate is Result.Success) {
                        _response.value = RESPONSE_TYPE.success
                    } else if (resultUpdate is Result.Error){
                        _response.value = RESPONSE_TYPE.fail
                    }
                }
            } else  if (result is Result.Error) {
                _response.value = RESPONSE_TYPE.fail
            }

        }
        return isSuccess
    }

    fun deleteProduct(idProduct: Int) {
        viewModelScope.launch {
            try {
                var response = ProductApi.retrofitService.deleteProduct(idProduct)
                if (response.isSuccess()) {
//                    getProductList()
                }
            } catch (e: Exception) {
                _products.value = listOf()
            }
        }
    }

    private fun getFoodBillingList() {
        viewModelScope.launch {
            try {
                _foodBilling.value = ProductApi.retrofitService.getFoodBillingList(1).data.items
            } catch (e: Exception) {
                _foodBilling.value = listOf()
            }
        }
    }

}
