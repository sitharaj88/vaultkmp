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
 * Migration for upgrading vault data between schema versions.
 *
 * @property fromVersion The schema version to migrate from
 * @property toVersion The schema version to migrate to
 * @property migrate The migration function that transforms data
 */
public data class VaultMigration(
    val fromVersion: Int,
    val toVersion: Int,
    val migrate: suspend (VaultStore) -> Unit
)

/**
 * Builder for creating migrations.
 */
public class VaultMigrationBuilder {
    private val migrations = mutableListOf<VaultMigration>()
    
    /**
     * Add a migration from one version to another.
     */
    public fun addMigration(
        fromVersion: Int,
        toVersion: Int,
        migrate: suspend (VaultStore) -> Unit
    ): VaultMigrationBuilder = apply {
        migrations.add(VaultMigration(fromVersion, toVersion, migrate))
    }
    
    /**
     * Build the list of migrations.
     */
    public fun build(): List<VaultMigration> = migrations.toList()
}

/**
 * Key rotation helper for rotating encryption keys.
 */
public object KeyRotation {
    /**
     * Rotate encryption key by re-encrypting all data.
     *
     * @param oldVault The vault with the old key
     * @param newVault The vault with the new key
     * @param keys List of keys to migrate
     */
    public suspend fun rotateKey(
        oldVault: VaultStore,
        newVault: VaultStore,
        keys: Set<String>
    ) {
        for (key in keys) {
            val value = oldVault.getString(key)
            if (value != null) {
                newVault.putString(key, value)
                oldVault.remove(key)
            }
        }
    }
}
