package com.example.android.marsphotos.ui.start

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.marsphotos.App
import com.example.android.marsphotos.data.Event
import com.example.android.marsphotos.data.Result
import com.example.android.marsphotos.data.constant.POSITION_TYPE
import com.example.android.marsphotos.data.constant.RESPONSE_TYPE
import com.example.android.marsphotos.data.db.entity.User
import com.example.android.marsphotos.data.db.repository.DatabaseRepository
import com.example.android.marsphotos.ui.DefaultViewModel
import com.example.android.marsphotos.util.SharedPreferencesUtil
import kotlinx.coroutines.launch

class StartViewModel : DefaultViewModel() {
    private val repository: DatabaseRepository = DatabaseRepository()
    private val _loginEvent = MutableLiveData<Event<Unit>>()

    private val _position = MutableLiveData<POSITION_TYPE>()
    var position: LiveData<POSITION_TYPE> = _position

    val loginEvent: LiveData<Event<Unit>> = _loginEvent

    fun goToLoginPressed() {
        _loginEvent.value = Event(Unit)
    }

    fun setupProfile() {
        viewModelScope.launch {
            try {
                repository.loadUser(
                    SharedPreferencesUtil.getUserID(App.application.applicationContext).orEmpty()
                ) { result: Result<User> ->
                    if (result is Result.Success) {
                        _position.value = result.data?.info?.position
                        result.data?.let {
                            SharedPreferencesUtil.saveUser(
                                App.application.applicationContext,
                                it
                            )
                            Log.i("tesss","info: "+it.info)
                            SharedPreferencesUtil.saveTableID(
                                App.application.applicationContext,
                                it.info.tableId
                            )
                        }
                    } else if(result is Result.Error){
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
}


