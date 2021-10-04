package com.android.my.organizze.helper

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object ConfiguracaoFirebase {
    private lateinit var userAuth: FirebaseAuth
    private lateinit var firebase: DatabaseReference

    //retorna instância do FirebaseDataBase
    fun getFirebaseDatabase(): DatabaseReference {
        firebase = FirebaseDatabase.getInstance().reference
        return firebase
    }

    //retorna instância do FirebaseAuth
    fun getFirebaseAutentification(): FirebaseAuth {
        userAuth = FirebaseAuth.getInstance()
        return userAuth
    }
}