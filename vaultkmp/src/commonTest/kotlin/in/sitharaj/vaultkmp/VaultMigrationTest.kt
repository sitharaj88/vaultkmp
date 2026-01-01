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

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class VaultMigrationTest {
    
    @Test
    fun `VaultMigration should store version info`() {
        val migration = VaultMigration(
            fromVersion = 1,
            toVersion = 2,
            migrate = { /* no-op */ }
        )
        
        migration.fromVersion shouldBe 1
        migration.toVersion shouldBe 2
    }
    
    @Test
    fun `VaultMigrationBuilder should build migration list`() {
        val migrations = VaultMigrationBuilder()
            .addMigration(1, 2) { /* migration 1->2 */ }
            .addMigration(2, 3) { /* migration 2->3 */ }
            .build()
        
        migrations.size shouldBe 2
        migrations[0].fromVersion shouldBe 1
        migrations[0].toVersion shouldBe 2
        migrations[1].fromVersion shouldBe 2
        migrations[1].toVersion shouldBe 3
    }
    
    @Test
    fun `VaultConfig should have schema version`() {
        val config = VaultConfig.Builder()
            .name("test")
            .schemaVersion(5)
            .build()
        
        config.schemaVersion shouldBe 5
    }
    
    @Test
    fun `default config should have schema version 1`() {
        VaultConfig.DEFAULT.schemaVersion shouldBe 1
    }
}
