package com.example.forcethernet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.forcethernet.databinding.ActivitySetupBinding

enum class SetupStep { WELCOME, NOTIFICATIONS, ACCESSIBILITY, BATTERY, COMPLETE }

class SetupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetupBinding
    private val prefs by lazy { getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    private var currentStep = SetupStep.WELCOME

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

    private fun handleNext() {
        if (currentStep == SetupStep.ACCESSIBILITY) {
            // Requirement for ACCESSIBILITY step: "Open Settings"
            // For now just advance, but real implementation would open settings
            advanceStep()
        } else {
            advanceStep()
        }
    }

    private fun advanceStep() {
        val nextOrdinal = currentStep.ordinal + 1
        if (nextOrdinal < SetupStep.entries.size) {
            currentStep = SetupStep.entries[nextOrdinal]
            updateUI()
        }
    }

    private fun updateUI() {
        if (currentStep == SetupStep.COMPLETE) {
            finishSetup()
            return
        }

        binding.stepIndicator.text = "Step ${currentStep.ordinal + 1} of 4"

        // For now, clear the container and add a placeholder TextView
        binding.stepContainer.removeAllViews()
        val textView = TextView(this).apply {
            text = "Content for ${currentStep.name}"
            textSize = 20f
            gravity = Gravity.CENTER
        }
        binding.stepContainer.addView(textView)

        when (currentStep) {
            SetupStep.WELCOME -> {
                binding.btnSkip.visibility = View.GONE
                binding.btnNext.text = "Get Started"
            }
            SetupStep.NOTIFICATIONS -> {
                binding.btnSkip.visibility = View.VISIBLE
                binding.btnNext.text = "Next"
            }
            SetupStep.ACCESSIBILITY -> {
                binding.btnSkip.visibility = View.GONE // Required
                binding.btnNext.text = "Open Settings"
            }
            SetupStep.BATTERY -> {
                binding.btnSkip.visibility = View.VISIBLE
                binding.btnNext.text = "Next"
            }
            SetupStep.COMPLETE -> {
                // Handled above
            }
        }
    }

    private fun finishSetup() {
        prefs.edit().putBoolean("setup_complete", true).apply()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
