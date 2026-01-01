# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0.0] - 2024-01-02

### Added

- Initial release of VaultKMP
- **Core Features**
  - Type-safe encrypted key-value storage
  - Support for String, Int, Long, Boolean, Float, Double, ByteArray, and serializable objects
  - Three encryption levels: NONE, STANDARD (AES-128), HIGH (AES-256)
  - Flow-based observation for reactive value changes
  - Builder pattern for configuration

- **Platform Support**
  - Android: DataStore + Android Keystore encryption
  - iOS: NSUserDefaults with AES encryption
  - Desktop/JVM: Encrypted file storage (~/.vaultkmp/)
  - JavaScript/WASM: localStorage with obfuscation

- **API**
  - `VaultStore` main interface
  - `VaultConfig` configuration with Builder
  - `VaultInitializer` for Android context injection
  - Type-safe entry classes: `StringEntry`, `IntEntry`, etc.
  - `VaultException` for proper error handling

- **Documentation**
  - Comprehensive README with usage examples
  - Apache 2.0 License
  - Contributing guidelines
  - API documentation via Dokka

### Security

- AES-256-GCM encryption for HIGH security level
- AES-128-GCM encryption for STANDARD security level
- Platform-native key storage (Android Keystore)
- PBKDF2 key derivation on Desktop
