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
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test

class VaultExceptionTest {
    
    @Test
    fun `encryptionFailed should create proper exception`() {
        val exception = VaultException.encryptionFailed("test_key")
        
        exception.key shouldBe "test_key"
        exception.operation shouldBe "encrypt"
        exception.message shouldContain "encrypt"
        exception.message shouldContain "test_key"
    }
    
    @Test
    fun `decryptionFailed should create proper exception`() {
        val exception = VaultException.decryptionFailed("secret_key")
        
        exception.key shouldBe "secret_key"
        exception.operation shouldBe "decrypt"
        exception.message shouldContain "decrypt"
        exception.message shouldContain "corrupted"
    }
    
    @Test
    fun `storageFailed should include operation`() {
        val exception = VaultException.storageFailed("key", "write")
        
        exception.key shouldBe "key"
        exception.operation shouldBe "write"
        exception.message shouldContain "write"
    }
    
    @Test
    fun `initializationFailed should have null key`() {
        val exception = VaultException.initializationFailed("Not initialized")
        
        exception.key shouldBe null
        exception.operation shouldBe "initialize"
    }
}
