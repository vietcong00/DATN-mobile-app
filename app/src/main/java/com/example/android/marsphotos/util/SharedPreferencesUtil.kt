package com.example.android.marsphotos.util

import android.content.Context
import android.content.SharedPreferences
import com.example.android.marsphotos.data.db.entity.Billing
import com.example.android.marsphotos.data.db.entity.Food
import com.example.android.marsphotos.data.db.entity.Table
import com.example.android.marsphotos.data.db.entity.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPreferencesUtil {
    private const val PACKAGE_NAME = "com.example.android.marsphotos"
    private const val KEY_USER_ID = "user_info"
    private const val KEY_FOOD_LIST = "food_list"
    private const val KEY_FOOD_MAP = "food_map"
    private const val KEY_BILLING = "billing"
    private const val KEY_TABLE_ID = "table_map"
    private const val KEY_TABLE = "table"
    private const val KEY_USER = "user"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE)
    }

    fun getUserID(context: Context): String? {
        return getPrefs(context).getString(KEY_USER_ID, null)
    }

    fun saveUserID(context: Context, userID: String) {
        getPrefs(context).edit().putString(KEY_USER_ID, userID).apply()
    }

    private fun removeUserID(context: Context) {
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

    fun getBilling(context: Context): Billing? {
        var gson = Gson()
        val billingString = getPrefs(context).getString(KEY_BILLING, null)
        val typeToken = object : TypeToken<Billing>() {}.type
        return gson.fromJson<Billing>(billingString, typeToken)
    }

    fun saveBilling(context: Context, billing: Billing) {
        var gson = Gson()
        val billingString = gson.toJson(billing)
        getPrefs(context).edit().putString(KEY_BILLING, billingString).apply()
    }

    fun removeBilling(context: Context) {
        getPrefs(context).edit().remove(KEY_BILLING).apply()
    }

    fun getTableID(context: Context): Int? {
        return getPrefs(context).getString(KEY_TABLE_ID, null)?.toInt()
    }

    fun saveTableID(context: Context, ID: Int?) {
        getPrefs(context).edit().putString(KEY_TABLE_ID, ID.toString()).apply()
    }

    fun removeTableID(context: Context) {
        getPrefs(context).edit().remove(KEY_TABLE_ID).apply()
    }

    fun getTable(context: Context): Table? {
        var gson = Gson()
        val tableString = getPrefs(context).getString(KEY_TABLE, null)
        val typeToken = object : TypeToken<Table>() {}.type
        return gson.fromJson<Table>(tableString, typeToken)
    }

    fun saveTable(context: Context, table: Table) {
        var gson = Gson()
        val tableString = gson.toJson(table)
        getPrefs(context).edit().putString(KEY_TABLE, tableString).apply()
    }

    fun removeTable(context: Context) {
        getPrefs(context).edit().remove(KEY_TABLE).apply()
    }

    fun getUser(context: Context): User? {
        var gson = Gson()
        val userString = getPrefs(context).getString(KEY_USER, null)
        val typeToken = object : TypeToken<User>() {}.type
        return gson.fromJson<User>(userString, typeToken)
    }

    fun saveUser(context: Context, user: User) {
        var gson = Gson()
        val userString = gson.toJson(user)
        getPrefs(context).edit().putString(KEY_USER, userString).apply()
    }

    fun removeUser(context: Context) {
        getPrefs(context).edit().remove(KEY_USER).apply()
    }

    fun logout(context: Context){
        removeUserID(context);
        removeUser(context);
        removeBilling(context);
        removeTableID(context);
        removeTable(context);
    }
}