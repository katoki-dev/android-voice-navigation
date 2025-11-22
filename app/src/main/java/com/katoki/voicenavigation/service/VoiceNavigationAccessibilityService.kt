package com.katoki.voicenavigation.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.util.Log
import com.katoki.voicenavigation.mapper.GridMapper
import com.katoki.voicenavigation.parser.CommandParser

/**
 * AccessibilityService that executes gestures based on parsed voice commands.
 */
class VoiceNavigationAccessibilityService : AccessibilityService() {
    
    companion object {
        private const val TAG = "VoiceNavAccessibility"
        private var instance: VoiceNavigationAccessibilityService? = null
        
        fun getInstance(): VoiceNavigationAccessibilityService? = instance
        
        fun isServiceEnabled(): Boolean = instance != null
    }
    
    private lateinit var gridMapper: GridMapper
    private val commandParser = CommandParser()
    private var voiceRecognitionService: VoiceRecognitionService? = null
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d(TAG, "Accessibility Service Connected")
        
        // Initialize grid mapper with screen dimensions
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        
        gridMapper = GridMapper(displayMetrics.widthPixels, displayMetrics.heightPixels)
        
        // Start voice recognition service
        startVoiceRecognitionService()
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // We don't need to handle accessibility events for this use case
    }
    
    override fun onInterrupt() {
        Log.d(TAG, "Accessibility Service Interrupted")
    }
    
    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "Accessibility Service Disconnected")
        stopVoiceRecognitionService()
        instance = null
        return super.onUnbind(intent)
    }
    
    /**
     * Starts the voice recognition service.
     */
    private fun startVoiceRecognitionService() {
        val intent = Intent(this, VoiceRecognitionService::class.java)
        startService(intent)
    }
    
    /**
     * Stops the voice recognition service.
     */
    private fun stopVoiceRecognitionService() {
        val intent = Intent(this, VoiceRecognitionService::class.java)
        stopService(intent)
    }
    
    /**
     * Processes a voice command and executes the corresponding gesture.
     */
    fun processVoiceCommand(spokenText: String) {
        Log.d(TAG, "Processing command: $spokenText")
        
        val command = commandParser.parse(spokenText)
        
        when (command) {
            is CommandParser.Command.Tap -> executeTap(command.position)
            is CommandParser.Command.Swipe -> executeSwipe(command.startPosition, command.endPosition)
            is CommandParser.Command.Circle -> executeCircle(command.centerPosition)
            is CommandParser.Command.Home -> executeHome()
            is CommandParser.Command.Back -> executeBack()
            is CommandParser.Command.Recents -> executeRecents()
            is CommandParser.Command.Unknown -> Log.w(TAG, "Unknown command: $spokenText")
        }
    }
    
    /**
     * Executes a tap gesture at the specified grid position.
     */
    private fun executeTap(gridPosition: String) {
        val point = gridMapper.gridToCoordinates(gridPosition)
        if (point == null) {
            Log.w(TAG, "Invalid grid position: $gridPosition")
            return
        }
        
        val path = Path()
        path.moveTo(point.x.toFloat(), point.y.toFloat())
        
        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, 100))
        
        dispatchGesture(gestureBuilder.build(), null, null)
        Log.d(TAG, "Executed tap at $gridPosition (${point.x}, ${point.y})")
    }
    
    /**
     * Executes a swipe gesture from start to end position.
     */
    private fun executeSwipe(startPosition: String, endPosition: String) {
        val startPoint = gridMapper.gridToCoordinates(startPosition)
        val endPoint = gridMapper.gridToCoordinates(endPosition)
        
        if (startPoint == null || endPoint == null) {
            Log.w(TAG, "Invalid swipe positions: $startPosition to $endPosition")
            return
        }
        
        val path = Path()
        path.moveTo(startPoint.x.toFloat(), startPoint.y.toFloat())
        path.lineTo(endPoint.x.toFloat(), endPoint.y.toFloat())
        
        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, 500))
        
        dispatchGesture(gestureBuilder.build(), null, null)
        Log.d(TAG, "Executed swipe from $startPosition to $endPosition")
    }
    
    /**
     * Executes a circle gesture at the specified center position.
     */
    private fun executeCircle(centerPosition: String) {
        val center = gridMapper.gridToCoordinates(centerPosition)
        if (center == null) {
            Log.w(TAG, "Invalid circle position: $centerPosition")
            return
        }
        
        val path = Path()
        val radius = 100f
        
        // Draw a circle
        path.addCircle(center.x.toFloat(), center.y.toFloat(), radius, Path.Direction.CW)
        
        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, 1000))
        
        dispatchGesture(gestureBuilder.build(), null, null)
        Log.d(TAG, "Executed circle at $centerPosition")
    }
    
    /**
     * Executes the Home button action.
     */
    private fun executeHome() {
        performGlobalAction(GLOBAL_ACTION_HOME)
        Log.d(TAG, "Executed Home action")
    }
    
    /**
     * Executes the Back button action.
     */
    private fun executeBack() {
        performGlobalAction(GLOBAL_ACTION_BACK)
        Log.d(TAG, "Executed Back action")
    }
    
    /**
     * Executes the Recents action.
     */
    private fun executeRecents() {
        performGlobalAction(GLOBAL_ACTION_RECENTS)
        Log.d(TAG, "Executed Recents action")
    }
}
