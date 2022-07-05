package com.example.android.marsphotos.data.db.repository

import com.example.android.marsphotos.data.db.remote.FirebaseDataSource
import com.example.android.marsphotos.data.db.remote.FirebaseReferenceChildObserver
import com.example.android.marsphotos.data.db.remote.FirebaseReferenceValueObserver
import com.example.android.marsphotos.data.Result
import com.example.android.marsphotos.data.constant.TYPE_DISH_LIST
import com.example.android.marsphotos.data.db.entity.*
import com.example.android.marsphotos.util.wrapSnapshotToArrayList
import com.example.android.marsphotos.util.wrapSnapshotToClass


class DatabaseRepository {
    private val firebaseDatabaseService = FirebaseDataSource()

    //region Update
    fun updateUserStatus(userID: String, status: String) {
        firebaseDatabaseService.updateUserStatus(userID, status)
    }

    fun updateNewMessage(messagesID: String, message: Message) {
        firebaseDatabaseService.pushNewMessage(messagesID, message)
    }

    fun updateNewUser(user: User) {
        firebaseDatabaseService.updateNewUser(user)
    }

    fun updateNewFriend(myUser: UserFriend, otherUser: UserFriend) {
        firebaseDatabaseService.updateNewFriend(myUser, otherUser)
    }

    fun updateNewSentRequest(userID: String, userRequest: UserRequest) {
        firebaseDatabaseService.updateNewSentRequest(userID, userRequest)
    }

    fun updateNewNotification(otherUserID: String, userNotification: UserNotification) {
        firebaseDatabaseService.updateNewNotification(otherUserID, userNotification)
    }

    fun updateChatLastMessage(chatID: String, message: Message) {
        firebaseDatabaseService.updateLastMessage(chatID, message)
    }

    fun updateNewChat(chat: Chat) {
        firebaseDatabaseService.updateNewChat(chat)
    }

    fun updateUserProfileImageUrl(userID: String, url: String) {
        firebaseDatabaseService.updateUserProfileImageUrl(userID, url)
    }

    fun updateDishProcessing(billingId: Int, dishList: MutableList<DishInfo>?) {
        firebaseDatabaseService.updateDishProcessing(billingId, dishList)
    }

    //endregion

    //region Remove
    fun removeNotification(userID: String, notificationID: String) {
        firebaseDatabaseService.removeNotification(userID, notificationID)
    }

    fun removeFriend(userID: String, friendID: String) {
        firebaseDatabaseService.removeFriend(userID, friendID)
    }

    fun removeSentRequest(otherUserID: String, myUserID: String) {
        firebaseDatabaseService.removeSentRequest(otherUserID, myUserID)
    }

    fun removeChat(chatID: String) {
        firebaseDatabaseService.removeChat(chatID)
    }

    fun removeMessages(messagesID: String) {
        firebaseDatabaseService.removeMessages(messagesID)
    }

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

    fun loadChat(chatID: String, b: ((Result<Chat>) -> Unit)) {
        firebaseDatabaseService.loadChatTask(chatID).addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToClass(Chat::class.java, it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    //endregion

    //region Load List

    fun loadUsers(b: ((Result<MutableList<User>>) -> Unit)) {
        b.invoke(Result.Loading)
        firebaseDatabaseService.loadUsersTask().addOnSuccessListener {
            val usersList = wrapSnapshotToArrayList(User::class.java, it)
            b.invoke(Result.Success(usersList))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadFriends(userID: String, b: ((Result<List<UserFriend>>) -> Unit)) {
        b.invoke(Result.Loading)
        firebaseDatabaseService.loadFriendsTask(userID).addOnSuccessListener {
            val friendsList = wrapSnapshotToArrayList(UserFriend::class.java, it)
            b.invoke(Result.Success(friendsList))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadNotifications(userID: String, b: ((Result<MutableList<UserNotification>>) -> Unit)) {
        b.invoke(Result.Loading)
        firebaseDatabaseService.loadNotificationsTask(userID).addOnSuccessListener {
            val notificationsList = wrapSnapshotToArrayList(UserNotification::class.java, it)
            b.invoke(Result.Success(notificationsList))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

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

    fun loadAndObserveUser(
        userID: String,
        observer: FirebaseReferenceValueObserver,
        b: ((Result<User>) -> Unit)
    ) {
        firebaseDatabaseService.attachUserObserver(User::class.java, userID, observer, b)
    }

    fun loadAndObserveUserInfo(
        userID: String,
        observer: FirebaseReferenceValueObserver,
        b: ((Result<UserInfo>) -> Unit)
    ) {
        firebaseDatabaseService.attachUserInfoObserver(UserInfo::class.java, userID, observer, b)
    }

    fun loadAndObserveUserNotifications(
        userID: String,
        observer: FirebaseReferenceValueObserver,
        b: ((Result<MutableList<UserNotification>>) -> Unit)
    ) {
        firebaseDatabaseService.attachUserNotificationsObserver(
            UserNotification::class.java,
            userID,
            observer,
            b
        )
    }

    fun loadAndObserveMessagesAdded(
        messagesID: String,
        observer: FirebaseReferenceChildObserver,
        b: ((Result<Message>) -> Unit)
    ) {
        firebaseDatabaseService.attachMessagesObserver(Message::class.java, messagesID, observer, b)
    }

    fun loadAndObserveChat(
        chatID: String,
        observer: FirebaseReferenceValueObserver,
        b: ((Result<Chat>) -> Unit)
    ) {
        firebaseDatabaseService.attachChatObserver(Chat::class.java, chatID, observer, b)
    }

    //endregion
}

