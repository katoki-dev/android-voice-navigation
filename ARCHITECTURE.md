# Architecture Documentation

## Overview

The Android Voice Navigation system is built with a clean, modular architecture that separates concerns into distinct components. This design makes the codebase maintainable, testable, and extensible.

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                         User Interface                       │
│                        (MainActivity)                        │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        │ Controls
                        ▼
┌─────────────────────────────────────────────────────────────┐
│              VoiceNavigationAccessibilityService            │
│                    (Main Orchestrator)                      │
└────────┬────────────────────────┬───────────────────────────┘
         │                        │
         │ Starts/Stops           │ Executes Gestures
         ▼                        │
┌────────────────────┐            │
│ VoiceRecognition   │            │
│     Service        │            │
└─────────┬──────────┘            │
          │                       │
          │ Spoken Text           │
          ▼                       │
┌────────────────────┐            │
│  CommandParser     │            │
│  (Text → Command)  │            │
└─────────┬──────────┘            │
          │                       │
          │ Parsed Command        │
          └───────────────────────┤
                                  │
                         Grid Position
                                  │
                                  ▼
                        ┌────────────────────┐
                        │    GridMapper      │
                        │ (Position → Point) │
                        └────────────────────┘
```

## Component Details

### 1. MainActivity (UI Layer)

**Responsibilities:**
- Display service status
- Manage permissions
- Provide user instructions
- Open accessibility settings

**Key Features:**
- Real-time status updates
- Permission request handling
- User-friendly instructions

**Dependencies:**
- VoiceNavigationAccessibilityService (status checks)
- VoiceRecognitionService (listening status)

### 2. VoiceNavigationAccessibilityService (Core Service)

**Responsibilities:**
- Manage accessibility service lifecycle
- Start/stop voice recognition
- Receive parsed commands
- Execute gestures using Android's gesture APIs

**Key Features:**
- Singleton pattern for global access
- Screen dimension detection
- Gesture execution (tap, swipe, circle)
- Global actions (home, back, recents)

**Dependencies:**
- GridMapper (coordinate conversion)
- CommandParser (command interpretation)
- VoiceRecognitionService (voice input)

### 3. VoiceRecognitionService (Speech Layer)

**Responsibilities:**
- Continuous speech recognition
- Handle recognition events
- Error recovery and retry
- Send recognized text to accessibility service

**Key Features:**
- Automatic restart after errors
- Partial results support
- Low latency recognition
- Robust error handling

**Dependencies:**
- Android SpeechRecognizer API
- VoiceNavigationAccessibilityService (command delivery)

### 4. CommandParser (Logic Layer)

**Responsibilities:**
- Parse spoken text into structured commands
- Validate command syntax
- Extract grid positions
- Return typed command objects

**Key Features:**
- Support for multiple command patterns
- Case-insensitive parsing
- Grid position validation
- Sealed class for type safety

**Command Types:**
```kotlin
sealed class Command {
    data class Tap(val position: String) : Command()
    data class Swipe(val startPosition: String, val endPosition: String) : Command()
    data class Circle(val centerPosition: String) : Command()
    object Home : Command()
    object Back : Command()
    object Recents : Command()
    object Unknown : Command()
}
```

### 5. GridMapper (Utility Layer)

**Responsibilities:**
- Convert grid positions to screen coordinates
- Validate grid positions
- Calculate cell centers

**Key Features:**
- Dynamic screen dimension handling
- 10x10 grid system
- Center-point calculation
- Position validation

**Grid System:**
- Columns: A-J (10 columns)
- Rows: 1-10 (10 rows)
- Each cell represents a portion of the screen

## Data Flow

### Voice Command Execution Flow

1. **User speaks a command** (e.g., "Tap A5")
2. **VoiceRecognitionService** captures the audio
3. **SpeechRecognizer** converts audio to text
4. **VoiceRecognitionService** forwards text to **VoiceNavigationAccessibilityService**
5. **VoiceNavigationAccessibilityService** calls **CommandParser.parse()**
6. **CommandParser** returns a `Command.Tap("A5")` object
7. **VoiceNavigationAccessibilityService** calls **GridMapper.gridToCoordinates("A5")**
8. **GridMapper** returns `Point(x, y)` coordinates
9. **VoiceNavigationAccessibilityService** creates a gesture path
10. **AccessibilityService** dispatches the gesture
11. **System executes the tap** at the specified coordinates

### Service Lifecycle

```
App Launch
    │
    ▼
