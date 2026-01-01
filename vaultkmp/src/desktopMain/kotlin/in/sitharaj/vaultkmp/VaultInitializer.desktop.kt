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
 * Desktop/JVM implementation of VaultInitializer.
 * No initialization required on Desktop.
 */
public actual object VaultInitializer {
    private var _initialized = false
    
    /**
     * Initialize VaultKMP. No-op on Desktop.
     *
     * @param context Ignored on Desktop
     */
    public actual fun initialize(context: Any) {
        _initialized = true
    }
    
    /**
     * Check if VaultKMP has been initialized.
     * Always returns true on Desktop as no initialization is required.
     */
    public actual val isInitialized: Boolean
        get() = true
}
