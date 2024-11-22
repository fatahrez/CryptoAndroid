package com.appsvgalore.cryptoandroid.domain.repository

import com.appsvgalore.cryptoandroid.domain.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val userData: Flow<UserData>

    suspend fun setUsername(username: String)

    suspend fun setSecurityKey(securityKey: String)

    suspend fun setAccess(access: String)

}