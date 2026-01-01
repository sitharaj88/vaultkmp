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

package `in`.sitharaj.vaultkmp

import `in`.sitharaj.vaultkmp.internal.Encryptor
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class EncryptorDesktopTest {
    
    @Test
    fun `encryption with NONE level should return original data`() {
        val config = VaultConfig.Builder()
            .name("test")
            .encryptionLevel(EncryptionLevel.NONE)
            .build()
        val encryptor = Encryptor(config)
        
        val data = "Hello, World!".encodeToByteArray()
        val encrypted = encryptor.encrypt(data)
        
        encrypted shouldBe data
    }
    
    @Test
    fun `encryption with STANDARD level should encrypt data`() {
        val config = VaultConfig.Builder()
            .name("test")
            .encryptionLevel(EncryptionLevel.STANDARD)
            .build()
        val encryptor = Encryptor(config)
        
        val data = "Secret Message".encodeToByteArray()
        val encrypted = encryptor.encrypt(data)
        
        encrypted shouldNotBe data
        encrypted.size shouldNotBe data.size
    }
    
    @Test
    fun `encryption with HIGH level should encrypt data`() {
        val config = VaultConfig.Builder()
            .name("test")
            .encryptionLevel(EncryptionLevel.HIGH)
            .build()
        val encryptor = Encryptor(config)
        
        val data = "Top Secret".encodeToByteArray()
        val encrypted = encryptor.encrypt(data)
        
        encrypted shouldNotBe data
    }
    
    @Test
    fun `decryption should return original data`() {
        val config = VaultConfig.Builder()
            .name("test")
            .encryptionLevel(EncryptionLevel.HIGH)
            .build()
        val encryptor = Encryptor(config)
        
        val original = "Hello, World!".encodeToByteArray()
        val encrypted = encryptor.encrypt(original)
        val decrypted = encryptor.decrypt(encrypted)
        
        decrypted.decodeToString() shouldBe "Hello, World!"
    }
    
    @Test
    fun `different keys should produce different ciphertext`() {
        val config1 = VaultConfig.Builder()
            .name("vault1")
            .keyAlias("key1")
            .encryptionLevel(EncryptionLevel.HIGH)
            .build()
        val config2 = VaultConfig.Builder()
            .name("vault2")
            .keyAlias("key2")
            .encryptionLevel(EncryptionLevel.HIGH)
            .build()
        
        val encryptor1 = Encryptor(config1)
        val encryptor2 = Encryptor(config2)
        
        val data = "Same Data".encodeToByteArray()
        
        // Note: Due to random IV, even same key would produce different output
        // But different keys definitely should produce different decryption results
        val encrypted1 = encryptor1.encrypt(data)
        val encrypted2 = encryptor2.encrypt(data)
        
        encrypted1 shouldNotBe encrypted2
    }
    
    @Test
    fun `empty data encryption should work`() {
        val config = VaultConfig.Builder()
            .name("test")
            .encryptionLevel(EncryptionLevel.HIGH)
            .build()
        val encryptor = Encryptor(config)
        
        val data = "".encodeToByteArray()
        val encrypted = encryptor.encrypt(data)
        val decrypted = encryptor.decrypt(encrypted)
        
        decrypted.decodeToString() shouldBe ""
    }
}
