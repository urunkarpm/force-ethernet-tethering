# MainActivity & UI Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement the main entry point of the application, providing a master toggle for Ethernet monitoring and handling permission checks for the Accessibility Service.

**Architecture:** 
- `MainActivity` manages the master state (stored in `SharedPreferences`).
- It starts/stops `EthernetMonitorService` and schedules `CheckTetheringWorker`.
- `BootReceiver` restores the service state on device reboot.
- `activity_main.xml` provides the user interface.

**Tech Stack:** Kotlin, Android SDK, ViewBinding, Material Components, SharedPreferences, WorkManager.

---

### Task 1: UI Layout

**Files:**
- Create: `app/src/main/res/layout/activity_main.xml`

- [ ] **Step 1: Create activity_main.xml layout**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="24dp"
    android:gravity="center_horizontal">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Ethernet Auto-Toggle"
        android:textSize="24sp"
        android:textStyle="bold"
        android:layout_marginBottom="32dp" />

    <com.google.android.material.switchmaterial.SwitchMaterial
        android:id="@+id/master_toggle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Enable Auto-Tethering"
        android:textSize="18sp"
        android:layout_marginBottom="24dp" />

    <TextView
        android:id="@+id/accessibility_status"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Accessibility Service: Disabled"
        android:textColor="#FF0000"
        android:layout_marginBottom="16dp" />

    <Button
        android:id="@+id/btn_accessibility_settings"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Open Accessibility Settings" />

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="32dp"
        android:text="Instructions:\n1. Grant Accessibility permission.\n2. Enable the master toggle.\n3. App will auto-enable Ethernet Tethering when cable is detected."
        android:alpha="0.7" />

</LinearLayout>
```

### Task 2: MainActivity Logic

**Files:**
- Create: `app/src/main/kotlin/com/example/forcethernet/MainActivity.kt`

- [ ] **Step 1: Implement MainActivity class with ViewBinding and logic**

```kotlin
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
```

### Task 3: BootReceiver Implementation

**Files:**
- Create: `app/src/main/kotlin/com/example/forcethernet/BootReceiver.kt`

- [ ] **Step 1: Implement BootReceiver class**

```kotlin
package com.example.forcethernet

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val isEnabled = prefs.getBoolean("master_enabled", false)
            if (isEnabled) {
                val serviceIntent = Intent(context, EthernetMonitorService::class.java)
                ContextCompat.startForegroundService(context, serviceIntent)
            }
        }
    }
}
```

### Task 4: Final Validation and Commit

- [ ] **Step 1: Verify files exist and project builds**
- [ ] **Step 2: Final commit**

```bash
git add .
git commit -m "feat: implement MainActivity, UI, and BootReceiver"
```
