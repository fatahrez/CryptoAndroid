package com.appsvgalore.cryptoandroid

import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

object FirebaseHelper  {

    private val db = FirebaseFirestore.getInstance()

    fun fetchEncryptionKey(userId: String, onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        db.collection("keys").document(userId).get()
            .addOnSuccessListener { document ->
                val key = document.getString("encryption_key")
                if (key != null) {
                    onSuccess(key)
                } else {
                    onError(Exception("Key not found"))
                }
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }

    fun saveEncryptionKey(userId: String, key: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val data = mapOf("encryption_key" to key)
        db.collection("keys").document(userId).set(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener{exception -> onError(exception)}
    }

}