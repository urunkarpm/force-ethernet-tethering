package com.example.forcethernet

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.forcethernet.databinding.ActivitySetupBinding

enum class SetupStep { WELCOME, NOTIFICATIONS, ACCESSIBILITY, BATTERY, COMPLETE }

class SetupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetupBinding
    private val prefs by lazy { getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    private var currentStep = SetupStep.WELCOME

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            advanceStep()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.setOnClickListener {
            handleNext()
        }

        binding.btnSkip.setOnClickListener {
            advanceStep()
        }

        updateUI()
    }

    override fun onResume() {
        super.onResume()
        checkPermissionsAndAdvance()
    }

    private fun checkPermissionsAndAdvance() {
        when (currentStep) {
            SetupStep.NOTIFICATIONS -> {
                if (hasNotificationPermission()) {
                    advanceStep()
                }
            }
            SetupStep.ACCESSIBILITY -> {
                if (isAccessibilityServiceEnabled()) {
                    advanceStep()
                }
            }
            SetupStep.BATTERY -> {
                if (isIgnoringBatteryOptimizations()) {
                    advanceStep()
                }
            }
            else -> {}
        }
    }

    private fun handleNext() {
        when (currentStep) {
            SetupStep.WELCOME -> advanceStep()
            SetupStep.NOTIFICATIONS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    advanceStep()
                }
            }
            SetupStep.ACCESSIBILITY -> {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
            SetupStep.BATTERY -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:$packageName")
                    }
                    startActivity(intent)
                } else {
                    advanceStep()
                }
            }
            SetupStep.COMPLETE -> finishSetup()
        }
    }

    private fun advanceStep() {
        val nextOrdinal = currentStep.ordinal + 1
        if (nextOrdinal < SetupStep.entries.size) {
            currentStep = SetupStep.entries[nextOrdinal]
            updateUI()
        } else if (currentStep == SetupStep.BATTERY) {
            currentStep = SetupStep.COMPLETE
            updateUI()
        }
    }

    private fun updateUI() {
        if (currentStep == SetupStep.COMPLETE) {
            finishSetup()
            return
        }

        binding.stepIndicator.text = "Step ${currentStep.ordinal + 1} of 4"
        binding.stepContainer.removeAllViews()

        val titleView = TextView(this).apply {
            textSize = 22f
            setPadding(0, 0, 0, 16)
            gravity = Gravity.CENTER
            setTextColor(ContextCompat.getColor(context, android.R.color.black))
        }

        val descriptionView = TextView(this).apply {
            textSize = 16f
            gravity = Gravity.CENTER
            alpha = 0.8f
        }

        when (currentStep) {
            SetupStep.WELCOME -> {
                titleView.text = "Welcome to Force Ethernet"
                descriptionView.text = "This app will help you keep Ethernet Tethering enabled automatically when a cable is connected."
                binding.btnSkip.visibility = View.GONE
                binding.btnNext.text = "Get Started"
            }
            SetupStep.NOTIFICATIONS -> {
                titleView.text = "Stay Informed"
                descriptionView.text = "Grant notification permission so the app can show the status of Ethernet monitoring."
                binding.btnSkip.visibility = View.VISIBLE
                binding.btnNext.text = "Grant Permission"
            }
            SetupStep.ACCESSIBILITY -> {
                titleView.text = "Accessibility Service"
                descriptionView.text = "The app needs Accessibility access to click the 'Ethernet tethering' toggle for you.\n\nFind 'Force Ethernet' in settings and turn it ON."
                binding.btnSkip.visibility = View.GONE
                binding.btnNext.text = "Open Settings"
            }
            SetupStep.BATTERY -> {
                titleView.text = "Battery Optimization"
                descriptionView.text = "To ensure stable monitoring in the background, please exclude the app from battery optimizations."
                binding.btnSkip.visibility = View.VISIBLE
                binding.btnNext.text = "Ignore Optimization"
            }
            else -> {}
        }

        binding.stepContainer.addView(titleView)
        binding.stepContainer.addView(descriptionView)
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
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

    private fun isIgnoringBatteryOptimizations(): Boolean {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pm.isIgnoringBatteryOptimizations(packageName)
        } else {
            true
        }
    }

    private fun finishSetup() {
        prefs.edit().putBoolean("setup_complete", true).apply()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
