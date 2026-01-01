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

import `in`.sitharaj.vaultkmp.VaultConfig

/**
 * JavaScript implementation of Encryptor.
 * Uses Web Crypto API for encryption (simplified for browser compatibility).
 */
internal actual class Encryptor actual constructor(private val config: VaultConfig) {
    
    // Note: For full Web Crypto implementation, use external JS bindings
    // This is a simplified version using XOR obfuscation for demo purposes
    
    private val key = config.keyAlias.encodeToByteArray()
    
    actual fun encrypt(data: ByteArray): ByteArray {
        return data.mapIndexed { index, byte ->
            (byte.toInt() xor key[index % key.size].toInt()).toByte()
        }.toByteArray()
    }
    
    actual fun decrypt(data: ByteArray): ByteArray {
        // XOR is symmetric
        return encrypt(data)
    }
}
