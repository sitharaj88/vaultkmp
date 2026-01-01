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

class VaultConfigTest {
    
    @Test
    fun `default config should have HIGH encryption`() {
        val config = VaultConfig.DEFAULT
        
        config.encryptionLevel shouldBe EncryptionLevel.HIGH
        config.name shouldBe "default_vault"
    }
    
    @Test
    fun `builder should create config with custom values`() {
        val config = VaultConfig.Builder()
            .name("test_vault")
            .encryptionLevel(EncryptionLevel.STANDARD)
            .keyAlias("my_key")
            .build()
        
        config.name shouldBe "test_vault"
        config.encryptionLevel shouldBe EncryptionLevel.STANDARD
        config.keyAlias shouldBe "my_key"
    }
    
    @Test
    fun `EncryptionLevel should have three levels`() {
        EncryptionLevel.entries.size shouldBe 3
        EncryptionLevel.NONE shouldNotBe EncryptionLevel.STANDARD
        EncryptionLevel.STANDARD shouldNotBe EncryptionLevel.HIGH
    }
}
