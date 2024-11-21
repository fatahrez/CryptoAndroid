package com.appsvgalore.cryptoandroid.data.remote

import com.appsvgalore.cryptoandroid.domain.model.request.Auth
import com.appsvgalore.cryptoandroid.domain.model.request.SendMessageRequest
import com.appsvgalore.cryptoandroid.domain.model.response.MessageResponse
import com.appsvgalore.cryptoandroid.domain.model.response.SendMessageResponse
import com.appsvgalore.cryptoandroid.domain.model.response.SignInResponse
import com.appsvgalore.cryptoandroid.domain.model.response.SignUpResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CryptoAPI {

    @POST("api/users/register/")
    suspend fun signUpUser(@Body auth: Auth): SignUpResponse

    @POST("api/users/login/")
    suspend fun signIn(@Body auth: Auth): SignInResponse

    @POST("api/messages/send/")
    suspend fun sendMessage(@Body sendMessageRequest: SendMessageRequest): SendMessageResponse

    @GET("api/messages/")
    suspend fun getAllMessages(): List<MessageResponse>
}