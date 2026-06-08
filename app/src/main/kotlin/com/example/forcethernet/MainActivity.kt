package com.example.forcethernet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.*
import com.example.forcethernet.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val prefs by lazy { getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isSetupComplete = prefs.getBoolean("setup_complete", false)
        if (!isSetupComplete) {
            startActivity(Intent(this, SetupActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isEnabled = prefs.getBoolean("master_enabled", false)
        binding.masterToggle.isChecked = isEnabled
        updateStatusUI(isEnabled)

        if (isEnabled) {
            startMonitoring()
        }

        binding.masterToggle.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("master_enabled", isChecked).apply()
            updateStatusUI(isChecked)
            if (isChecked) {
                startMonitoring()
            } else {
                stopMonitoring()
            }
        }
    }

    private fun updateStatusUI(isEnabled: Boolean) {
        if (isEnabled) {
            binding.statusText.text = "ENABLED"
            binding.statusText.setTextColor(ContextCompat.getColor(this, R.color.status_enabled))
        } else {
            binding.statusText.text = "DISABLED"
            binding.statusText.setTextColor(ContextCompat.getColor(this, R.color.status_disabled))
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expectedService = "${packageName}/${TetheringAccessibilityService::class.java.canonicalName}"
        val enabledServices = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        enabledServices?.let {
            colonSplitter.setString(it)
            while (colonSplitter.hasNext()) {
                if (colonSplitter.next().equals(expectedService, ignoreCase = true)) return true
            }
        }
        return false
    }

    private fun startMonitoring() {
        if (!isAccessibilityServiceEnabled()) {
            Toast.makeText(this, "Please enable Accessibility Service first!", Toast.LENGTH_LONG).show()
        }
        val intent = Intent(this, EthernetMonitorService::class.java)
        ContextCompat.startForegroundService(this, intent)
        schedulePeriodicCheck()
    }

    private fun stopMonitoring() {
        val intent = Intent(this, EthernetMonitorService::class.java)
        stopService(intent)
        WorkManager.getInstance(this).cancelUniqueWork("check_tether")
    }

    private fun schedulePeriodicCheck() {
        CheckTetheringWorker.enqueue(this)
    }
}
