package com.example.android.marsphotos.data.constant

const val BASE_URL_NGROK = "https://32d7-42-113-60-62.ap.ngrok.io"
enum class TYPE_DISH_LIST {
    foodDones,
    foodProcessings,
    foodRequests,
}

enum class RESPONSE_TYPE {
    success,
    fail,
    nothing,
}

enum class POSITION_TYPE {
    chef,
    waiter,
    table,
}

const val TIME_DISPLAY_NOTIFY = 3000L
const val FOOD_SELECT_MAX = 10