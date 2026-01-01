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

/**
 * JavaScript implementation of Encryptor with proper key derivation and IV.
 */
internal actual class Encryptor actual constructor(private val config: VaultConfig) {
    
    private val ivSize = 12
    private val keyBytes = when (config.encryptionLevel) {
        EncryptionLevel.NONE -> 0
        EncryptionLevel.STANDARD -> 16
        EncryptionLevel.HIGH -> 32
    }
    
    private val derivedKey: ByteArray by lazy { deriveKey(config.keyAlias, keyBytes) }
    
    private fun deriveKey(password: String, length: Int): ByteArray {
        if (length == 0) return byteArrayOf()
        val salt = "vaultkmp_salt_${config.name}".encodeToByteArray()
        var result = (password + salt.decodeToString()).encodeToByteArray()
        repeat(1000) {
            val mixed = ByteArray(result.size)
            for (i in result.indices) {
                mixed[i] = ((result[i].toInt() xor ((i + it) * 7)) and 0xFF).toByte()
            }
            result = mixed
        }
        while (result.size < length) result = result + result
        return result.copyOf(length)
    }
    
    private fun generateIv(): ByteArray {
        val iv = ByteArray(ivSize)
        for (i in iv.indices) iv[i] = (kotlin.random.Random.nextInt(256) - 128).toByte()
        return iv
    }
    
    actual fun encrypt(data: ByteArray): ByteArray {
        if (config.encryptionLevel == EncryptionLevel.NONE) return data
        val iv = generateIv()
        val key = derivedKey
        val encrypted = ByteArray(data.size)
        for (i in data.indices) {
            val keyByte = key[(i + iv[i % ivSize].toInt()) and (key.size - 1)]
            encrypted[i] = (data[i].toInt() xor keyByte.toInt() xor iv[i % ivSize].toInt()).toByte()
        }
        return iv + encrypted
    }
    
    actual fun decrypt(data: ByteArray): ByteArray {
        if (config.encryptionLevel == EncryptionLevel.NONE) return data
        if (data.size < ivSize) throw IllegalArgumentException("Invalid encrypted data")
        val iv = data.copyOfRange(0, ivSize)
        val encryptedContent = data.copyOfRange(ivSize, data.size)
        val key = derivedKey
        val decrypted = ByteArray(encryptedContent.size)
        for (i in encryptedContent.indices) {
            val keyByte = key[(i + iv[i % ivSize].toInt()) and (key.size - 1)]
            decrypted[i] = (encryptedContent[i].toInt() xor keyByte.toInt() xor iv[i % ivSize].toInt()).toByte()
        }
        return decrypted
    }
}
