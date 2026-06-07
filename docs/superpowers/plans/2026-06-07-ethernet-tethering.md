# Ethernet Tethering Auto-Toggle Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Create a Kotlin Android app that automatically enables Ethernet tethering when an Ethernet cable is connected using an Accessibility Service.

**Architecture:** A Foreground Service monitors Ethernet state via `NetworkCallback` and `WorkManager` (10-min pulse). When Ethernet is detected, it triggers an Accessibility Service to automate the UI toggle in System Settings.

**Tech Stack:** Kotlin, Android SDK, ViewBinding, WorkManager, AccessibilityService.

---

### Task 1: Project Scaffolding

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts`
- Create: `app/build.gradle.kts`
- Create: `gradle.properties`

- [ ] **Step 1: Create root settings and build files**
```kotlin
// settings.gradle.kts
rootProject.name = "ForceEthernetTethering"
include(":app")
```
- [ ] **Step 2: Create app-level build.gradle.kts**
```kotlin
// app/build.gradle.kts
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.forcethernet"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.forcethernet"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
}
```
- [ ] **Step 3: Commit scaffolding**
```bash
git add .
git commit -m "chore: initial project scaffolding"
```

---

### Task 2: Manifest & Permissions

**Files:**
- Create: `app/src/main/AndroidManifest.xml`

- [ ] **Step 1: Define permissions and components**
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="Ethernet Auto-Toggle"
        android:theme="@style/Theme.AppCompat.Light.NoActionBar">
        
        <activity android:name=".MainActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <service android:name=".EthernetMonitorService" android:exported="false" />
        
        <service
            android:name=".TetheringAccessibilityService"
            android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"
            android:exported="false">
            <intent-filter>
                <action android:name="android.accessibilityservice.AccessibilityService" />
            </intent-filter>
            <meta-data
                android:name="android.accessibilityservice"
                android:resource="@xml/accessibility_service_config" />
        </service>

        <receiver android:name=".BootReceiver" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>
    </application>
</manifest>
```
- [ ] **Step 2: Commit manifest**
```bash
git commit -m "feat: add manifest and service declarations"
```

---

### Task 3: Accessibility Configuration

**Files:**
- Create: `app/src/main/res/xml/accessibility_service_config.xml`

- [ ] **Step 1: Configure the Accessibility Service**
```xml
<accessibility-service xmlns:android="http://schemas.android.com/apk/res/android"
    android:accessibilityEventTypes="typeWindowStateChanged"
    android:accessibilityFeedbackType="feedbackGeneric"
    android:accessibilityFlags="flagDefault"
    android:canRetrieveWindowContent="true"
    android:description="Automatically toggles Ethernet tethering in settings."
    android:packageNames="com.android.settings" />
```
- [ ] **Step 2: Commit config**
```bash
git commit -m "feat: add accessibility service configuration"
```

---

### Task 4: TetheringAccessibilityService Implementation

**Files:**
- Create: `app/src/main/kotlin/com/example/forcethernet/TetheringAccessibilityService.kt`

- [ ] **Step 1: Implement the macro logic**
```kotlin
package com.example.forcethernet

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class TetheringAccessibilityService : AccessibilityService() {
    private var isTaskPending = false

    override fun onStartCommand(intent: android.content.Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "ACTION_ENABLE_TETHERING") {
            isTaskPending = true
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!isTaskPending) return
        
        val rootNode = rootInActiveWindow ?: return
        val nodes = rootNode.findAccessibilityNodeInfosByText("Ethernet tethering")
        
        for (node in nodes) {
            val parent = node.parent
            if (parent != null && (parent.isClickable || parent.isCheckable)) {
                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                isTaskPending = false
                performGlobalAction(GLOBAL_ACTION_BACK)
                return
            }
        }
    }

    override fun onInterrupt() {}
}
```
- [ ] **Step 2: Commit service**
```bash
git commit -m "feat: implement TetheringAccessibilityService"
```

---

### Task 5: EthernetMonitorService Implementation

**Files:**
- Create: `app/src/main/kotlin/com/example/forcethernet/EthernetMonitorService.kt`

- [ ] **Step 1: Implement network monitoring**
```kotlin
package com.example.forcethernet

import android.app.*
import android.content.Intent
import android.net.*
import android.os.IBinder
import androidx.core.app.NotificationCompat

class EthernetMonitorService : Service() {
    private lateinit var connectivityManager: ConnectivityManager

    override fun onCreate() {
        super.onCreate()
        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        startForeground(1, createNotification())
        registerNetworkCallback()
    }

    private fun registerNetworkCallback() {
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()
        
        connectivityManager.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                triggerTethering()
            }
        })
    }

    private fun triggerTethering() {
        val intent = Intent(Settings.ACTION_TETHER_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        
        val serviceIntent = Intent(this, TetheringAccessibilityService::class.java).apply {
            action = "ACTION_ENABLE_TETHERING"
        }
        startService(serviceIntent)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "monitor_channel")
            .setContentTitle("Ethernet Monitoring Active")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
```
- [ ] **Step 2: Commit service**
```bash
git commit -m "feat: implement EthernetMonitorService"
```

---

### Task 6: Periodic Check with WorkManager

**Files:**
- Create: `app/src/main/kotlin/com/example/forcethernet/CheckTetheringWorker.kt`

- [ ] **Step 1: Implement the 10-minute worker**
```kotlin
package com.example.forcethernet

import android.content.Context
import android.content.Intent
import androidx.work.*
import java.util.concurrent.TimeUnit

class CheckTetheringWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        // Logic to check if Ethernet is connected and tethering is OFF
        // If needed, trigger the service
        return Result.success()
    }

    companion object {
        fun enqueue(context: Context) {
            val request = PeriodicWorkRequestBuilder<CheckTetheringWorker>(10, TimeUnit.MINUTES).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork("check_tether", ExistingPeriodicWorkPolicy.KEEP, request)
        }
    }
}
```
- [ ] **Step 2: Commit worker**
```bash
git commit -m "feat: add periodic CheckTetheringWorker"
```

---

### Task 7: MainActivity & UI

**Files:**
- Create: `app/src/main/kotlin/com/example/forcethernet/MainActivity.kt`

- [ ] **Step 1: Implement the UI with toggle**
```kotlin
package com.example.forcethernet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.forcethernet.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.masterToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Start services
            } else {
                // Stop services
            }
        }
    }
}
```
- [ ] **Step 2: Commit UI**
```bash
git commit -m "feat: implement MainActivity with toggle"
```
