package com.example.android.marsphotos.ui.dish

import android.util.Log
import androidx.lifecycle.*
import com.example.android.marsphotos.data.db.entity.UserInfo
import com.example.android.marsphotos.data.db.entity.UserNotification
import com.example.android.marsphotos.data.Result
import com.example.android.marsphotos.data.constant.TYPE_DISH_LIST
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

class DishViewModelFactory(private val myUserID: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DishViewModel(myUserID) as T
    }
}

class DishViewModel(private val myUserID: String) : DefaultViewModel() {
    var dishListType = TYPE_DISH_LIST.dishRequests
    private val dbRepository: DatabaseRepository = DatabaseRepository()
    private val updatedUserInfo = MutableLiveData<UserInfo>()

    private val _userNotificationsList = MutableLiveData<MutableList<UserNotification>>()
    var userNotificationsList: LiveData<MutableList<UserNotification>> = _userNotificationsList

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

    val usersInfoList = MediatorLiveData<MutableList<UserInfo>>()

    init {
        usersInfoList.addSource(updatedUserInfo) { usersInfoList.addNewItem(it) }
        loadNotifications()
        loadDishs()
        startObservingNotifications()
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
        _dishList.value?.removeAt(index)
        dbRepository.updateDishProcessing(1, _dishList.value)
        loadDishs()
    }

    override fun onCleared() {
        super.onCleared()
        fbRefNotificationsObserver.clear()
        fbRefDishProcessingObserver.clear()
    }

    private fun startObservingNotifications() {
        dbRepository.loadAndObserveUserNotifications(
            myUserID,
            fbRefNotificationsObserver
        ) { result: Result<MutableList<UserNotification>> ->
            if (result is Result.Success) {
                _userNotificationsList.value = result.data
            }
        }
    }

    private fun loadNotifications() {
        dbRepository.loadNotifications(myUserID) { result: Result<MutableList<UserNotification>> ->
            onResult(_userNotificationsList, result)
            if (result is Result.Success) result.data?.forEach { loadUserInfo(it) }
        }
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

    private fun loadUserInfo(userNotification: UserNotification) {
        dbRepository.loadUserInfo(userNotification.userID) { result: Result<UserInfo> ->
            onResult(updatedUserInfo, result)
        }
    }

    private fun updateNotification(otherUserInfo: UserInfo, removeOnly: Boolean) {
        val userNotification = userNotificationsList.value?.find {
            it.userID == otherUserInfo.id
        }

        if (userNotification != null) {
            dbRepository.removeNotification(myUserID, otherUserInfo.id)
            dbRepository.removeSentRequest(otherUserInfo.id, myUserID)

            usersInfoList.removeItem(otherUserInfo)
            _userNotificationsList.removeItem(userNotification)
        }
    }

    fun switchDishListType(type: TYPE_DISH_LIST) {
        dishListType = type
        loadDishs()
    }

    fun acceptNotificationPressed(userInfo: UserInfo) {
        updateNotification(userInfo, false)
    }

    fun declineNotificationPressed(userInfo: UserInfo) {
        updateNotification(userInfo, true)
    }
}