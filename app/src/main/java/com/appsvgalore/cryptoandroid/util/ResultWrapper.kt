package com.appsvgalore.cryptoandroid.util

sealed class ResultWrapper<out T> {
    data class Success<out T>(val values: T): ResultWrapper<T>()
    data class GenericError(
        val code: Int? = null,
        val errors: ErrorResponse? = null
    ): ResultWrapper<Nothing>()
    object NetworkError: ResultWrapper<Nothing>()
    object Loading: ResultWrapper<Nothing>()
}