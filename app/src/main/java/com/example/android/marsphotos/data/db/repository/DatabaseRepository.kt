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

    fun updateDishRequestsOfBilling(
        billingId: Int,
        dishList: MutableList<DishInfo>?,
        b: ((Result<String>) -> Unit)
    ) {
        b.invoke(Result.Loading)
        firebaseDatabaseService.updateDishRequestsOfBilling(billingId, dishList, b)
            .addOnSuccessListener {
                b.invoke(Result.Success("Success"))
            }.addOnFailureListener {
            b.invoke(Result.Error(it.message))
        }
    }

    fun updateDishOfBilling(
        typeDishList: TYPE_DISH_LIST,
        billingId: Int,
        dishList: MutableList<DishInfo>?,
        b: ((Result<String>) -> Unit)
    ) {
        b.invoke(Result.Loading)
        firebaseDatabaseService.updateDishsOfBilling(typeDishList, billingId, dishList, b)
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

    fun loadDishRequestsOfBillings(
        billingID: Int,
        b: ((Result<MutableList<DishInfo>>) -> Unit)
    ) {
        b.invoke(Result.Loading)
        firebaseDatabaseService.loadDishRequestsOfBillingsTask(billingID).addOnSuccessListener {
            val dishList = wrapSnapshotToArrayList(DishInfo::class.java, it)
            b.invoke(Result.Success(dishList))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }
    //endregion

    //region Load List

    fun loadDishOfBillings(billingID: Int, b: ((Result<BillingInfo>) -> Unit)) {
        b.invoke(Result.Loading)
        firebaseDatabaseService.loadDishOfBillingsTask(billingID).addOnSuccessListener {
            val dishList = wrapSnapshotToClass(BillingInfo::class.java, it)
            b.invoke(Result.Success(dishList))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadDishProcessingOfBillings(
        billingID: Int,
        b: ((Result<MutableList<DishInfo>>) -> Unit)
    ) {
        b.invoke(Result.Loading)
        firebaseDatabaseService.loadDishProcessingOfBillingsTask(billingID).addOnSuccessListener {
            val dishList = wrapSnapshotToArrayList(DishInfo::class.java, it)
            b.invoke(Result.Success(dishList))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadAndObserveDishProcessingOfBillings(
        billingID: Int,
        observer: FirebaseReferenceValueObserver,
        b: ((Result<MutableList<DishInfo>>) -> Unit)
    ) {
        firebaseDatabaseService.attachDishProcessingsObserver(
            DishInfo::class.java,
            billingID,
            observer,
            b
        )
    }

    fun loadAndObserveDishsOfBillings(
        billingID: Int,
        typeDishList: TYPE_DISH_LIST,
        observer: FirebaseReferenceValueObserver,
        b: ((Result<MutableList<DishInfo>>) -> Unit)
    ) {
        firebaseDatabaseService.attachDishsObserver(
            DishInfo::class.java,
            billingID,
            typeDishList,
            observer,
            b
        )
    }

    fun loadAndObserveAllDish(
        observer: FirebaseReferenceValueObserver,
        b: ((Result<MutableList<BillingInfo>>) -> Unit)
    ) {
        firebaseDatabaseService.attachAllDishObserver(
            BillingInfo::class.java,
            observer,
            b
        )
    }

    //endregion

    //#region Load and Observe

    //endregion
}

