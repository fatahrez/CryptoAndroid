package com.appsvgalore.cryptoandroid.di

import android.content.Context
import com.appsvgalore.cryptoandroid.data.remote.CryptoAPI
import com.appsvgalore.cryptoandroid.data.remote.HttpClient
import com.appsvgalore.cryptoandroid.data.remote.HttpLogger
import com.appsvgalore.cryptoandroid.data.repository.CryptoRepositoryImpl
import com.appsvgalore.cryptoandroid.domain.repository.CryptoRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CryptoModule {

    @Singleton
    @Provides
    fun providesApplicationContext(@ApplicationContext applicationContext: Context): Context {
        return applicationContext
    }

    @Singleton
    @Provides
    fun providesFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun providesCryptoRepository(
        firestore: FirebaseFirestore,
        context: Context,
        api: CryptoAPI
    ): CryptoRepository {
        return CryptoRepositoryImpl(api, context, firestore)
    }

    @Singleton
    @Provides
    fun providesCryptoAPI(okHttpClient: OkHttpClient): CryptoAPI {
        return  Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(CryptoAPI::class.java)
    }

    @Provides
    fun providesOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return HttpClient.setupOkHttpClient(
            httpLoggingInterceptor
        )
    }

    @Provides
    fun providesIODispatchers(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Provides
    @Singleton
    fun providesLoggingInterceptor(): HttpLoggingInterceptor = HttpLogger.create()
}