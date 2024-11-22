package com.appsvgalore.cryptoandroid

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsvgalore.cryptoandroid.domain.model.request.Auth
import com.appsvgalore.cryptoandroid.domain.model.request.SendMessageRequest
import com.appsvgalore.cryptoandroid.domain.repository.CryptoRepository
import com.appsvgalore.cryptoandroid.domain.repository.UserDataRepository
import com.appsvgalore.cryptoandroid.util.EncryptionHelper
import com.appsvgalore.cryptoandroid.util.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: CryptoRepository,
    private val userDataRepository: UserDataRepository
): ViewModel() {

    val key = userDataRepository.userData.map {
        it.securityKey
    }

    val username = userDataRepository.userData.map {
        it.username
    }

    private val _messageState = mutableStateOf(MessagesState())
    val messageState: State<MessagesState> = _messageState

    fun login(auth: Auth) {
        viewModelScope.launch {
            repository.signInUser(auth).collectLatest { result ->
                when(result) {
                    is ResultWrapper.Success -> {
                        val username = result.values.username
                        val access = result.values.access
                        repository.fetchEncryptionKey(
                            username,
                            onError = {
                                Log.i("TAG", "login: error")
                                createKey(username, access)
                            },
                            onSuccess = {
                                Log.i("TAG", "login: success")
                                saveDataToDataStore(username, it, access)
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
        userId: String,
        access: String
    ) {
        viewModelScope.launch {
            val key = EncryptionHelper.generateKey()
            repository.saveEncryptionKey(
                userId,
                key,
                onSuccess = {
                    Log.i("TAG", "createKey: encryption key success")
                    saveDataToDataStore(userId, key, access)
                },
                onError = {
                    Log.e("TAG", "createKey: error ${it.message}")
                }
            )
        }
    }

    fun sendMessage(message: String, secretKey: String) {
        viewModelScope.launch {
            EncryptionHelper.setEncryptionKey(secretKey)
            val encryptedMessage = EncryptionHelper.encryptMessage(message)
            val sendMessageRequest = SendMessageRequest(
                receiver_id = 3,
                content = encryptedMessage
            )
            repository.sendMessage(sendMessageRequest).collectLatest { result ->
                when(result) {
                    is ResultWrapper.Success -> {
                        Log.i("TAG", "sendMessage: success")
                    }
                    is ResultWrapper.Loading -> {
                        Log.i("TAG", "sendMessage: loading")
                    }
                    is ResultWrapper.GenericError -> {
                        Log.e("TAG", "sendMessage: error ${result.errors!!.message}")
                    }
                    is ResultWrapper.NetworkError -> {
                        Log.e("TAG", "sendMessage: network error")
                    }
                }
            }
        }
    }

    fun getAllMessages() {
        viewModelScope.launch {
            repository.getAllMessages().collectLatest { result ->
                when(result) {
                    is ResultWrapper.Success -> {
                        _messageState.value = messageState.value.copy(
                            messages = result.values,
                            isLoading = false,
                            error = null
                        )
                    }
                    is ResultWrapper.Loading -> {
                        _messageState.value = messageState.value.copy(
                            messages = emptyList(),
                            isLoading = true,
                            error = null
                        )
                    }
                    is ResultWrapper.GenericError -> {
                        _messageState.value = messageState.value.copy(
                            messages = emptyList(),
                            isLoading = false,
                            error = result.errors?.message
                        )
                    }
                    is ResultWrapper.NetworkError -> {
                        _messageState.value = messageState.value.copy(
                            messages = emptyList(),
                            isLoading = false,
                            error = "network error"
                        )
                    }
                }
            }
        }
    }

    fun saveDataToDataStore(
        username: String,
        securityKey: String,
        access: String
    ) {
        viewModelScope.launch {
            userDataRepository.setUsername(username)
            userDataRepository.setSecurityKey(securityKey)
            userDataRepository.setAccess(access)
        }
    }
}
