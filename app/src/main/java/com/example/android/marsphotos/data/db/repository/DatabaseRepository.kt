package com.example.android.marsphotos.data.db.repository

import com.example.android.marsphotos.data.db.remote.FirebaseDataSource
import com.example.android.marsphotos.data.db.remote.FirebaseReferenceValueObserver
import com.example.android.marsphotos.data.Result
import com.example.android.marsphotos.data.constant.TYPE_DISH_LIST
import com.example.android.marsphotos.data.db.entity.*
import com.example.android.marsphotos.util.wrapSnapshotToArrayList
import com.example.android.marsphotos.util.wrapSnapshotToClass


class DatabaseRepository {
    private val firebaseDatabaseService = FirebaseDataSource()

    fun updateFoodRequestsOfBilling(
        billingId: Int,
        foodList: MutableList<FoodInfo>?,
        b: ((Result<String>) -> Unit)
    ) {
        b.invoke(Result.Loading)
        firebaseDatabaseService.updateFoodRequestsOfBilling(billingId, foodList, b)
            .addOnSuccessListener {
                b.invoke(Result.Success("Success"))
            }.addOnFailureListener {
                b.invoke(Result.Error(it.message))
            }
    }

    fun updateFoodOfBilling(
        typeFoodList: TYPE_DISH_LIST,
        billingId: Int,
        foodList: MutableList<FoodInfo>?,
        b: ((Result<String>) -> Unit)
    ) {
        b.invoke(Result.Loading)
        firebaseDatabaseService.updateFoodsOfBilling(typeFoodList, billingId, foodList, b)
            .addOnSuccessListener {
                b.invoke(Result.Success("Success"))
            }.addOnFailureListener {
                b.invoke(Result.Error(it.message))
            }
    }

    //endregion

    //region Remove

    //endregion

    //region Load Single

    fun loadUser(userID: String, b: ((Result<User>) -> Unit)) {
        firebaseDatabaseService.loadUserTask(userID).addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToClass(User::class.java, it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadUserInfo(userID: String, b: ((Result<UserInfo>) -> Unit)) {
        firebaseDatabaseService.loadUserInfoTask(userID).addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToClass(UserInfo::class.java, it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadFoodRequestsOfBillings(
        billingID: Int,
        b: ((Result<MutableList<FoodInfo>>) -> Unit)
    ) {
        b.invoke(Result.Loading)
        firebaseDatabaseService.loadFoodRequestsOfBillingsTask(billingID).addOnSuccessListener {
            val foodList = wrapSnapshotToArrayList(FoodInfo::class.java, it)
            b.invoke(Result.Success(foodList))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }
    //endregion

    //region Remove
    fun removeBilling(billingId: Int) {
        firebaseDatabaseService.removeBilling(billingId)
    }
    //endregion

    //region Load List

    fun loadFoodOfBillings(billingID: Int, b: ((Result<BillingInfo>) -> Unit)) {
        b.invoke(Result.Loading)
        firebaseDatabaseService.loadFoodOfBillingsTask(billingID).addOnSuccessListener {
            val foodList = wrapSnapshotToClass(BillingInfo::class.java, it)
            b.invoke(Result.Success(foodList))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadFoodOfBillingsByType(
        billingID: Int,
        typeFoodList: TYPE_DISH_LIST,
        b: ((Result<MutableList<FoodInfo>>) -> Unit)
    ) {
        b.invoke(Result.Loading)
        firebaseDatabaseService.loadFoodOfBillingsByTypeTask(billingID,typeFoodList).addOnSuccessListener {
            val foodList = wrapSnapshotToArrayList(FoodInfo::class.java, it)
            b.invoke(Result.Success(foodList))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadFoodProcessingOfBillings(
        billingID: Int,
        b: ((Result<MutableList<FoodInfo>>) -> Unit)
    ) {
        b.invoke(Result.Loading)
        firebaseDatabaseService.loadFoodProcessingOfBillingsTask(billingID).addOnSuccessListener {
            val foodList = wrapSnapshotToArrayList(FoodInfo::class.java, it)
            b.invoke(Result.Success(foodList))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadAndObserveFoodProcessingOfBillings(
        billingID: Int,
        observer: FirebaseReferenceValueObserver,
        b: ((Result<MutableList<FoodInfo>>) -> Unit)
    ) {
        firebaseDatabaseService.attachFoodProcessingsObserver(
            FoodInfo::class.java,
            billingID,
            observer,
            b
        )
    }

    fun loadAndObserveFoodsOfBillings(
        billingID: Int,
        typeFoodList: TYPE_DISH_LIST,
        observer: FirebaseReferenceValueObserver,
        b: ((Result<MutableList<FoodInfo>>) -> Unit)
    ) {
        firebaseDatabaseService.attachFoodsObserver(
            FoodInfo::class.java,
            billingID,
            typeFoodList,
            observer,
            b
        )
    }

    fun loadAndObserveAllFood(
        observer: FirebaseReferenceValueObserver,
        b: ((Result<MutableList<BillingInfo>>) -> Unit)
    ) {
        firebaseDatabaseService.attachAllFoodObserver(
            BillingInfo::class.java,
            observer,
            b
        )
    }

    //endregion

    //#region Load and Observe

    //endregion
}

