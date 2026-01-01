# VaultKMP

[![Maven Central](https://img.shields.io/maven-central/v/in.sitharaj/vaultkmp)](https://central.sonatype.com/artifact/in.sitharaj/vaultkmp)
[![Kotlin](https://img.shields.io/badge/kotlin-2.1.0-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A Kotlin Multiplatform secure encrypted key-value storage library.

## Features

- 🔐 **Secure** - AES-256-GCM encryption with platform-native key storage
- 🌍 **Multiplatform** - Android, iOS, JVM Desktop, JavaScript, WASM
- 🎯 **Type-Safe** - Compile-time type checking with dedicated entry classes
- ⚡ **Coroutine-based** - Modern async API with Kotlin Coroutines
- 📡 **Observable** - Flow-based observation of value changes
- 🔧 **Configurable** - Builder pattern for flexible configuration

## Platform Implementation

| Platform | Encryption | Storage |
|----------|------------|---------|
| Android | Android Keystore + AES-256-GCM | DataStore Preferences |
| iOS | Keychain Services | Keychain |
| Desktop | AES-256-GCM (PBKDF2) | Encrypted Files |
| JS/WASM | XOR obfuscation* | localStorage |

*JS/WASM uses simplified encryption due to Web Crypto API limitations

## Installation

Add the dependency to your `build.gradle.kts`:

```kotlin
// For Kotlin Multiplatform projects
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("in.sitharaj:vaultkmp:1.0.0")
        }
    }
}

// For Android/JVM only projects
dependencies {
    implementation("in.sitharaj:vaultkmp:1.0.0")
}
```

## Quick Start

### Basic Usage

```kotlin
import `in`.sitharaj.vaultkmp.VaultStore
import `in`.sitharaj.vaultkmp.VaultConfig

// Create a vault instance
val vault = VaultStore.create {
    name("my_secrets")
    encryptionLevel(EncryptionLevel.HIGH)
}

// Store values
vault.putString("auth_token", "abc123")
vault.putInt("user_id", 42)
vault.putBoolean("is_premium", true)

// Retrieve values
val token = vault.getString("auth_token")
val userId = vault.getInt("user_id", default = 0)
```

### Type-Safe Entries

```kotlin
// Define type-safe entries
val tokenEntry = vault.stringEntry("auth_token")
val userIdEntry = vault.intEntry("user_id", default = 0)
val isPremiumEntry = vault.booleanEntry("is_premium", default = false)

// Use entries
tokenEntry.set("new_token")
val token = tokenEntry.get()
val hasToken = tokenEntry.isSet()
tokenEntry.remove()
```

### Storing Objects

```kotlin
@Serializable
data class UserSettings(
    val theme: String,
    val notifications: Boolean
)

val settingsEntry = vault.objectEntry(
    key = "settings",
    serializer = UserSettings.serializer(),
    default = UserSettings("light", true)
)

settingsEntry.set(UserSettings("dark", false))
val settings = settingsEntry.getOrDefault()
```

### Observing Changes

```kotlin
vault.observeString("auth_token").collect { token ->
    println("Token changed: $token")
}
```

## Platform Setup

### Android

Initialize in your `Application` class:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize VaultKMP with context
        PlatformVault.initialize(this)
    }
}
```

### iOS

No special setup required. Keychain access is automatic.

### Desktop

No special setup required. Files stored in `~/.vaultkmp/`.

## API Reference

### VaultStore

| Method | Description |
|--------|-------------|
| `putString/getString` | Store/retrieve string values |
| `putInt/getInt` | Store/retrieve integer values |
| `putLong/getLong` | Store/retrieve long values |
| `putBoolean/getBoolean` | Store/retrieve boolean values |
| `putFloat/getFloat` | Store/retrieve float values |
| `putDouble/getDouble` | Store/retrieve double values |
| `putBytes/getBytes` | Store/retrieve raw bytes |
| `putObject/getObject` | Store/retrieve serializable objects |
| `contains(key)` | Check if key exists |
| `remove(key)` | Remove a value |
| `clear()` | Remove all values |
| `keys()` | Get all stored keys |

### VaultConfig.Builder

| Method | Default | Description |
|--------|---------|-------------|
| `name(string)` | "default_vault" | Unique vault name |
| `encryptionLevel(level)` | HIGH | NONE, STANDARD, or HIGH |
| `keyAlias(string)` | "vault_master_key" | Key alias for hardware storage |
| `cacheDuration(duration)` | null (forever) | In-memory cache duration |

## License

```
Copyright 2024 Sitharaj Seenivasan

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
