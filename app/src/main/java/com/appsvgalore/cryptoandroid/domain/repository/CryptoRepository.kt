package com.appsvgalore.cryptoandroid.domain.repository

import com.appsvgalore.cryptoandroid.domain.model.request.Auth
import com.appsvgalore.cryptoandroid.domain.model.request.SendMessageRequest
import com.appsvgalore.cryptoandroid.domain.model.response.MessageResponse
import com.appsvgalore.cryptoandroid.domain.model.response.SendMessageResponse
import com.appsvgalore.cryptoandroid.domain.model.response.SignInResponse
import com.appsvgalore.cryptoandroid.domain.model.response.SignUpResponse
import com.appsvgalore.cryptoandroid.util.ResultWrapper
import kotlinx.coroutines.flow.Flow
import java.lang.Exception

interface CryptoRepository {

    suspend fun signUpUser(auth: Auth): Flow<ResultWrapper<SignUpResponse>>

    suspend fun signInUser(auth: Auth): Flow<ResultWrapper<SignInResponse>>

    suspend fun sendMessage(sendMessageRequest: SendMessageRequest): Flow<ResultWrapper<SendMessageResponse>>

    suspend fun getAllMessages(): Flow<ResultWrapper<List<MessageResponse>>>

    suspend fun fetchEncryptionKey(userId: String, onSuccess: (String) -> Unit, onError: (Exception) -> Unit)

    suspend fun saveEncryptionKey(userId: String, key: String, onSuccess: () -> Unit, onError: (Exception) -> Unit)
}