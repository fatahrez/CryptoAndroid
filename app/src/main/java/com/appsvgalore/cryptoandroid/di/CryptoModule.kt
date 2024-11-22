package com.appsvgalore.cryptoandroid.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.appsvgalore.cryptoandroid.data.remote.AuthInterceptor
import com.appsvgalore.cryptoandroid.data.remote.CryptoAPI
import com.appsvgalore.cryptoandroid.data.remote.HttpClient
import com.appsvgalore.cryptoandroid.data.remote.HttpLogger
import com.appsvgalore.cryptoandroid.data.repository.CryptoRepositoryImpl
import com.appsvgalore.cryptoandroid.data.repository.UserDataRepositoryImpl
import com.appsvgalore.cryptoandroid.datastore.UserPreferences
import com.appsvgalore.cryptoandroid.domain.repository.CryptoRepository
import com.appsvgalore.cryptoandroid.domain.repository.UserDataRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
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
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return HttpClient.setupOkHttpClient(
            httpLoggingInterceptor,
            authInterceptor
        )
    }

    @Provides
    fun providesAuthInterceptor(
        userPreferencesDataSource: DataStore<UserPreferences>
    ): AuthInterceptor = AuthInterceptor(
        userPreferencesDataSource
    )

    @Provides
    fun providesIODispatchers(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Provides
    @Singleton
    fun providesLoggingInterceptor(): HttpLoggingInterceptor = HttpLogger.create()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModule {
    @Binds
    internal abstract fun bindsUserDataRepository(
        userDataRepositoryImpl: UserDataRepositoryImpl
    ): UserDataRepository
}