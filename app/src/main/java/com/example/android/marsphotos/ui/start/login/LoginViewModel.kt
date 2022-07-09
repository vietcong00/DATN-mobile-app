package com.example.android.marsphotos.ui.start.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.marsphotos.App
import com.example.android.marsphotos.ui.DefaultViewModel
import com.example.android.marsphotos.data.db.repository.AuthRepository
import com.example.android.marsphotos.data.model.Login
import com.example.android.marsphotos.data.Event
import com.example.android.marsphotos.data.Result
import com.example.android.marsphotos.data.constant.POSITION_TYPE
import com.example.android.marsphotos.data.constant.RESPONSE_TYPE
import com.example.android.marsphotos.data.db.entity.User
import com.example.android.marsphotos.data.db.repository.DatabaseRepository
import com.example.android.marsphotos.util.SharedPreferencesUtil
import com.example.android.marsphotos.util.isEmailValid
import com.example.android.marsphotos.util.isTextValid
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class LoginViewModel : DefaultViewModel() {

    private val authRepository = AuthRepository()
    private val repository: DatabaseRepository = DatabaseRepository()
    private val _isLoggedInEvent = MutableLiveData<Event<FirebaseUser>>()

    val isLoggedInEvent: LiveData<Event<FirebaseUser>> = _isLoggedInEvent
    val emailText = MutableLiveData<String>() // Two way
    val passwordText = MutableLiveData<String>() // Two way
    val isLoggingIn = MutableLiveData<Boolean>() // Two way

    private val _position = MutableLiveData<POSITION_TYPE>()
    var position: LiveData<POSITION_TYPE> = _position

    private fun login() {
        isLoggingIn.value = true
        val login = Login(emailText.value!!, passwordText.value!!)

        authRepository.loginUser(login) { result: Result<FirebaseUser> ->
            onResult(null, result)
            if (result is Result.Success) {
                _isLoggedInEvent.value = Event(result.data!!)
                this.setupProfile()
            }
            if (result is Result.Success || result is Result.Error) isLoggingIn.value = false
        }
    }

    fun loginPressed() {
        if (!isEmailValid(emailText.value.toString())) {
            mSnackBarText.value = Event("Invalid email format")
            return
        }
        if (!isTextValid(6, passwordText.value)) {
            mSnackBarText.value = Event("Password is too short")
            return
        }

        login()
    }

    private fun setupProfile() {
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
                        }
                    } else {
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