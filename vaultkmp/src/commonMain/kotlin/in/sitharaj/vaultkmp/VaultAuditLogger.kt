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
 * Audit logger for tracking vault operations.
 * 
 * Implement this interface to receive audit logs for all vault operations.
 * Useful for security compliance, debugging, and analytics.
 */
public interface VaultAuditLogger {
    /**
     * Log a vault operation.
     * 
     * @param operation The operation type (PUT, GET, REMOVE, CLEAR)
     * @param key The key accessed (null for CLEAR)
     * @param success Whether the operation succeeded
     * @param error Optional error message if the operation failed
     */
    public fun log(
        operation: VaultOperation,
        key: String?,
        success: Boolean,
        error: String? = null
    )
}

/**
 * Types of vault operations that can be logged.
 */
public enum class VaultOperation {
    PUT,
    GET,
    REMOVE,
    CLEAR,
    CONTAINS,
    KEYS
}

/**
 * Default console audit logger for debugging.
 */
public object ConsoleAuditLogger : VaultAuditLogger {
    override fun log(operation: VaultOperation, key: String?, success: Boolean, error: String?) {
        val status = if (success) "✓" else "✗"
        val keyInfo = key?.let { " key=$it" } ?: ""
        val errorInfo = error?.let { " error=$it" } ?: ""
        println("[VaultKMP] $status $operation$keyInfo$errorInfo")
    }
}

/**
 * Silent logger that discards all logs (default).
 */
public object NoOpAuditLogger : VaultAuditLogger {
    override fun log(operation: VaultOperation, key: String?, success: Boolean, error: String?) {
        // No-op
    }
}