MainActivity Opens
    │
    ▼
User Enables Accessibility Service
    │
    ▼
VoiceNavigationAccessibilityService.onServiceConnected()
    │
    ├─► Initialize GridMapper with screen dimensions
    │
    └─► Start VoiceRecognitionService
            │
            ▼
        Initialize SpeechRecognizer
            │
            ▼
        Start Listening (Continuous Loop)
            │
            ▼
        On Speech Recognized
            │
            └─► Process Command
```

## Design Patterns

### 1. Singleton Pattern
- **VoiceNavigationAccessibilityService** maintains a static instance
- Allows other components to access the service globally

### 2. Sealed Classes
- **CommandParser.Command** uses sealed classes for type safety
- Enables exhaustive when expressions

### 3. Service Pattern
- Both accessibility and voice recognition are Android Services
- Allows background operation and lifecycle management

### 4. Strategy Pattern
- Different command types handled by different execution strategies
- Each gesture type has its own execution method

## Thread Safety

### Main Thread Operations
- All gesture execution happens on the main thread
- UI updates in MainActivity are on the main thread
- Service callbacks are on the main thread

### Background Operations
- Speech recognition runs on a background thread
- Command parsing is thread-safe (no shared mutable state)

## Error Handling

### VoiceRecognitionService
- **Audio errors**: Restart recognition
- **Network errors**: Restart recognition (local processing still works)
- **Permission errors**: Don't restart, notify user
- **Timeout errors**: Restart recognition

### VoiceNavigationAccessibilityService
- **Invalid grid positions**: Log warning, don't execute
- **Null coordinates**: Log warning, don't execute
- **Unknown commands**: Log warning, ignore

### CommandParser
- **Invalid syntax**: Return `Command.Unknown`
- **Invalid positions**: Return `Command.Unknown`
- **Empty input**: Return `Command.Unknown`

## Extension Points

The architecture is designed to be extensible:

### Adding New Commands
1. Add a new command type to `CommandParser.Command` sealed class
2. Add parsing logic in `CommandParser.parse()`
3. Add execution logic in `VoiceNavigationAccessibilityService`

### Adding New Gesture Types
1. Create a new command type in `CommandParser`
2. Implement gesture creation in `VoiceNavigationAccessibilityService`
3. Use Android's `GestureDescription` API to define the gesture

### Customizing Grid System
1. Modify `GridMapper` constants (GRID_COLUMNS, GRID_ROWS)
2. Update parsing logic in `CommandParser` to match new grid format
3. Update user instructions in strings.xml

## Performance Considerations

### Memory
- Services are lightweight with minimal state
- No heavy object allocations during command processing
- Efficient string parsing without regex where possible

### CPU
- Grid calculations are simple arithmetic operations
- Command parsing uses basic string operations
- No heavy computation in the critical path

### Battery
- Continuous speech recognition can impact battery
- Service can be disabled when not needed
- No unnecessary background processing

## Security Considerations

### Privacy
- All processing happens locally on device
- No network communication for voice processing
- No data storage or logging of commands

### Permissions
- Minimal permissions required
- RECORD_AUDIO for voice input
- BIND_ACCESSIBILITY_SERVICE for gesture execution
- INTERNET required by SpeechRecognizer but not used for external communication

### Accessibility Service Security
- Android restricts accessibility services
- User must explicitly enable the service
- Service can be disabled at any time

## Testing Strategy

### Unit Testing
- **CommandParser**: Test all command patterns
- **GridMapper**: Test coordinate calculations
- Mock dependencies for isolated testing

### Integration Testing
- Test service communication
- Test command flow end-to-end
- Test error handling

### Manual Testing
- Test on different screen sizes
- Test with different Android versions
- Test voice recognition accuracy
- Test gesture execution accuracy

## Future Enhancements

Potential areas for improvement:

1. **Customizable Grid Size**: Allow users to configure grid dimensions
2. **Visual Feedback**: Show grid overlay on screen
3. **Command History**: Log executed commands for debugging
4. **Custom Commands**: Allow users to define custom voice commands
5. **Gesture Recording**: Record and replay gesture sequences
6. **Multi-language Support**: Support for different languages
7. **Voice Feedback**: Audio confirmation of executed commands
8. **Gesture Templates**: Pre-defined gestures for common actions
