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

class VaultAuditLoggerTest {
    
    @Test
    fun `ConsoleAuditLogger should not throw`() {
        // Should not throw
        ConsoleAuditLogger.log(VaultOperation.PUT, "test_key", true)
        ConsoleAuditLogger.log(VaultOperation.GET, "test_key", false, "error")
        ConsoleAuditLogger.log(VaultOperation.CLEAR, null, true)
    }
    
    @Test
    fun `NoOpAuditLogger should not throw`() {
        // Should not throw - just no-op
        NoOpAuditLogger.log(VaultOperation.PUT, "key", true)
        NoOpAuditLogger.log(VaultOperation.GET, null, false, "error")
    }
    
    @Test
    fun `VaultOperation should have correct values`() {
        VaultOperation.entries.size shouldBe 6
        VaultOperation.PUT shouldNotBe VaultOperation.GET
        VaultOperation.REMOVE shouldNotBe VaultOperation.CLEAR
    }
    
    @Test
    fun `custom logger can track operations`() {
        val operations = mutableListOf<String>()
        val testLogger = object : VaultAuditLogger {
            override fun log(operation: VaultOperation, key: String?, success: Boolean, error: String?) {
                operations.add("$operation:$key:$success")
            }
        }
        
        testLogger.log(VaultOperation.PUT, "key1", true)
        testLogger.log(VaultOperation.GET, "key2", false, "error")
        
        operations.size shouldBe 2
        operations[0] shouldBe "PUT:key1:true"
        operations[1] shouldBe "GET:key2:false"
    }
}
