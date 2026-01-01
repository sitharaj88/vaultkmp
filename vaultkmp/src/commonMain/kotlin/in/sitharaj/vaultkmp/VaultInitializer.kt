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
 * Thread-safe initialization helper for Android.
 *
 * Call this in your Application.onCreate() before using VaultStore:
 *
 * ```kotlin
 * class MyApplication : Application() {
 *     override fun onCreate() {
 *         super.onCreate()
 *         VaultInitializer.initialize(this)
 *     }
 * }
 * ```
 */
public expect object VaultInitializer {
    /**
     * Initialize VaultKMP with platform-specific context.
     * 
     * @param context Platform-specific context (Android Context, or Unit for other platforms)
     */
    public fun initialize(context: Any)
    
    /**
     * Check if VaultKMP has been initialized.
     */
    public val isInitialized: Boolean
}
