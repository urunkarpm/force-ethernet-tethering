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
import android.widget.FrameLayout
import android.widget.LinearLayout
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
                    try {
                        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                            data = Uri.parse("package:$packageName")
                        }
                        startActivity(intent)
                    } catch (e: Exception) {
                        // Fallback if the direct intent fails
                        startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
                    }
                } else {
                    advanceStep()
                }
            }
            SetupStep.COMPLETE -> finishSetup()
        }
    }

    private fun advanceStep() {
        val entries = SetupStep.entries
        val nextOrdinal = currentStep.ordinal + 1
        if (nextOrdinal < entries.size) {
            currentStep = entries[nextOrdinal]
            updateUI()
        }
    }

    private fun updateUI() {
        val totalSteps = SetupStep.entries.size
        val progress = ((currentStep.ordinal + 1) * 100) / totalSteps
        binding.stepProgress.setProgressCompat(progress, true)
        
        binding.stepIndicator.text = "Step ${currentStep.ordinal + 1} of $totalSteps"
        binding.stepContainer.removeAllViews()

        val context = this
        val stepLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
        }

        val iconView = TextView(context).apply {
            textSize = 64f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 32)
        }

        val titleView = TextView(context).apply {
            textSize = 28f
            setPadding(0, 0, 0, 16)
            gravity = Gravity.CENTER
            setTextColor(ContextCompat.getColor(context, android.R.color.white))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }

        val descriptionView = TextView(context).apply {
            textSize = 17f
            gravity = Gravity.CENTER
            setTextColor(ContextCompat.getColor(context, R.color.on_surface_variant))
            alpha = 0.8f
            setLineSpacing(0f, 1.3f)
        }

        when (currentStep) {
            SetupStep.WELCOME -> {
                iconView.text = "👋"
                titleView.text = "Welcome"
                descriptionView.text = "This app will help you keep Ethernet Tethering enabled automatically when a cable is connected."
                binding.btnSkip.visibility = View.GONE
                binding.btnNext.text = "Get Started"
            }
            SetupStep.NOTIFICATIONS -> {
                iconView.text = "🔔"
                titleView.text = "Stay Informed"
                descriptionView.text = "Grant notification permission so the app can show the status of Ethernet monitoring."
                binding.btnSkip.visibility = View.VISIBLE
                binding.btnNext.text = "Grant Permission"
            }
            SetupStep.ACCESSIBILITY -> {
                iconView.text = "⚙️"
                titleView.text = "Accessibility"
                descriptionView.text = "The app needs Accessibility access to click the 'Ethernet tethering' toggle for you.\n\nFind 'Force Ethernet' and turn it ON."
                binding.btnSkip.visibility = View.GONE
                binding.btnNext.text = "Open Settings"
            }
            SetupStep.BATTERY -> {
                iconView.text = "⚡"
                titleView.text = "Battery"
                descriptionView.text = "To ensure stable monitoring in the background, please exclude the app from battery optimizations."
                binding.btnSkip.visibility = View.VISIBLE
                binding.btnNext.text = "Ignore Optimization"
            }
            SetupStep.COMPLETE -> {
                iconView.text = "🎉"
                titleView.text = "You're All Set!"
                descriptionView.text = "Setup is complete. The app will now monitor your Ethernet connection."
                binding.btnSkip.visibility = View.GONE
                binding.btnNext.text = "Finish"
            }
        }

        stepLayout.addView(iconView)
        stepLayout.addView(titleView)
        stepLayout.addView(descriptionView)
        binding.stepContainer.addView(stepLayout)
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
