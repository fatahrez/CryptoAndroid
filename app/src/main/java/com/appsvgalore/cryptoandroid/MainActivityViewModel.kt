package com.appsvgalore.cryptoandroid

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsvgalore.cryptoandroid.domain.model.request.Auth
import com.appsvgalore.cryptoandroid.domain.repository.CryptoRepository
import com.appsvgalore.cryptoandroid.util.EncryptionHelper
import com.appsvgalore.cryptoandroid.util.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: CryptoRepository
): ViewModel() {

    fun login(auth: Auth) {
        viewModelScope.launch {
            repository.signInUser(auth).collectLatest { result ->
                when(result) {
                    is ResultWrapper.Success -> {
                        var key: String? = null
                        val username = result.values.username
                        repository.fetchEncryptionKey(
                            username,
                            onError = {
                                Log.i("TAG", "login: error")
                                key = null
                                createKey(result.values.username)
                            },
                            onSuccess = {
                                Log.i("TAG", "login: success")
                                key = it
                            }
                        )

                    }
                    is ResultWrapper.NetworkError -> {
                        Log.e("TAG", "login: network error")
                    }
                    is ResultWrapper.Loading -> {
                        Log.i("TAG", "login: loading")
                    }
                    is ResultWrapper.GenericError -> {
                        Log.e("TAG", "login: generic error ${result.errors}")
                    }
                }
            }
        }
    }

    fun signUp(auth: Auth) {
        viewModelScope.launch {
            repository.signUpUser(auth).collectLatest { result ->
                when (result) {
                    is ResultWrapper.Success -> {
                        Log.i("TAG", "signUp: success")
                    }

                    is ResultWrapper.NetworkError -> {
                        Log.e("TAG", "signUp: network error")
                    }

                    is ResultWrapper.Loading -> {
                        Log.i("TAG", "signUp: loading")
                    }

                    is ResultWrapper.GenericError -> {
                        Log.e("TAG", "signUp: generic error ${result.errors}")
                    }
                }
            }
        }
    }

    fun fetchKey(
        username: String
    ) {
        viewModelScope.launch {
            repository.fetchEncryptionKey(
                userId = username,
                onError = {
                    Log.e("TAG", "fetchOrCreateKey: error ${it.message}", )
                },
                onSuccess = {
                    Log.i("TAG", "fetchOrCreateKey: $it")
                }
            )
        }
    }

    fun createKey(
        userId: String
    ) {
        viewModelScope.launch {
            val key = EncryptionHelper.generateKey()
            repository.saveEncryptionKey(
                userId,
                key,
                onSuccess = {
                    Log.i("TAG", "createKey: encryption key success")
                },
                onError = {
                    Log.e("TAG", "createKey: error ${it.message}")
                }
            )
        }
    }
}