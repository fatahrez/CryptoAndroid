package com.appsvgalore.cryptoandroid.domain.model.response

data class SignInResponse(
    val refresh: String,
    val access: String,
    val username: String
)
