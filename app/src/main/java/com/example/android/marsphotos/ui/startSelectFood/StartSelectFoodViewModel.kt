package com.example.android.marsphotos.ui.startSelectFood

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.android.marsphotos.App
import com.example.android.marsphotos.data.constant.RESPONSE_TYPE
import com.example.android.marsphotos.network.ProductApi
import com.example.android.marsphotos.ui.DefaultViewModel
import com.example.android.marsphotos.util.SharedPreferencesUtil
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit

class StartSelectFoodViewModel : DefaultViewModel() {

    fun getBillingRelativeTable() {
        viewModelScope.launch {
            try { 
                val response = SharedPreferencesUtil.getTableID(App.application.applicationContext)
                    ?.let { ProductApi.retrofitService.getBillingRelativeTable(it) }
                if (response != null && response.isSuccess()) {
                    SharedPreferencesUtil.saveBilling(
                        App.application.applicationContext,
                        response.data
                    )
                    SharedPreferencesUtil.saveTable(
                        App.application.applicationContext,
                        response.data.table
                    )
                    _response.value = RESPONSE_TYPE.success
                } else {
                    _message.value = "Error!"
                    _response.value = RESPONSE_TYPE.fail
                }
            } catch (e: Exception) {
                Log.e("error", e.toString())
                _message.value = e.toString()
                _response.value = RESPONSE_TYPE.fail
            }
        }
    }
}
