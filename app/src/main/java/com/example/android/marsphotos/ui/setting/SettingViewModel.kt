package com.example.android.marsphotos.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.marsphotos.App
import com.example.android.marsphotos.data.Event
import com.example.android.marsphotos.data.db.entity.User
import com.example.android.marsphotos.data.db.repository.AuthRepository
import com.example.android.marsphotos.ui.DefaultViewModel
import com.example.android.marsphotos.util.SharedPreferencesUtil

class SettingViewModel : DefaultViewModel() {
    private val authRepository = AuthRepository()

    private val _logoutEvent = MutableLiveData<Event<Unit>>()
    val logoutEvent: LiveData<Event<Unit>> = _logoutEvent

    fun logoutUserPressed() {
        authRepository.logoutUser()
        _logoutEvent.value = Event(Unit)
    }

    fun getUser(): User? {
        return SharedPreferencesUtil.getUser(App.application.applicationContext)
    }
}


