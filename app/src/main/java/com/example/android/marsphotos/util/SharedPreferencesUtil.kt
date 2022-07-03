package com.example.android.marsphotos.util

import android.content.Context
import android.content.SharedPreferences
import com.example.android.marsphotos.pojo.Food
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPreferencesUtil {
    private const val PACKAGE_NAME = "com.example.android.marsphotos"
    private const val KEY_USER_ID = "user_info"
    private const val KEY_FOOD_LIST = "food_list"
    private const val KEY_FOOD_MAP = "food_map"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE)
    }

    fun getUserID(context: Context): String? {
        return getPrefs(context).getString(KEY_USER_ID, null)
    }

    fun saveUserID(context: Context, userID: String) {
        getPrefs(context).edit().putString(KEY_USER_ID, userID).apply()
    }

    fun removeUserID(context: Context) {
        getPrefs(context).edit().remove(KEY_USER_ID).apply()
    }

    fun getFoodList(context: Context): MutableList<Food>? {
        var gson = Gson()
        val foodListString = getPrefs(context).getString(KEY_FOOD_LIST, null)
        val typeToken = object : TypeToken<MutableList<Food>>() {}.type
        return gson.fromJson<MutableList<Food>>(foodListString, typeToken)
    }

    fun saveFoodList(context: Context, foodList: MutableList<Food>) {
        var gson = Gson()
        val foodListString = gson.toJson(foodList)
        getPrefs(context).edit().putString(KEY_FOOD_LIST, foodListString).apply()
    }

    fun getFoodMap(context: Context): MutableMap<Int, Food>? {
        var gson = Gson()
        val foodMapString = getPrefs(context).getString(KEY_FOOD_MAP, null)
        val typeToken = object : TypeToken<MutableMap<Int, Food>>() {}.type
        return gson.fromJson<MutableMap<Int, Food>>(foodMapString, typeToken)
    }

    fun saveFoodMap(context: Context, foodList: MutableMap<Int, Food>) {
        var gson = Gson()
        val foodListString = gson.toJson(foodList)
        getPrefs(context).edit().putString(KEY_FOOD_MAP, foodListString).apply()
    }
}