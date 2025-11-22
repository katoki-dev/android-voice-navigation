# Contributing to Android Voice Navigation

Thank you for your interest in contributing to Android Voice Navigation! This document provides guidelines and instructions for contributing to the project.

## Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- Focus on the issue, not the person
- Help others learn and grow

## How to Contribute

### Reporting Bugs

If you find a bug, please create an issue with the following information:

1. **Description**: Clear description of the bug
2. **Steps to Reproduce**: Detailed steps to reproduce the issue
3. **Expected Behavior**: What you expected to happen
4. **Actual Behavior**: What actually happened
5. **Environment**: Android version, device model, app version
6. **Logs**: Relevant logcat output if available

### Suggesting Enhancements

We welcome enhancement suggestions! Please create an issue with:

1. **Use Case**: Describe the problem you're trying to solve
2. **Proposed Solution**: Your idea for solving it
3. **Alternatives**: Other solutions you've considered
4. **Benefits**: How this would help users

### Pull Requests

#### Before You Start

1. Check existing issues and PRs to avoid duplicates
2. For major changes, create an issue first to discuss
3. Make sure you can build and run the project locally

#### Development Process

1. **Fork the Repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/android-voice-navigation.git
   ```

2. **Create a Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```
   
   Branch naming conventions:
   - `feature/` for new features
   - `fix/` for bug fixes
   - `docs/` for documentation
   - `refactor/` for code refactoring

3. **Make Your Changes**
   - Follow the coding style (see below)
   - Write clear commit messages
   - Add tests if applicable
   - Update documentation as needed

4. **Test Your Changes**
   - Build the project: `./gradlew build`
   - Run tests: `./gradlew test`
   - Test manually on a device
   - Ensure no new warnings

5. **Commit Your Changes**
   ```bash
   git add .
   git commit -m "Brief description of changes"
   ```
   
   Commit message guidelines:
   - Use present tense ("Add feature" not "Added feature")
   - Be concise but descriptive
   - Reference issues when applicable

6. **Push to Your Fork**
   ```bash
   git push origin feature/your-feature-name
   ```

7. **Create a Pull Request**
   - Provide a clear title and description
   - Reference related issues
   - Describe what you changed and why
   - Include screenshots for UI changes

#### Pull Request Checklist

- [ ] Code follows the project's coding style
- [ ] Changes are well-documented
- [ ] All tests pass
- [ ] No new warnings introduced
- [ ] Documentation updated if needed
- [ ] Commit messages are clear and descriptive

## Coding Style

### Kotlin Style Guidelines

Follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):

- Use 4 spaces for indentation
- Maximum line length: 120 characters
- Use meaningful variable names
- Add KDoc comments for public APIs

### Code Organization

- One class per file
- Group related classes in packages
- Keep classes focused and small
- Use dependency injection where appropriate

### Naming Conventions

- **Classes**: PascalCase (e.g., `VoiceRecognitionService`)
- **Functions**: camelCase (e.g., `processVoiceCommand`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_RETRIES`)
- **Variables**: camelCase (e.g., `gridPosition`)

### Documentation

- Add KDoc comments for public classes and functions
- Explain "why" not "what" in comments
- Keep comments up-to-date with code changes
- Use clear and concise language

Example:
```kotlin
/**
 * Converts a grid position (e.g., "A5") to screen coordinates.
 * 
 * @param gridPosition Grid position in format [A-J][1-10]
 * @return Point with x,y coordinates, or null if invalid
 */
fun gridToCoordinates(gridPosition: String): Point?
```

### Testing

- Write unit tests for new functionality
- Keep tests focused and isolated
- Use descriptive test names
- Follow the AAA pattern (Arrange, Act, Assert)

Example:
```kotlin
@Test
fun `parse should return Tap command for valid tap input`() {
    // Arrange
    val parser = CommandParser()
    
    // Act
    val result = parser.parse("tap A5")
    
    // Assert
    assertTrue(result is CommandParser.Command.Tap)
    assertEquals("A5", (result as CommandParser.Command.Tap).position)
}
```

## Project Structure

```
app/src/main/java/com/katoki/voicenavigation/
├── mapper/        # Coordinate mapping
├── parser/        # Command parsing
├── service/       # Background services
└── ui/            # User interface
```

When adding new features:
- Put classes in the appropriate package
- Create new packages if needed
- Update this document if structure changes

## Development Environment

### Prerequisites

- Android Studio (latest stable version)
- JDK 8 or higher
- Android SDK (API 24+)
- Git

### Setup

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on a device or emulator

### Building

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test

# Run all checks
./gradlew check
```

## Feature Requests

We love feature ideas! When suggesting a feature:

1. Explain the use case clearly
2. Describe the expected behavior
3. Consider edge cases
4. Think about backward compatibility
5. Be open to discussion and alternatives

## Questions?

If you have questions:

1. Check existing issues and discussions
2. Read the documentation (README.md, ARCHITECTURE.md)
3. Create an issue with your question
4. Be patient and respectful

## Recognition

Contributors will be recognized in:
- The project's README
- Release notes
- GitHub's contributors list

Thank you for contributing to Android Voice Navigation!
