/*
 * Copyright 2024 Sitharaj Seenivasan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package `in`.sitharaj.vaultkmp.internal

import `in`.sitharaj.vaultkmp.EncryptionLevel
import `in`.sitharaj.vaultkmp.VaultConfig
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Desktop/JVM implementation of Encryptor using AES-GCM.
 */
internal actual class Encryptor actual constructor(private val config: VaultConfig) {
    
    private val secretKey: SecretKeySpec by lazy {
        deriveKey(config.keyAlias)
    }
    
    private val keySize: Int = when (config.encryptionLevel) {
        EncryptionLevel.NONE -> 0
        EncryptionLevel.STANDARD -> 128
        EncryptionLevel.HIGH -> 256
    }
    
    companion object {
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH = 128
        private const val GCM_IV_LENGTH = 12
        private const val PBKDF2_ITERATIONS = 65536
        private val SALT = "VaultKMP_Salt_v1".toByteArray()
    }
    
    private fun deriveKey(password: String): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), SALT, PBKDF2_ITERATIONS, keySize)
        val secret = factory.generateSecret(spec)
        return SecretKeySpec(secret.encoded, "AES")
    }
    
    actual fun encrypt(data: ByteArray): ByteArray {
        if (config.encryptionLevel == EncryptionLevel.NONE) {
            return data
        }
        
        val iv = ByteArray(GCM_IV_LENGTH)
        SecureRandom().nextBytes(iv)
        
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)
        
        val encrypted = cipher.doFinal(data)
        
        // Prepend IV to encrypted data
        return iv + encrypted
    }
    
    actual fun decrypt(data: ByteArray): ByteArray {
        if (config.encryptionLevel == EncryptionLevel.NONE) {
            return data
        }
        
        val iv = data.copyOfRange(0, GCM_IV_LENGTH)
        val encrypted = data.copyOfRange(GCM_IV_LENGTH, data.size)
        
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        
        return cipher.doFinal(encrypted)
    }
}
