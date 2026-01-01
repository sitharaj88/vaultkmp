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

package `in`.sitharaj.vaultkmp.entry

import `in`.sitharaj.vaultkmp.VaultStore
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

/**
 * Base interface for type-safe vault entries.
 */
public interface VaultEntry<T> {
    /**
     * The key for this entry.
     */
    public val key: String

    /**
     * Get the current value, or null if not set.
     */
    public suspend fun get(): T?

    /**
     * Get the current value, or the default if not set.
     */
    public suspend fun getOrDefault(): T

    /**
     * Set the value.
     */
    public suspend fun set(value: T)

    /**
     * Remove the value.
     */
    public suspend fun remove()

    /**
     * Check if a value is set.
     */
    public suspend fun isSet(): Boolean

    /**
     * Observe value changes as a Flow.
     */
    public fun asFlow(): Flow<T?>
}

/**
 * Type-safe string entry.
 */
public class StringEntry internal constructor(
    override val key: String,
    private val store: VaultStore,
    private val default: String?
) : VaultEntry<String> {
    override suspend fun get(): String? = store.getString(key)
    override suspend fun getOrDefault(): String = store.getString(key) ?: default ?: ""
    override suspend fun set(value: String): Unit = store.putString(key, value)
    override suspend fun remove(): Unit = store.remove(key)
    override suspend fun isSet(): Boolean = store.contains(key)
    override fun asFlow(): Flow<String?> = store.observeString(key)
}

/**
 * Type-safe int entry.
 */
public class IntEntry internal constructor(
    override val key: String,
    private val store: VaultStore,
    private val default: Int?
) : VaultEntry<Int> {
    override suspend fun get(): Int? = store.getInt(key)
    override suspend fun getOrDefault(): Int = store.getInt(key) ?: default ?: 0
    override suspend fun set(value: Int): Unit = store.putInt(key, value)
    override suspend fun remove(): Unit = store.remove(key)
    override suspend fun isSet(): Boolean = store.contains(key)
    override fun asFlow(): Flow<Int?> = store.observeInt(key)
}

/**
 * Type-safe long entry.
 */
public class LongEntry internal constructor(
    override val key: String,
    private val store: VaultStore,
    private val default: Long?
) : VaultEntry<Long> {
    override suspend fun get(): Long? = store.getLong(key)
    override suspend fun getOrDefault(): Long = store.getLong(key) ?: default ?: 0L
    override suspend fun set(value: Long): Unit = store.putLong(key, value)
    override suspend fun remove(): Unit = store.remove(key)
    override suspend fun isSet(): Boolean = store.contains(key)
    override fun asFlow(): Flow<Long?> = store.observeInt(key).let { 
        throw UnsupportedOperationException("Long flow not implemented") 
    }
}

/**
 * Type-safe boolean entry.
 */
public class BooleanEntry internal constructor(
    override val key: String,
    private val store: VaultStore,
    private val default: Boolean?
) : VaultEntry<Boolean> {
    override suspend fun get(): Boolean? = store.getBoolean(key)
    override suspend fun getOrDefault(): Boolean = store.getBoolean(key) ?: default ?: false
    override suspend fun set(value: Boolean): Unit = store.putBoolean(key, value)
    override suspend fun remove(): Unit = store.remove(key)
    override suspend fun isSet(): Boolean = store.contains(key)
    override fun asFlow(): Flow<Boolean?> = store.observeBoolean(key)
}

/**
 * Type-safe float entry.
 */
public class FloatEntry internal constructor(
    override val key: String,
    private val store: VaultStore,
    private val default: Float?
) : VaultEntry<Float> {
    override suspend fun get(): Float? = store.getFloat(key)
    override suspend fun getOrDefault(): Float = store.getFloat(key) ?: default ?: 0f
    override suspend fun set(value: Float): Unit = store.putFloat(key, value)
    override suspend fun remove(): Unit = store.remove(key)
    override suspend fun isSet(): Boolean = store.contains(key)
    override fun asFlow(): Flow<Float?> = throw UnsupportedOperationException("Float flow not implemented")
}

/**
 * Type-safe double entry.
 */
public class DoubleEntry internal constructor(
    override val key: String,
    private val store: VaultStore,
    private val default: Double?
) : VaultEntry<Double> {
    override suspend fun get(): Double? = store.getDouble(key)
    override suspend fun getOrDefault(): Double = store.getDouble(key) ?: default ?: 0.0
    override suspend fun set(value: Double): Unit = store.putDouble(key, value)
    override suspend fun remove(): Unit = store.remove(key)
    override suspend fun isSet(): Boolean = store.contains(key)
    override fun asFlow(): Flow<Double?> = throw UnsupportedOperationException("Double flow not implemented")
}

/**
 * Type-safe object entry for serializable objects.
 */
public class ObjectEntry<T> internal constructor(
    override val key: String,
    private val store: VaultStore,
    private val serializer: KSerializer<T>,
    private val default: T?
) : VaultEntry<T> {
    override suspend fun get(): T? = store.getObject(key, serializer)
    override suspend fun getOrDefault(): T = store.getObject(key, serializer) ?: default ?: throw NoSuchElementException("No value for key: $key")
    override suspend fun set(value: T): Unit = store.putObject(key, value, serializer)
    override suspend fun remove(): Unit = store.remove(key)
    override suspend fun isSet(): Boolean = store.contains(key)
    override fun asFlow(): Flow<T?> = throw UnsupportedOperationException("Object flow not implemented")
}
