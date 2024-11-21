package com.appsvgalore.cryptoandroid.util

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptionHelper {

    private const val KEY_SIZE = 256 // AES key size
    private const val ALGORITHM = "AES"
    private lateinit var encryptionKey: SecretKey

    // Generate a new AES key and return it as a Base64 string
    fun generateKey(): String {
        val keyGenerator = KeyGenerator.getInstance(ALGORITHM)
        keyGenerator.init(KEY_SIZE) // Set the desired key size
        val secretKey = keyGenerator.generateKey()
        encryptionKey = secretKey
        return Base64.encodeToString(secretKey.encoded, Base64.DEFAULT)
    }

    // Set the encryption key manually from a Base64 string (useful for loading from Firestore)
    fun setEncryptionKey(base64Key: String) {
        val decodedKey = Base64.decode(base64Key, Base64.DEFAULT)
        encryptionKey = SecretKeySpec(decodedKey, 0, decodedKey.size, ALGORITHM)
    }

    // Encrypt a message using the current key
    fun encryptMessage(message: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(12) // GCM IV size
        SecureRandom().nextBytes(iv) // Generate a random IV

        val gcmParameterSpec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, gcmParameterSpec)

        val encryptedData = cipher.doFinal(message.toByteArray())
        val ivAndEncryptedData = iv + encryptedData // Combine IV and encrypted data
        return Base64.encodeToString(ivAndEncryptedData, Base64.DEFAULT)
    }

    // Decrypt a message using the current key
    fun decryptMessage(encryptedMessage: String): String {
        val ivAndEncryptedData = Base64.decode(encryptedMessage, Base64.DEFAULT)

        val iv = ivAndEncryptedData.copyOfRange(0, 12) // Extract the IV
        val encryptedData = ivAndEncryptedData.copyOfRange(12, ivAndEncryptedData.size) // Extract the data

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmParameterSpec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, encryptionKey, gcmParameterSpec)

        val decryptedData = cipher.doFinal(encryptedData)
        return String(decryptedData)
    }
}