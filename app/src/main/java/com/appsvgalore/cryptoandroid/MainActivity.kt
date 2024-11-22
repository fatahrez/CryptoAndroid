package com.appsvgalore.cryptoandroid

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appsvgalore.cryptoandroid.domain.model.request.Auth
import com.appsvgalore.cryptoandroid.ui.theme.CryptoAndroidTheme
import com.appsvgalore.cryptoandroid.util.EncryptionHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainActivityViewModel = hiltViewModel()
            CryptoAndroidTheme {

                val secretKey = viewModel.key.collectAsStateWithLifecycle(initialValue = null)
                val workingSecretKey = secretKey.value
                Log.i("TAG", "onCreate: activity key: $workingSecretKey")


                LaunchedEffect(viewModel.messageState) {
                    viewModel.getAllMessages()
                }

                val messagesNet = viewModel.messageState.value
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Login",
                            Modifier.clickable {
                                viewModel.login(auth = Auth("fatah", "userpassword"))
                            }
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "send message",
                            Modifier.clickable {
                                if (workingSecretKey != null) {
                                    viewModel.sendMessage("hello", workingSecretKey)
                                }

                            }
                        )

                        Spacer(Modifier.height(16.dp))
                        if (messagesNet.messages.isNotEmpty()) {
                            Text("all messages encrypted")
                            Spacer(Modifier.height(16.dp))
                            messagesNet.messages.forEach {
                                Text(it.content)
                            }
                            Text("all messages decrypted")
                            messagesNet.messages.forEach {
                                if (workingSecretKey != null) {
                                    EncryptionHelper.setEncryptionKey(workingSecretKey)
                                }
                                val decryptedMessage = EncryptionHelper.decryptMessage(it.content)
                                Text(decryptedMessage)
                            }
                        }

                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CryptoAndroidTheme {

    }
}