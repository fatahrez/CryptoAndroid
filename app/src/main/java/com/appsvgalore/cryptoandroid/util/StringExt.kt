package com.appsvgalore.cryptoandroid.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

fun String.split(): String {
    return try {
        val index = this.lastIndexOf(":")
        this.substring(index + 1, this.length)
    } catch (e: Exception) {
        e.printStackTrace()
        this
    }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")