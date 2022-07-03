package com.example.android.marsphotos.ui.notifications

import android.util.Log
import androidx.lifecycle.*
import com.example.android.marsphotos.data.db.entity.UserInfo
import com.example.android.marsphotos.data.db.entity.UserNotification
import com.example.android.marsphotos.data.Result
import com.example.android.marsphotos.data.db.entity.BillingInfo
import com.example.android.marsphotos.data.db.remote.FirebaseReferenceValueObserver
import com.example.android.marsphotos.data.db.repository.DatabaseRepository
import com.example.android.marsphotos.ui.DefaultViewModel
import com.example.android.marsphotos.util.addNewItem
import com.example.android.marsphotos.util.removeItem

class NotificationsViewModelFactory(private val myUserID: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NotificationsViewModel(myUserID) as T
    }
}

class NotificationsViewModel(private val myUserID: String) : DefaultViewModel() {

    private val dbRepository: DatabaseRepository = DatabaseRepository()
    private val updatedUserInfo = MutableLiveData<UserInfo>()
    private val _userNotificationsList = MutableLiveData<MutableList<UserNotification>>()
    var userNotificationsList: LiveData<MutableList<UserNotification>> = _userNotificationsList

    private val fbRefNotificationsObserver = FirebaseReferenceValueObserver()

    val usersInfoList = MediatorLiveData<MutableList<UserInfo>>()

    init {
        usersInfoList.addSource(updatedUserInfo) { usersInfoList.addNewItem(it) }
        loadNotifications()
        startObservingNotifications()
    }

    override fun onCleared() {
        super.onCleared()
        fbRefNotificationsObserver.clear()
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

        dbRepository.loadDishOfBillings(1) { result: Result<BillingInfo> ->
            if (result is Result.Success){
                Log.i("tesss","bilingg: "+result.data)
            }
        }
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

    fun acceptNotificationPressed(userInfo: UserInfo) {
        updateNotification(userInfo, false)
    }

    fun declineNotificationPressed(userInfo: UserInfo) {
        updateNotification(userInfo, true)
    }
}