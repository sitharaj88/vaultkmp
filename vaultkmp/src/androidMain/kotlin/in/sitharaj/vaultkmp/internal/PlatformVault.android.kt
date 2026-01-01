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
import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Factory function to create Android VaultStore.
 */
internal actual fun createPlatformVault(config: VaultConfig): VaultStore {
    return AndroidVault(config)
}

/**
 * Android implementation of VaultStore using DataStore with encryption.
 */
internal class AndroidVault(
    override val config: VaultConfig
) : VaultStore {
    
    private val encryptor = Encryptor(config)
    private val json = Json { ignoreUnknownKeys = true }
    
    // DataStore instance - must be initialized with context
    private lateinit var dataStore: androidx.datastore.core.DataStore<Preferences>
    
    companion object {
        private var appContext: Context? = null
        
        /**
         * Initialize with application context. Must be called before using VaultStore on Android.
         */
        fun initialize(context: Context) {
            appContext = context.applicationContext
        }
        
        private val Context.vaultDataStore by preferencesDataStore(name = "vault_store")
    }
    
    init {
        val context = appContext ?: throw IllegalStateException(
            "VaultStore not initialized. Call AndroidVault.initialize(context) in Application.onCreate()"
        )
        dataStore = context.vaultDataStore
    }
    
    @OptIn(ExperimentalEncodingApi::class)
    private fun encryptAndEncode(value: String): String {
        val encrypted = encryptor.encrypt(value.encodeToByteArray())
        return Base64.encode(encrypted)
    }
    
    @OptIn(ExperimentalEncodingApi::class)
    private fun decodeAndDecrypt(value: String): String {
        val encrypted = Base64.decode(value)
        return encryptor.decrypt(encrypted).decodeToString()
    }
    
    private fun stringKey(key: String) = stringPreferencesKey("vault_$key")
    
    // ==================== String Operations ====================
    
    override suspend fun putString(key: String, value: String) {
        dataStore.edit { prefs ->
            prefs[stringKey(key)] = encryptAndEncode(value)
        }
    }
    
    override suspend fun getString(key: String): String? {
        val prefs = dataStore.data.first()
        val encrypted = prefs[stringKey(key)] ?: return null
        return decodeAndDecrypt(encrypted)
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
    
    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun putBytes(key: String, value: ByteArray) {
        val encrypted = encryptor.encrypt(value)
        dataStore.edit { prefs ->
            prefs[stringKey(key)] = Base64.encode(encrypted)
        }
    }
    
    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun getBytes(key: String): ByteArray? {
        val prefs = dataStore.data.first()
        val encoded = prefs[stringKey(key)] ?: return null
        return encryptor.decrypt(Base64.decode(encoded))
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
        val prefs = dataStore.data.first()
        return prefs.contains(stringKey(key))
    }
    
    override suspend fun remove(key: String) {
        dataStore.edit { prefs ->
            prefs.remove(stringKey(key))
        }
    }
    
    override suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
    
    override suspend fun keys(): Set<String> {
        val prefs = dataStore.data.first()
        return prefs.asMap().keys
            .filterIsInstance<Preferences.Key<String>>()
            .map { it.name.removePrefix("vault_") }
            .toSet()
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
        return dataStore.data.map { prefs ->
            prefs[stringKey(key)]?.let { decodeAndDecrypt(it) }
        }
    }
    
    override fun observeInt(key: String): Flow<Int?> {
        return observeString(key).map { it?.toIntOrNull() }
    }
    
    override fun observeBoolean(key: String): Flow<Boolean?> {
        return observeString(key).map { it?.toBooleanStrictOrNull() }
    }
}
