package com.appsvgalore.cryptoandroid.data.repository

import android.content.Context
import com.appsvgalore.cryptoandroid.data.remote.CryptoAPI
import com.appsvgalore.cryptoandroid.domain.model.request.Auth
import com.appsvgalore.cryptoandroid.domain.model.request.SendMessageRequest
import com.appsvgalore.cryptoandroid.domain.model.response.MessageResponse
import com.appsvgalore.cryptoandroid.domain.model.response.SendMessageResponse
import com.appsvgalore.cryptoandroid.domain.model.response.SignInResponse
import com.appsvgalore.cryptoandroid.domain.model.response.SignUpResponse
import com.appsvgalore.cryptoandroid.domain.repository.CryptoRepository
import com.appsvgalore.cryptoandroid.util.ResultWrapper
import com.appsvgalore.cryptoandroid.util.safeApiCall
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import java.lang.Exception

class CryptoRepositoryImpl(
    private val api: CryptoAPI,
    private val context: Context,
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CryptoRepository {
    override suspend fun signUpUser(auth: Auth): Flow<ResultWrapper<SignUpResponse>> = safeApiCall(ioDispatcher, context) {
        api.signUpUser(auth)
    }

    override suspend fun signInUser(auth: Auth): Flow<ResultWrapper<SignInResponse>> = safeApiCall(ioDispatcher, context) {
        api.signIn(auth)
    }

    override suspend fun sendMessage(sendMessageRequest: SendMessageRequest): Flow<ResultWrapper<SendMessageResponse>> = safeApiCall(ioDispatcher, context) {
        api.sendMessage(sendMessageRequest)
    }

    override suspend fun getAllMessages(): Flow<ResultWrapper<List<MessageResponse>>> = safeApiCall(ioDispatcher, context) {
        api.getAllMessages()
    }

    override suspend fun fetchEncryptionKey(
        userId: String,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        firestore.collection("keys").document(userId).get()
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

    override suspend fun saveEncryptionKey(
        userId: String,
        key: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val data = mapOf("encryption_key" to key)
        firestore.collection("keys").document(userId).set(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener{exception -> onError(exception)}
    }

}