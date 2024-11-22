package com.appsvgalore.cryptoandroid

import com.appsvgalore.cryptoandroid.domain.model.response.MessageResponse

data class MessagesState(
    val messages: List<MessageResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
