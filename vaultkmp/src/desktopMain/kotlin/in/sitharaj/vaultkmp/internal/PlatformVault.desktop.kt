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
import `in`.sitharaj.vaultkmp.VaultOperation
import `in`.sitharaj.vaultkmp.VaultStore
import `in`.sitharaj.vaultkmp.entry.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*
import java.util.Arrays as JavaArrays

/**
 * Factory function to create Desktop VaultStore.
 */
internal actual fun createPlatformVault(config: VaultConfig): VaultStore {
    return DesktopVault(config)
}

/**
 * Desktop/JVM implementation of VaultStore using encrypted file storage.
 * 
 * Features:
 * - AES-256-GCM encryption with PBKDF2 key derivation
 * - Thread-safe operations using Mutex
 * - Audit logging support
 * - Files stored in ~/.vaultkmp/{vault_name}/
 */
internal class DesktopVault(
    override val config: VaultConfig
) : VaultStore {
    
    private val encryptor = Encryptor(config)
    private val json = Json { ignoreUnknownKeys = true }
    private val observableCache = mutableMapOf<String, MutableStateFlow<String?>>()
    private val mutex = Mutex() // Thread safety
    private val logger = config.auditLogger
    
    private val storageDir: File by lazy {
        val userHome = System.getProperty("user.home")
        File(userHome, ".vaultkmp/${config.name}").apply { mkdirs() }
    }
    
    private fun keyToFile(key: String): File {
        val safeKey = Base64.getEncoder().encodeToString(key.toByteArray())
        return File(storageDir, "$safeKey.vault")
    }
    
    private fun encryptAndSave(key: String, value: String) {
        val encrypted = encryptor.encrypt(value.toByteArray())
        keyToFile(key).writeBytes(encrypted)
        notifyObservers(key, value)
    }
    
    private fun loadAndDecrypt(key: String): String? {
        val file = keyToFile(key)
        if (!file.exists()) return null
        
        val encrypted = file.readBytes()
        return encryptor.decrypt(encrypted).decodeToString()
    }
    
    private fun notifyObservers(key: String, value: String?) {
        observableCache[key]?.value = value
    }
    
    /**
     * Clear sensitive data from byte array.
     */
    private fun clearArray(array: ByteArray) {
        JavaArrays.fill(array, 0.toByte())
    }
    
    // ==================== String Operations ====================
    
    override suspend fun putString(key: String, value: String): Unit = mutex.withLock {
        try {
            encryptAndSave(key, value)
            logger.log(VaultOperation.PUT, key, true)
        } catch (e: Exception) {
            logger.log(VaultOperation.PUT, key, false, e.message)
            throw e
        }
    }
    
    override suspend fun getString(key: String): String? = mutex.withLock {
        try {
            val result = loadAndDecrypt(key)
            logger.log(VaultOperation.GET, key, true)
            result
        } catch (e: Exception) {
            logger.log(VaultOperation.GET, key, false, e.message)
            null
        }
    }
    
    override suspend fun getString(key: String, default: String): String {
        return getString(key) ?: default
    }
    
    // ==================== Int Operations ====================
    
    override suspend fun putInt(key: String, value: Int) {
        putString(key, value.toString())
    }
    
    override suspend fun getInt(key: String): Int? {
        return getString(key)?.toIntOrNull()
    }
    
    override suspend fun getInt(key: String, default: Int): Int {
        return getInt(key) ?: default
    }
    
    // ==================== Long Operations ====================
    
    override suspend fun putLong(key: String, value: Long) {
        putString(key, value.toString())
    }
    
    override suspend fun getLong(key: String): Long? {
        return getString(key)?.toLongOrNull()
    }
    
    override suspend fun getLong(key: String, default: Long): Long {
        return getLong(key) ?: default
    }
    
    // ==================== Boolean Operations ====================
    
    override suspend fun putBoolean(key: String, value: Boolean) {
        putString(key, value.toString())
    }
    
    override suspend fun getBoolean(key: String): Boolean? {
        return getString(key)?.toBooleanStrictOrNull()
    }
    
    override suspend fun getBoolean(key: String, default: Boolean): Boolean {
        return getBoolean(key) ?: default
    }
    
    // ==================== Float Operations ====================
    
    override suspend fun putFloat(key: String, value: Float) {
        putString(key, value.toString())
    }
    
    override suspend fun getFloat(key: String): Float? {
        return getString(key)?.toFloatOrNull()
    }
    
    override suspend fun getFloat(key: String, default: Float): Float {
        return getFloat(key) ?: default
    }
    
    // ==================== Double Operations ====================
    
    override suspend fun putDouble(key: String, value: Double) {
        putString(key, value.toString())
    }
    
    override suspend fun getDouble(key: String): Double? {
        return getString(key)?.toDoubleOrNull()
    }
    
    override suspend fun getDouble(key: String, default: Double): Double {
        return getDouble(key) ?: default
    }
    
    // ==================== ByteArray Operations ====================
    
    override suspend fun putBytes(key: String, value: ByteArray): Unit = mutex.withLock {
        try {
            val encrypted = encryptor.encrypt(value)
            keyToFile(key).writeBytes(encrypted)
            logger.log(VaultOperation.PUT, key, true)
        } catch (e: Exception) {
            logger.log(VaultOperation.PUT, key, false, e.message)
            throw e
        }
    }
    
    override suspend fun getBytes(key: String): ByteArray? = mutex.withLock {
        try {
            val file = keyToFile(key)
            if (!file.exists()) {
                logger.log(VaultOperation.GET, key, true)
                return@withLock null
            }
            val encrypted = file.readBytes()
            val result = encryptor.decrypt(encrypted)
            logger.log(VaultOperation.GET, key, true)
            result
        } catch (e: Exception) {
            logger.log(VaultOperation.GET, key, false, e.message)
            null
        }
    }
    
    // ==================== Object Operations ====================
    
    override suspend fun <T> putObject(key: String, value: T, serializer: KSerializer<T>) {
        val jsonString = json.encodeToString(serializer, value)
        putString(key, jsonString)
    }
    
    override suspend fun <T> getObject(key: String, serializer: KSerializer<T>): T? {
        val jsonString = getString(key) ?: return null
        return try {
            json.decodeFromString(serializer, jsonString)
        } catch (e: Exception) {
            null
        }
    }
    
    // ==================== Utility Operations ====================
    
    override suspend fun contains(key: String): Boolean = mutex.withLock {
        val result = keyToFile(key).exists()
        logger.log(VaultOperation.CONTAINS, key, true)
        result
    }
    
    override suspend fun remove(key: String): Unit = mutex.withLock {
        try {
            keyToFile(key).delete()
            notifyObservers(key, null)
            logger.log(VaultOperation.REMOVE, key, true)
        } catch (e: Exception) {
            logger.log(VaultOperation.REMOVE, key, false, e.message)
        }
    }
    
    override suspend fun clear(): Unit = mutex.withLock {
        try {
            storageDir.listFiles()?.forEach { it.delete() }
            observableCache.values.forEach { it.value = null }
            logger.log(VaultOperation.CLEAR, null, true)
        } catch (e: Exception) {
            logger.log(VaultOperation.CLEAR, null, false, e.message)
        }
    }
    
    override suspend fun keys(): Set<String> = mutex.withLock {
        try {
            val result = storageDir.listFiles()
                ?.filter { it.extension == "vault" }
                ?.mapNotNull { file ->
                    try {
                        String(Base64.getDecoder().decode(file.nameWithoutExtension))
                    } catch (e: Exception) {
                        null
                    }
                }
                ?.toSet() ?: emptySet()
            logger.log(VaultOperation.KEYS, null, true)
            result
        } catch (e: Exception) {
            logger.log(VaultOperation.KEYS, null, false, e.message)
            emptySet()
        }
    }
    
    // ==================== Type-Safe Entry Accessors ====================
    
    override fun stringEntry(key: String, default: String?): StringEntry {
        return StringEntry(key, this, default)
    }
    
    override fun intEntry(key: String, default: Int?): IntEntry {
        return IntEntry(key, this, default)
    }
    
    override fun longEntry(key: String, default: Long?): LongEntry {
        return LongEntry(key, this, default)
    }
    
    override fun booleanEntry(key: String, default: Boolean?): BooleanEntry {
        return BooleanEntry(key, this, default)
    }
    
    override fun floatEntry(key: String, default: Float?): FloatEntry {
        return FloatEntry(key, this, default)
    }

    override fun doubleEntry(key: String, default: Double?): DoubleEntry {
        return DoubleEntry(key, this, default)
    }

    override fun <T> objectEntry(key: String, serializer: KSerializer<T>, default: T?): ObjectEntry<T> {
        return ObjectEntry(key, this, serializer, default)
    }
    
    // ==================== Flow Observers ====================
    
    override fun observeString(key: String): Flow<String?> {
        return observableCache.getOrPut(key) { MutableStateFlow(null) }
    }
    
    override fun observeInt(key: String): Flow<Int?> {
        return observeString(key).map { it?.toIntOrNull() }
    }
    
    override fun observeBoolean(key: String): Flow<Boolean?> {
        return observeString(key).map { it?.toBooleanStrictOrNull() }
    }
}
