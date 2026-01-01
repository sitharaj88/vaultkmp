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

/**
 * Exception thrown when a vault operation fails.
 *
 * @property key The key that was being accessed when the error occurred
 * @property operation The operation that was being performed
 * @property cause The underlying cause of the failure
 */
public class VaultException(
    public val key: String?,
    public val operation: String,
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause) {
    
    public companion object {
        /**
         * Create an exception for encryption failures.
         */
        public fun encryptionFailed(key: String, cause: Throwable? = null): VaultException {
            return VaultException(
                key = key,
                operation = "encrypt",
                message = "Failed to encrypt value for key '$key'",
                cause = cause
            )
        }
        
        /**
         * Create an exception for decryption failures.
         */
        public fun decryptionFailed(key: String, cause: Throwable? = null): VaultException {
            return VaultException(
                key = key,
                operation = "decrypt",
                message = "Failed to decrypt value for key '$key'. Data may be corrupted.",
                cause = cause
            )
        }
        
        /**
         * Create an exception for storage failures.
         */
        public fun storageFailed(key: String, operation: String, cause: Throwable? = null): VaultException {
            return VaultException(
                key = key,
                operation = operation,
                message = "Storage operation '$operation' failed for key '$key'",
                cause = cause
            )
        }
        
        /**
         * Create an exception for initialization failures.
         */
        public fun initializationFailed(message: String, cause: Throwable? = null): VaultException {
            return VaultException(
                key = null,
                operation = "initialize",
                message = message,
                cause = cause
            )
        }
    }
}
