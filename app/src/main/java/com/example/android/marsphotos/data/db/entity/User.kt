package com.example.android.marsphotos.data.db.entity

import com.example.android.marsphotos.data.constant.POSITION_TYPE
import com.google.firebase.database.PropertyName

data class User(
    @get:PropertyName("info") @set:PropertyName("info") var info: UserInfo = UserInfo(),
)

data class UserInfo(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("displayName") @set:PropertyName("displayName") var displayName: String? = "",
    @get:PropertyName("position") @set:PropertyName("position") var position: POSITION_TYPE = POSITION_TYPE.chef,
    @get:PropertyName("tableId") @set:PropertyName("tableId") var tableId: Int? = 0,
    @get:PropertyName("online") @set:PropertyName("online") var online: Boolean = false
)