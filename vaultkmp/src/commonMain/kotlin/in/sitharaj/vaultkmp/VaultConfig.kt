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

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

/**
 * Configuration for VaultStore.
 *
 * @property name Unique name for this vault instance
 * @property encryptionLevel Level of encryption to use
 * @property keyAlias Alias for the encryption key (used in hardware-backed storage)
 * @property cacheDuration How long to cache values in memory (null = forever)
 */
public data class VaultConfig(
    val name: String,
    val encryptionLevel: EncryptionLevel = EncryptionLevel.HIGH,
    val keyAlias: String = "vault_master_key",
    val cacheDuration: Duration? = null,
) {
    public companion object {
        /**
         * Default configuration with high security.
         */
        public val DEFAULT: VaultConfig = VaultConfig(
            name = "default_vault",
            encryptionLevel = EncryptionLevel.HIGH
        )
    }

    /**
     * Builder for creating VaultConfig with a fluent API.
     */
    public class Builder {
        private var name: String = "default_vault"
        private var encryptionLevel: EncryptionLevel = EncryptionLevel.HIGH
        private var keyAlias: String = "vault_master_key"
        private var cacheDuration: Duration? = null

        /**
         * Set the vault name (must be unique per vault instance).
         */
        public fun name(name: String): Builder = apply { this.name = name }

        /**
         * Set the encryption level.
         */
        public fun encryptionLevel(level: EncryptionLevel): Builder = apply { this.encryptionLevel = level }

        /**
         * Set the key alias for hardware-backed storage.
         */
        public fun keyAlias(alias: String): Builder = apply { this.keyAlias = alias }

        /**
         * Set cache duration for in-memory caching.
         * Pass null to cache forever (until process death).
         */
        public fun cacheDuration(duration: Duration?): Builder = apply { this.cacheDuration = duration }

        /**
         * Build the VaultConfig.
         */
        public fun build(): VaultConfig = VaultConfig(
            name = name,
            encryptionLevel = encryptionLevel,
            keyAlias = keyAlias,
            cacheDuration = cacheDuration
        )
    }
}
