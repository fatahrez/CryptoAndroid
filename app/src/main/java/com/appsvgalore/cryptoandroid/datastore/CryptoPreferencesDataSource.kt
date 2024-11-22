package com.appsvgalore.cryptoandroid.datastore

import androidx.datastore.core.DataStore
import com.appsvgalore.cryptoandroid.domain.model.UserData
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CryptoPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) {
    val userData = userPreferences.data
        .map {
            UserData(
                username = it.username,
                securityKey = it.securityKey,
                access = it.access
            )
        }

    suspend fun setUsername(username: String) {
        userPreferences.updateData {
            it.copy { this.username = username }
        }
    }

    suspend fun setSecurityKey(securityKey: String) {
        userPreferences.updateData {
            it.copy { this.securityKey = securityKey }
        }
    }

    suspend fun setAccess(access: String) {
        userPreferences.updateData {
            it.copy { this.access = access }
        }
    }

}