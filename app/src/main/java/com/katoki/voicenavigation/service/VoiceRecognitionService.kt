package com.katoki.voicenavigation.service

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

/**
 * Background service that continuously listens for voice commands.
 */
class VoiceRecognitionService : Service() {
    
    companion object {
        private const val TAG = "VoiceRecognitionService"
        private var isListening = false
        
        fun isListening(): Boolean = isListening
    }
    
    private var speechRecognizer: SpeechRecognizer? = null
    private var recognizerIntent: Intent? = null
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Voice Recognition Service Created")
        initializeSpeechRecognizer()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Voice Recognition Service Started")
        startListening()
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Voice Recognition Service Destroyed")
        stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
    
    /**
     * Initializes the speech recognizer.
     */
    private fun initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer?.setRecognitionListener(VoiceRecognitionListener())
            
            recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }
        } else {
            Log.e(TAG, "Speech recognition not available")
        }
    }
    
    /**
     * Starts listening for voice commands.
     */
    private fun startListening() {
        recognizerIntent?.let {
            try {
                speechRecognizer?.startListening(it)
                isListening = true
                Log.d(TAG, "Started listening")
            } catch (e: Exception) {
                Log.e(TAG, "Error starting listening", e)
                isListening = false
            }
        }
    }
    
    /**
     * Stops listening for voice commands.
     */
    private fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            isListening = false
            Log.d(TAG, "Stopped listening")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping listening", e)
        }
    }
    
    /**
     * RecognitionListener implementation for handling speech recognition events.
     */
    private inner class VoiceRecognitionListener : RecognitionListener {
        
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d(TAG, "Ready for speech")
        }
        
        override fun onBeginningOfSpeech() {
            Log.d(TAG, "Beginning of speech")
        }
        
        override fun onRmsChanged(rmsdB: Float) {
            // Audio level changed - can be used for visual feedback
        }
        
        override fun onBufferReceived(buffer: ByteArray?) {
            // Audio buffer received
        }
        
        override fun onEndOfSpeech() {
            Log.d(TAG, "End of speech")
        }
        
        override fun onError(error: Int) {
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No match"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                SpeechRecognizer.ERROR_SERVER -> "Server error"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
                else -> "Unknown error"
            }
            
            Log.w(TAG, "Recognition error: $errorMessage")
            
            // Restart listening unless it's a permission error
            if (error != SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                // Restart after a short delay
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    startListening()
                }, 500)
            }
        }
        
        override fun onResults(results: Bundle?) {
            results?.let {
                val matches = it.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spokenText = matches[0]
                    Log.d(TAG, "Recognized: $spokenText")
                    
                    // Send command to accessibility service
                    VoiceNavigationAccessibilityService.getInstance()?.processVoiceCommand(spokenText)
                }
            }
            
            // Restart listening for continuous recognition
            startListening()
        }
        
        override fun onPartialResults(partialResults: Bundle?) {
            // Partial results can be used for real-time feedback
        }
        
        override fun onEvent(eventType: Int, params: Bundle?) {
            // Additional events
        }
    }
}
