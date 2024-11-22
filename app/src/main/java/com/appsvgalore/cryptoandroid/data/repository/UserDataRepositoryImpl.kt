package com.appsvgalore.cryptoandroid.data.repository

import com.appsvgalore.cryptoandroid.datastore.CryptoPreferencesDataSource
import com.appsvgalore.cryptoandroid.domain.model.UserData
import com.appsvgalore.cryptoandroid.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserDataRepositoryImpl @Inject constructor(
    private val cryptoPreferencesDataSource: CryptoPreferencesDataSource
): UserDataRepository {
    override val userData: Flow<UserData> =
        cryptoPreferencesDataSource.userData

    override suspend fun setUsername(username: String) {
        cryptoPreferencesDataSource.setUsername(username)
    }

    override suspend fun setSecurityKey(securityKey: String) {
        cryptoPreferencesDataSource.setSecurityKey(securityKey)
    }

    override suspend fun setAccess(access: String) {
        cryptoPreferencesDataSource.setAccess(access)
    }

}