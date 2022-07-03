package com.example.android.marsphotos.data.db.repository

import com.example.android.marsphotos.data.model.Login
import com.example.android.marsphotos.data.db.remote.FirebaseAuthSource
import com.example.android.marsphotos.data.db.remote.FirebaseAuthStateObserver
import com.example.android.marsphotos.data.Result
import com.google.firebase.auth.FirebaseUser

class AuthRepository{
    private val firebaseAuthService = FirebaseAuthSource()

    fun observeAuthState(stateObserver: FirebaseAuthStateObserver, b: ((Result<FirebaseUser>) -> Unit)){
        firebaseAuthService.attachAuthStateObserver(stateObserver,b)
    }

    fun loginUser(login: Login, b: (Result<FirebaseUser>) -> Unit) {
        b.invoke(Result.Loading)
        firebaseAuthService.loginWithEmailAndPassword(login).addOnSuccessListener {
            b.invoke(Result.Success(it.user))
        }.addOnFailureListener {
            b.invoke(Result.Error(msg = it.message))
        }
    }

    fun logoutUser() {
        firebaseAuthService.logout()
    }
}