# VaultKMP Sample App

This is a Compose Multiplatform sample application demonstrating the capabilities of the VaultKMP library.

## Features Demonstrated
- **Secure Storage**: Encrypted storage of Strings, Ints, and other types.
- **Cross-Platform UI**: Shared UI code across Android, iOS, Desktop, and Web.
- **Audit Logging**: Console logging of all vault operations.
- **Key Management**: List, delete, and clear keys.

## Running the App

### 🤖 Android
Run inside Android Studio or via command line:
```bash
./gradlew :sample:composeApp:installDebug
```

### 🖥️ Desktop (macOS/Linux/Windows)
Run via Gradle:
```bash
./gradlew :sample:composeApp:desktopRun
```

### 🌐 Web (WASM)
Run the development server:
```bash
./gradlew :sample:composeApp:wasmJsBrowserRun
```
Then open `http://localhost:8080/index.html` in a browser with WASM GC support (Chrome/Firefox).

### 🍎 iOS
iOS requires Xcode to build and run.

1. Open `sample/iosApp/iosApp.xcodeproj` in Xcode.
2. Select a simulator (e.g., iPhone 15).
3. Press **Run** (Cmd+R).

**Note**: The Kotlin code is compiled into a framework that Xcode links against. If you make changes to Kotlin code, you may need to rebuild via Gradle or clean/build in Xcode.
