package com.example.forcethernet

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils
import android.view.Gravity
import android.view.View
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
        binding.stepProgress.progress = (currentStep.ordinal + 1) * 20 // 5 steps total

        binding.stepContainer.removeAllViews()

        val iconView = TextView(this).apply {
            textSize = 48f
            setPadding(0, 0, 0, 24)
            gravity = Gravity.CENTER
        }

        val titleView = TextView(this).apply {
            textSize = 24f
            setPadding(0, 0, 0, 12)
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
        }

        val descriptionView = TextView(this).apply {
            textSize = 16f
            gravity = Gravity.CENTER
            setTextColor(ContextCompat.getColor(this@SetupActivity, R.color.muted_gray))
        }

        when (currentStep) {
            SetupStep.WELCOME -> {
                iconView.text = "👋"
                titleView.text = getString(R.string.setup_welcome_title)
                descriptionView.text = getString(R.string.setup_welcome_desc)
                binding.btnSkip.visibility = View.GONE
                binding.btnNext.text = getString(R.string.setup_btn_get_started)
            }
            SetupStep.NOTIFICATIONS -> {
                iconView.text = "🔔"
                titleView.text = getString(R.string.setup_notifications_title)
                descriptionView.text = getString(R.string.setup_notifications_desc)
                binding.btnSkip.visibility = View.VISIBLE
                binding.btnNext.text = getString(R.string.setup_btn_grant_permission)
            }
            SetupStep.ACCESSIBILITY -> {
                iconView.text = "♿"
                titleView.text = getString(R.string.setup_accessibility_title)
                descriptionView.text = getString(R.string.setup_accessibility_desc)
                binding.btnSkip.visibility = View.GONE
                binding.btnNext.text = getString(R.string.setup_btn_open_settings)
            }
            SetupStep.BATTERY -> {
                iconView.text = "🔋"
                titleView.text = getString(R.string.setup_battery_title)
                descriptionView.text = getString(R.string.setup_battery_desc)
                binding.btnSkip.visibility = View.VISIBLE
                binding.btnNext.text = getString(R.string.setup_btn_ignore_optimization)
            }
            SetupStep.COMPLETE -> {
                iconView.text = "✅"
                titleView.text = getString(R.string.setup_complete_title)
                descriptionView.text = getString(R.string.setup_complete_desc)
                binding.btnSkip.visibility = View.GONE
                binding.btnNext.text = getString(R.string.setup_btn_finish)
            }
        }

        binding.stepContainer.addView(iconView)
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
