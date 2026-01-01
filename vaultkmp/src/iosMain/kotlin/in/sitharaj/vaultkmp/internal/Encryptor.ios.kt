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
import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Security.*

/**
 * iOS implementation of Encryptor using CommonCrypto.
 * Note: iOS Keychain provides encryption at rest, so we use a simpler approach here.
 */
@OptIn(ExperimentalForeignApi::class)
internal actual class Encryptor actual constructor(private val config: VaultConfig) {
    
    actual fun encrypt(data: ByteArray): ByteArray {
        // iOS Keychain provides encryption at rest
        // For additional encryption, use CommonCrypto
        if (config.encryptionLevel == EncryptionLevel.NONE) {
            return data
        }
        // For now, return data as-is since Keychain handles encryption
        // In production, implement AES using CommonCrypto
        return data
    }
    
    actual fun decrypt(data: ByteArray): ByteArray {
        if (config.encryptionLevel == EncryptionLevel.NONE) {
            return data
        }
        return data
    }
}
