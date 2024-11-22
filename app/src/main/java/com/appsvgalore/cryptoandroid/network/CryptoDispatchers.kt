package com.appsvgalore.cryptoandroid.network

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val cryptoDispatchers: CryptoDispatchers)

enum class CryptoDispatchers {
    Default,
    IO
}