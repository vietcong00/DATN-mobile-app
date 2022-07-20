package com.example.android.marsphotos.ui.setting

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.android.marsphotos.R
import com.example.android.marsphotos.data.constant.POSITION_TYPE

@BindingAdapter("bind_avatar")
fun bindAvatar(imgView: ImageView, position: POSITION_TYPE?) {
    when(position){
        POSITION_TYPE.chef->imgView.setImageResource(R.drawable.ic_chef)
        POSITION_TYPE.waiter->imgView.setImageResource(R.drawable.ic_man_waiter)
    }
}

