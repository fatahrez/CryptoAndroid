package com.appsvgalore.cryptoandroid.domain.model.request

data class SendMessageRequest(
    val receiver_id: Int,
    val content: String
)
