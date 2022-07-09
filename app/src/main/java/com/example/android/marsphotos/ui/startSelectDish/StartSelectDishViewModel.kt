package com.example.android.marsphotos.ui.startSelectDish

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.android.marsphotos.App
import com.example.android.marsphotos.data.Result
import com.example.android.marsphotos.data.constant.RESPONSE_TYPE
import com.example.android.marsphotos.network.ProductApi
import com.example.android.marsphotos.ui.DefaultViewModel
import com.example.android.marsphotos.util.SharedPreferencesUtil
import kotlinx.coroutines.launch

class StartSelectDishViewModel : DefaultViewModel() {

    fun getBillingRelativeTable() {
        viewModelScope.launch {
            try {
                val response = SharedPreferencesUtil.getTableID(App.application.applicationContext)
                    ?.let { ProductApi.retrofitService.getBilling(it) }

                if (response != null && response.isSuccess()) {
                    SharedPreferencesUtil.saveBilling(
                        App.application.applicationContext,
                        response.data
                    )
                    _response.value = RESPONSE_TYPE.success
                } else {
                    _response.value = RESPONSE_TYPE.fail
                }
            } catch (e: Exception) {
                Log.e("error", e.toString())
            }
        }
    }
}
