package com.example.android.marsphotos.data.constant

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