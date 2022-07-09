package com.example.android.marsphotos.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.marsphotos.data.Event
import com.example.android.marsphotos.data.Result
import com.example.android.marsphotos.data.constant.RESPONSE_TYPE

abstract class DefaultViewModel : ViewModel() {
    protected val mSnackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = mSnackBarText

    private val mDataLoading = MutableLiveData<Event<Boolean>>()
    val dataLoading: LiveData<Event<Boolean>> = mDataLoading

    protected val _response = MutableLiveData<RESPONSE_TYPE>()
    val response: LiveData<RESPONSE_TYPE> = _response

    protected val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    protected fun <T> onResult(mutableLiveData: MutableLiveData<T>? = null, result: Result<T>) {
        when (result) {
            is Result.Loading -> mDataLoading.value = Event(true)

            is Result.Error -> {
                mDataLoading.value = Event(false)
                result.msg?.let { mSnackBarText.value = Event(it) }
            }

            is Result.Success -> {
                mDataLoading.value = Event(false)
                result.data?.let { mutableLiveData?.value = it }
                result.msg?.let { mSnackBarText.value = Event(it) }
            }
        }
    }

    fun resetResponseType(){
        _response.value=RESPONSE_TYPE.nothing
    }
}