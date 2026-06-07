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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isEnabled = prefs.getBoolean("master_enabled", false)
        binding.masterToggle.isChecked = isEnabled

        binding.masterToggle.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("master_enabled", isChecked).apply()
            if (isChecked) {
                startMonitoring()
            } else {
                stopMonitoring()
            }
        }

        binding.btnAccessibilitySettings.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
    }

    override fun onResume() {
        super.onResume()
        updateAccessibilityStatus()
    }

    private fun updateAccessibilityStatus() {
        val enabled = isAccessibilityServiceEnabled()
        binding.accessibilityStatus.text = if (enabled) {
            binding.accessibilityStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            "Accessibility Service: Enabled"
        } else {
            binding.accessibilityStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            "Accessibility Service: Disabled"
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
        WorkManager.getInstance(this).cancelUniqueWork("check_tethering")
    }

    private fun schedulePeriodicCheck() {
        val request = PeriodicWorkRequestBuilder<CheckTetheringWorker>(15, TimeUnit.MINUTES)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "check_tethering",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
