# Contributing to VaultKMP

Thank you for your interest in contributing to VaultKMP! This document provides guidelines and steps for contributing.

## Code of Conduct

This project adheres to a Code of Conduct. By participating, you are expected to uphold this code.

## How to Contribute

### Reporting Bugs

1. Check if the issue already exists in [GitHub Issues](https://github.com/sitharaj88/vaultkmp/issues)
2. If not, create a new issue with:
   - Clear title and description
   - Platform affected (Android, iOS, Desktop, JS)
   - Steps to reproduce
   - Expected vs actual behavior
   - Version of VaultKMP

### Suggesting Features

1. Open an issue with `[Feature Request]` prefix
2. Describe the feature and its use case
3. Explain how it benefits users

### Pull Requests

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Make your changes following the coding standards
4. Add tests for new functionality
5. Run tests: `./gradlew check`
6. Commit with clear messages: `git commit -m "feat: add new feature"`
7. Push to your fork: `git push origin feature/my-feature`
8. Create a Pull Request

## Development Setup

### Prerequisites

- JDK 17+
- Android SDK (for Android development)
- Xcode 15+ (for iOS development on macOS)

### Building

```bash
# Build all platforms
./gradlew build

# Build specific platform
./gradlew :vaultkmp:compileKotlinDesktop
./gradlew :vaultkmp:compileDebugKotlinAndroid
./gradlew :vaultkmp:compileKotlinJs

# Run tests
./gradlew :vaultkmp:desktopTest
```

### Code Style

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use explicit API mode (all public declarations need visibility modifier)
- Add KDoc for public APIs
- Add license header to new files

### Commit Messages

We use [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` new feature
- `fix:` bug fix
- `docs:` documentation changes
- `test:` adding tests
- `refactor:` code refactoring
- `chore:` maintenance tasks

## License

By contributing, you agree that your contributions will be licensed under the Apache License 2.0.
