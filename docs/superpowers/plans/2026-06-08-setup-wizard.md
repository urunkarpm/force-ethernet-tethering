# Setup Wizard Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement a 4-step Setup Wizard to guide users through necessary permissions (Notifications, Accessibility, Battery Optimization) for the app to function correctly.

**Architecture:** A new `SetupActivity` will manage the flow using a state machine (current step index). It will be launched by `MainActivity` if setup is incomplete.

**Tech Stack:** Kotlin, Android SDK, ViewBinding, SharedPreferences.

---

### Task 1: SetupActivity Scaffolding & Routing

**Files:**
- Create: `app/src/main/kotlin/com/example/forcethernet/SetupActivity.kt`
- Modify: `app/src/main/kotlin/com/example/forcethernet/MainActivity.kt`
- Modify: `app/src/main/AndroidManifest.xml`

- [ ] **Step 1: Create SetupActivity class**
```kotlin
package com.example.forcethernet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.forcethernet.databinding.ActivitySetupBinding

class SetupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
```

- [ ] **Step 2: Add SetupActivity to Manifest**
```xml
<activity android:name=".SetupActivity" android:exported="false" />
```

- [ ] **Step 3: Update MainActivity to check setup status**
```kotlin
// In MainActivity.onCreate
val isSetupComplete = prefs.getBoolean("setup_complete", false)
if (!isSetupComplete) {
    startActivity(Intent(this, SetupActivity::class.java))
    finish()
    return
}
```

- [ ] **Step 4: Commit Scaffolding**
```bash
git add .
git commit -m "feat: add SetupActivity scaffolding and routing"
```

---

### Task 2: Setup Wizard Layouts

**Files:**
- Create: `app/src/main/res/layout/activity_setup.xml`

- [ ] **Step 1: Create activity_setup.xml with container and buttons**
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="24dp">

    <TextView
        android:id="@+id/step_indicator"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Step 1 of 4"
        android:textSize="14sp"
        android:alpha="0.6" />

    <FrameLayout
        android:id="@+id/step_container"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="end">

        <Button
            android:id="@+id/btn_skip"
            style="@style/Widget.MaterialComponents.Button.TextButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Skip"
            android:visibility="gone" />

        <Button
            android:id="@+id/btn_next"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Next" />
    </LinearLayout>
</LinearLayout>
```

- [ ] **Step 2: Commit Layout**
```bash
git commit -m "feat: add activity_setup layout"
```

---

### Task 3: SetupActivity Logic (State Machine)

**Files:**
- Modify: `app/src/main/kotlin/com/example/forcethernet/SetupActivity.kt`

- [ ] **Step 1: Implement step management logic**
```kotlin
enum class SetupStep { WELCOME, NOTIFICATIONS, ACCESSIBILITY, BATTERY, COMPLETE }

private var currentStep = SetupStep.WELCOME

private fun updateUI() {
    when (currentStep) {
        SetupStep.WELCOME -> showWelcome()
        SetupStep.NOTIFICATIONS -> showNotifications()
        SetupStep.ACCESSIBILITY -> showAccessibility()
        SetupStep.BATTERY -> showBattery()
        SetupStep.COMPLETE -> finishSetup()
    }
}

private fun finishSetup() {
    prefs.edit().putBoolean("setup_complete", true).apply()
    startActivity(Intent(this, MainActivity::class.java))
    finish()
}
```

- [ ] **Step 2: Commit Logic**
```bash
git commit -m "feat: implement SetupActivity state machine"
```

---

### Task 4: Permission Handling & Final Polish

**Files:**
- Modify: `app/src/main/kotlin/com/example/forcethernet/SetupActivity.kt`
- Modify: `app/src/main/kotlin/com/example/forcethernet/MainActivity.kt` (Remove redundant checks)

- [ ] **Step 1: Implement specific permission request logic in SetupActivity**
- [ ] **Step 2: Clean up MainActivity UI and logic**
- [ ] **Step 3: Run app to verify flow**
- [ ] **Step 4: Final Commit**
```bash
git add .
git commit -m "feat: complete Setup Wizard implementation"
```
