package com.katoki.voicenavigation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.katoki.voicenavigation.R
import com.katoki.voicenavigation.databinding.ActivityMainBinding
import com.katoki.voicenavigation.service.VoiceNavigationAccessibilityService
import com.katoki.voicenavigation.service.VoiceRecognitionService

/**
 * Main activity with UI controls to enable/disable the voice navigation service.
 */
class MainActivity : AppCompatActivity() {
    
    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
    
    private lateinit var binding: ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper())
    private var statusUpdateRunnable: Runnable? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        checkPermissions()
        startStatusUpdates()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopStatusUpdates()
    }
    
    override fun onResume() {
        super.onResume()
        updateServiceStatus()
    }
    
    /**
     * Sets up UI controls and listeners.
     */
    private fun setupUI() {
        binding.toggleButton.setOnClickListener {
            if (VoiceNavigationAccessibilityService.isServiceEnabled()) {
                // Service is enabled, show how to disable it
                openAccessibilitySettings()
            } else {
                // Service is disabled, show how to enable it
                openAccessibilitySettings()
            }
        }
        
        binding.settingsButton.setOnClickListener {
            openAccessibilitySettings()
        }
        
        binding.permissionButton.setOnClickListener {
            requestMicrophonePermission()
        }
    }
    
    /**
     * Checks and requests microphone permission if needed.
     */
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            binding.permissionButton.visibility = View.VISIBLE
        } else {
            binding.permissionButton.visibility = View.GONE
        }
    }
    
    /**
     * Requests microphone permission.
     */
    private fun requestMicrophonePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            PERMISSION_REQUEST_CODE
        )
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.permissionButton.visibility = View.GONE
            }
        }
    }
    
    /**
     * Opens accessibility settings to enable/disable the service.
     */
    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }
    
    /**
     * Updates the service status in the UI.
     */
    private fun updateServiceStatus() {
        val isEnabled = VoiceNavigationAccessibilityService.isServiceEnabled()
        val isListening = VoiceRecognitionService.isListening()
        
        if (isEnabled) {
            binding.statusText.text = getString(R.string.service_enabled)
            binding.statusText.setTextColor(
                ContextCompat.getColor(this, R.color.teal_700)
            )
            binding.toggleButton.text = getString(R.string.disable_service)
        } else {
            binding.statusText.text = getString(R.string.service_disabled)
            binding.statusText.setTextColor(
                ContextCompat.getColor(this, android.R.color.holo_red_dark)
            )
            binding.toggleButton.text = getString(R.string.enable_service)
        }
        
        if (isListening) {
            binding.listeningStatus.text = getString(R.string.listening)
            binding.listeningStatus.setTextColor(
                ContextCompat.getColor(this, R.color.teal_700)
            )
        } else {
            binding.listeningStatus.text = getString(R.string.not_listening)
            binding.listeningStatus.setTextColor(
                ContextCompat.getColor(this, android.R.color.darker_gray)
            )
        }
    }
    
    /**
     * Starts periodic status updates.
     */
    private fun startStatusUpdates() {
        statusUpdateRunnable = object : Runnable {
            override fun run() {
                updateServiceStatus()
                handler.postDelayed(this, 1000) // Update every second
            }
        }
        handler.post(statusUpdateRunnable!!)
    }
    
    /**
     * Stops periodic status updates.
     */
    private fun stopStatusUpdates() {
        statusUpdateRunnable?.let {
            handler.removeCallbacks(it)
        }
    }
}
