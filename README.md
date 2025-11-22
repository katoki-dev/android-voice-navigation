# Android Voice Navigation

A voice-controlled Android tap-navigation system using an AccessibilityService to perform tap, swipe, circle, home, back, and recents actions. A background SpeechRecognizer service listens continuously, sends spoken text to a CommandParser, converts grid-based commands to coordinates via a mapper, and triggers gesture execution. Includes a simple UI toggle, offline-ready design, modular architecture, and secure local processing.

## Features

- **Voice-Controlled Navigation**: Control your Android device using voice commands
- **Grid-Based Targeting**: Uses a 10x10 grid system (A-J, 1-10) for precise screen targeting
- **Multiple Gesture Types**: 
  - Tap at specific positions
  - Swipe between positions
  - Draw circles
  - Home, Back, and Recents actions
- **Continuous Listening**: Background service that listens for commands continuously
- **Offline-Ready**: All processing happens locally on the device
- **Privacy-Focused**: No data sent to external servers
- **Modular Architecture**: Clean separation of concerns with distinct components

## Architecture

The app is built with a modular architecture consisting of the following components:

### Core Components

1. **VoiceNavigationAccessibilityService**: 
   - Extends Android's `AccessibilityService`
   - Executes gestures on the screen
   - Manages the lifecycle of the voice recognition service

2. **VoiceRecognitionService**: 
   - Background service using Android's `SpeechRecognizer`
   - Continuously listens for voice commands
   - Handles speech recognition events and errors

3. **CommandParser**: 
   - Interprets spoken text into actionable commands
   - Supports various command patterns
   - Returns structured command objects

4. **GridMapper**: 
   - Converts grid positions (e.g., "A5") to screen coordinates
   - Handles screen dimensions dynamically
   - Validates grid positions

5. **MainActivity**: 
   - Simple UI for controlling the service
   - Manages permissions
   - Provides instructions to users

## Voice Commands

### Tap Commands
- "Tap A5" - Taps at grid position A5
- "Click B3" - Taps at grid position B3

### Swipe Commands
- "Swipe A1 to C5" - Swipes from A1 to C5
- "Swipe from D2 to F8" - Swipes from D2 to F8

### Circle Commands
- "Circle B3" - Draws a circle at position B3
- "Draw circle at E5" - Draws a circle at position E5

### System Commands
- "Home" - Goes to the home screen
- "Back" - Goes back
- "Recents" - Opens recent apps

## Grid System

The screen is divided into a 10x10 grid:
- **Columns**: A, B, C, D, E, F, G, H, I, J (left to right)
- **Rows**: 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 (top to bottom)

For example:
- A1 is the top-left corner
- J10 is the bottom-right corner
- E5 is approximately the center of the screen

## Setup and Installation

### Prerequisites
- Android device running Android 7.0 (API 24) or higher
- Android Studio (for building from source)

### Installation Steps

1. **Clone the repository**:
   ```bash
   git clone https://github.com/katoki-dev/android-voice-navigation.git
   cd android-voice-navigation
   ```

2. **Build the project**:
   ```bash
   ./gradlew assembleDebug
   ```

3. **Install on your device**:
   ```bash
   ./gradlew installDebug
   ```

### First-Time Setup

1. **Grant Microphone Permission**:
   - Open the app
   - Tap "Grant Permission" to allow microphone access

2. **Enable Accessibility Service**:
   - Tap "Open Accessibility Settings"
   - Find "Voice Navigation" in the list
   - Toggle it on
   - Accept the permission prompt

3. **Start Using Voice Commands**:
   - The service will start listening automatically
   - Speak any supported voice command
   - The app will execute the corresponding action

## Permissions

The app requires the following permissions:

- **RECORD_AUDIO**: To capture voice commands
- **BIND_ACCESSIBILITY_SERVICE**: To execute gestures on the screen
- **INTERNET**: Required by SpeechRecognizer (though all processing is local)

## Privacy and Security

- **All processing is local**: Voice commands are processed entirely on your device
- **No external servers**: No data is sent to external servers
- **Secure by design**: Minimal permissions required
- **No data collection**: The app doesn't collect or store any user data

## Project Structure

```
android-voice-navigation/
├── app/
│   ├── src/main/
│   │   ├── java/com/katoki/voicenavigation/
│   │   │   ├── mapper/
│   │   │   │   └── GridMapper.kt
│   │   │   ├── parser/
│   │   │   │   └── CommandParser.kt
│   │   │   ├── service/
│   │   │   │   ├── VoiceNavigationAccessibilityService.kt
│   │   │   │   └── VoiceRecognitionService.kt
│   │   │   └── ui/
│   │   │       └── MainActivity.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml
│   │   │   ├── values/
│   │   │   │   ├── colors.xml
│   │   │   │   ├── strings.xml
│   │   │   │   └── themes.xml
│   │   │   └── xml/
│   │   │       └── accessibility_service_config.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── README.md
```

## Technical Details

### Accessibility Service Configuration
- **Event Types**: All events (typeAllMask)
- **Feedback Type**: Generic feedback
- **Capabilities**: Can perform gestures and retrieve window content
- **Notification Timeout**: 100ms for responsive feedback

### Speech Recognition
- **Language Model**: Free-form (for natural language)
- **Partial Results**: Enabled for faster response
- **Continuous Mode**: Automatically restarts after each recognition
- **Error Handling**: Robust error recovery with automatic retry

### Gesture Execution
- **Tap Duration**: 100ms
- **Swipe Duration**: 500ms
- **Circle Duration**: 1000ms
- **Circle Radius**: 100 pixels

## Troubleshooting

### Voice commands not recognized
- Ensure microphone permission is granted
- Check that the accessibility service is enabled
- Speak clearly and at a moderate pace
- Try rephrasing the command

### Gestures not executing
- Verify the accessibility service is enabled in system settings
- Check that the grid position is valid (A-J, 1-10)
- Ensure no other apps are blocking touch events

### Service stops listening
- The service automatically restarts on errors
- If it stops completely, restart the accessibility service
- Check system battery optimization settings

## Development

### Building from Source
```bash
./gradlew build
```

### Running Tests
```bash
./gradlew test
```

### Code Style
- Kotlin coding conventions
- Clear separation of concerns
- Comprehensive inline documentation

## Contributing

Contributions are welcome! Please follow these guidelines:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Built with Android's AccessibilityService API
- Uses Android's SpeechRecognizer for voice input
- Designed for accessibility and ease of use

## Support

For issues, questions, or suggestions, please open an issue on the GitHub repository.

