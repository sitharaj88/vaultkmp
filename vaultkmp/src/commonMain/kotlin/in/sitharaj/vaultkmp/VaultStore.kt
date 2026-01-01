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

import `in`.sitharaj.vaultkmp.entry.*
import `in`.sitharaj.vaultkmp.internal.createPlatformVault
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

/**
 * VaultStore - Secure encrypted key-value storage for Kotlin Multiplatform.
 *
 * Provides type-safe, encrypted storage across Android, iOS, Desktop, and Web platforms.
 *
 * ## Basic Usage
 * ```kotlin
 * val vault = VaultStore.create(VaultConfig.DEFAULT)
 *
 * // Store values
 * vault.putString("token", "abc123")
 * vault.putInt("userId", 42)
 *
 * // Retrieve values
 * val token = vault.getString("token")
 * val userId = vault.getInt("userId")
 * ```
 *
 * ## Type-Safe Entries
 * ```kotlin
 * val tokenEntry = vault.stringEntry("token")
 * tokenEntry.set("abc123")
 * val token = tokenEntry.get()
 * ```
 */
public interface VaultStore {

    /**
     * The configuration used by this vault.
     */
    public val config: VaultConfig

    // ==================== String Operations ====================

    /**
     * Store a string value.
     */
    public suspend fun putString(key: String, value: String)

    /**
     * Retrieve a string value, or null if not found.
     */
    public suspend fun getString(key: String): String?

    /**
     * Retrieve a string value, or default if not found.
     */
    public suspend fun getString(key: String, default: String): String

    // ==================== Int Operations ====================

    /**
     * Store an integer value.
     */
    public suspend fun putInt(key: String, value: Int)

    /**
     * Retrieve an integer value, or null if not found.
     */
    public suspend fun getInt(key: String): Int?

    /**
     * Retrieve an integer value, or default if not found.
     */
    public suspend fun getInt(key: String, default: Int): Int

    // ==================== Long Operations ====================

    /**
     * Store a long value.
     */
    public suspend fun putLong(key: String, value: Long)

    /**
     * Retrieve a long value, or null if not found.
     */
    public suspend fun getLong(key: String): Long?

    /**
     * Retrieve a long value, or default if not found.
     */
    public suspend fun getLong(key: String, default: Long): Long

    // ==================== Boolean Operations ====================

    /**
     * Store a boolean value.
     */
    public suspend fun putBoolean(key: String, value: Boolean)

    /**
     * Retrieve a boolean value, or null if not found.
     */
    public suspend fun getBoolean(key: String): Boolean?

    /**
     * Retrieve a boolean value, or default if not found.
     */
    public suspend fun getBoolean(key: String, default: Boolean): Boolean

    // ==================== Float Operations ====================

    /**
     * Store a float value.
     */
    public suspend fun putFloat(key: String, value: Float)

    /**
     * Retrieve a float value, or null if not found.
     */
    public suspend fun getFloat(key: String): Float?

    /**
     * Retrieve a float value, or default if not found.
     */
    public suspend fun getFloat(key: String, default: Float): Float

    // ==================== Double Operations ====================

    /**
     * Store a double value.
     */
    public suspend fun putDouble(key: String, value: Double)

    /**
     * Retrieve a double value, or null if not found.
     */
    public suspend fun getDouble(key: String): Double?

    /**
     * Retrieve a double value, or default if not found.
     */
    public suspend fun getDouble(key: String, default: Double): Double

    // ==================== ByteArray Operations ====================

    /**
     * Store raw bytes.
     */
    public suspend fun putBytes(key: String, value: ByteArray)

    /**
     * Retrieve raw bytes, or null if not found.
     */
    public suspend fun getBytes(key: String): ByteArray?

    // ==================== Object Operations ====================

    /**
     * Store a serializable object.
     */
    public suspend fun <T> putObject(key: String, value: T, serializer: KSerializer<T>)

    /**
     * Retrieve a serializable object, or null if not found.
     */
    public suspend fun <T> getObject(key: String, serializer: KSerializer<T>): T?

    // ==================== Utility Operations ====================

    /**
     * Check if a key exists.
     */
    public suspend fun contains(key: String): Boolean

    /**
     * Remove a value by key.
     */
    public suspend fun remove(key: String)

    /**
     * Clear all stored values.
     */
    public suspend fun clear()

    /**
     * Get all keys in the vault.
     */
    public suspend fun keys(): Set<String>

    // ==================== Type-Safe Entry Accessors ====================

    /**
     * Create a type-safe string entry.
     */
    public fun stringEntry(key: String, default: String? = null): StringEntry

    /**
     * Create a type-safe int entry.
     */
    public fun intEntry(key: String, default: Int? = null): IntEntry

    /**
     * Create a type-safe long entry.
     */
    public fun longEntry(key: String, default: Long? = null): LongEntry

    /**
     * Create a type-safe boolean entry.
     */
    public fun booleanEntry(key: String, default: Boolean? = null): BooleanEntry

    /**
     * Create a type-safe float entry.
     */
    public fun floatEntry(key: String, default: Float? = null): FloatEntry

    /**
     * Create a type-safe double entry.
     */
    public fun doubleEntry(key: String, default: Double? = null): DoubleEntry

    /**
     * Create a type-safe object entry.
     */
    public fun <T> objectEntry(key: String, serializer: KSerializer<T>, default: T? = null): ObjectEntry<T>

    // ==================== Flow Observers ====================

    /**
     * Observe a string value as a Flow.
     */
    public fun observeString(key: String): Flow<String?>

    /**
     * Observe an int value as a Flow.
     */
    public fun observeInt(key: String): Flow<Int?>

    /**
     * Observe a boolean value as a Flow.
     */
    public fun observeBoolean(key: String): Flow<Boolean?>

    public companion object {
        /**
         * Create a new VaultStore instance.
         */
        public fun create(config: VaultConfig = VaultConfig.DEFAULT): VaultStore {
            return createPlatformVault(config)
        }

        /**
         * Create a new VaultStore with a builder.
         */
        public fun create(block: VaultConfig.Builder.() -> Unit): VaultStore {
            val config = VaultConfig.Builder().apply(block).build()
            return create(config)
        }
    }
}
