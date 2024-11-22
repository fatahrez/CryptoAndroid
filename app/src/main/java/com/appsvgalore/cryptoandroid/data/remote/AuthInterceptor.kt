package com.appsvgalore.cryptoandroid.data.remote

import android.util.Log
import androidx.datastore.core.DataStore
import com.appsvgalore.cryptoandroid.datastore.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val userPreferencesDataSource: DataStore<UserPreferences>
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            userPreferencesDataSource.data
                .map { preferences -> preferences.access }
                .first()
        }

        Log.i("TAG", "intercept: $token in interceptor")

        val request = chain.request()

        return if (token.isNullOrEmpty()) {
            chain.proceed(request)
        } else {
            val newRequest = request.newBuilder().header(
                "Authorization", "Bearer $token"
            ).build()
            chain.proceed(newRequest)
        }
    }
}