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
import `in`.sitharaj.vaultkmp.VaultStore
import `in`.sitharaj.vaultkmp.entry.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*

/**
 * Factory function to create Desktop VaultStore.
 */
internal actual fun createPlatformVault(config: VaultConfig): VaultStore {
    return DesktopVault(config)
}

/**
 * Desktop/JVM implementation of VaultStore using encrypted file storage.
 */
internal class DesktopVault(
    override val config: VaultConfig
) : VaultStore {
    
    private val encryptor = Encryptor(config)
    private val json = Json { ignoreUnknownKeys = true }
    private val observableCache = mutableMapOf<String, MutableStateFlow<String?>>()
    
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
    
    // ==================== String Operations ====================
    
    override suspend fun putString(key: String, value: String) {
        encryptAndSave(key, value)
    }
    
    override suspend fun getString(key: String): String? {
        return loadAndDecrypt(key)
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
    
    override suspend fun putBytes(key: String, value: ByteArray) {
        val encrypted = encryptor.encrypt(value)
        keyToFile(key).writeBytes(encrypted)
    }
    
    override suspend fun getBytes(key: String): ByteArray? {
        val file = keyToFile(key)
        if (!file.exists()) return null
        return encryptor.decrypt(file.readBytes())
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
    
    override suspend fun contains(key: String): Boolean {
        return keyToFile(key).exists()
    }
    
    override suspend fun remove(key: String) {
        keyToFile(key).delete()
        notifyObservers(key, null)
    }
    
    override suspend fun clear() {
        storageDir.listFiles()?.forEach { it.delete() }
        observableCache.values.forEach { it.value = null }
    }
    
    override suspend fun keys(): Set<String> {
        return storageDir.listFiles()
            ?.filter { it.extension == "vault" }
            ?.mapNotNull { file ->
                try {
                    String(Base64.getDecoder().decode(file.nameWithoutExtension))
                } catch (e: Exception) {
                    null
                }
            }
            ?.toSet() ?: emptySet()
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
