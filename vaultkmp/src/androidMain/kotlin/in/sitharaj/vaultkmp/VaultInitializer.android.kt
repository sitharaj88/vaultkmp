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

import android.content.Context
import `in`.sitharaj.vaultkmp.internal.AndroidVault

/**
 * Android implementation of VaultInitializer.
 */
public actual object VaultInitializer {
    private var _context: Context? = null
    
    internal val context: Context
        get() = _context ?: throw VaultException.initializationFailed(
            "VaultKMP not initialized. Call VaultInitializer.initialize(context) in Application.onCreate()"
        )
    
    /**
     * Initialize VaultKMP with Android Context.
     *
     * @param context Android Context (Application context recommended)
     */
    public actual fun initialize(context: Any) {
        require(context is Context) { "Expected Android Context" }
        _context = context.applicationContext
    }
    
    /**
     * Check if VaultKMP has been initialized.
     */
    public actual val isInitialized: Boolean
        get() = _context != null
}
