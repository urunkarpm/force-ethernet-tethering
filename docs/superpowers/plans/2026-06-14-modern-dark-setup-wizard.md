# Modern Dark Setup Wizard Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement a 5-step Setup Wizard with a "Modern Dark" aesthetic (Pure Black, Blue Glow) and ensure this UX is consistent across the entire app.

**Architecture:** 
- Centralized `colors.xml` and `themes.xml` updates to lock in the Design System globally.
- `SetupActivity` as a state-driven wizard using a dynamic `stepContainer` to swap view logic.
- `MainActivity` UI refactor to align with the new design tokens.

**Tech Stack:** Kotlin, Android SDK, ViewBinding, SharedPreferences.

---

### Task 1: Global Theme & Color Tokens

**Files:**
- Modify: `app/src/main/res/values/colors.xml`
- Modify: `app/src/main/res/values/themes.xml`

- [ ] **Step 1: Update `colors.xml` with Modern Dark tokens**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="black">#000000</color>
    <color name="dark_gray">#1C1C1E</color>
    <color name="blue_vibrant">#0A84FF</color>
    <color name="blue_glow">#800A84FF</color>
    <color name="white">#FFFFFF</color>
    <color name="muted_gray">#8E8E93</color>

    <color name="primary">@color/blue_vibrant</color>
    <color name="primary_variant">@color/black</color>
    <color name="on_primary">@color/white</color>
    <color name="secondary">@color/blue_vibrant</color>
    <color name="on_secondary">@color/white</color>
    <color name="background">@color/black</color>
    <color name="surface">@color/dark_gray</color>
    <color name="on_surface">@color/white</color>
    <color name="on_surface_variant">@color/muted_gray</color>
</resources>
```

- [ ] **Step 2: Update `themes.xml` to use Dark Mode defaults**
```xml
<resources>
    <style name="Theme.ForceEthernet" parent="Theme.MaterialComponents.DayNight.NoActionBar">
        <item name="colorPrimary">@color/primary</item>
        <item name="colorPrimaryVariant">@color/primary_variant</item>
        <item name="colorOnPrimary">@color/on_primary</item>
        <item name="colorSecondary">@color/secondary</item>
        <item name="colorOnSecondary">@color/on_secondary</item>
        <item name="android:statusBarColor">@color/black</item>
        <item name="android:windowBackground">@color/black</item>
        <item name="colorSurface">@color/surface</item>
        <item name="colorOnSurface">@color/on_surface</item>
    </style>
</resources>
```

- [ ] **Step 3: Commit Theme Changes**
```bash
git add app/src/main/res/values/colors.xml app/src/main/res/values/themes.xml
git commit -m "style: establish Modern Dark global design system"
```

---

### Task 2: Setup Wizard Layout Refresh

**Files:**
- Modify: `app/src/main/res/layout/activity_setup.xml`

- [ ] **Step 1: Update `activity_setup.xml` for Modern Dark UI**
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/black"
    android:padding="24dp">

    <com.google.android.material.progressindicator.LinearProgressIndicator
        android:id="@+id/step_progress"
        android:layout_width="match_parent"
        android:layout_height="6dp"
        android:layout_marginTop="16dp"
        app:trackColor="@color/dark_gray"
        app:indicatorColor="@color/blue_vibrant"
        app:trackCornerRadius="3dp" />

    <LinearLayout
        android:id="@+id/step_container"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:orientation="vertical"
        android:gravity="center"
        android:padding="16dp">
        <!-- Dynamic content: icon, title, description will be added/updated here -->
    </LinearLayout>

    <Button
        android:id="@+id/btn_next"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Next"
        android:backgroundTint="@color/blue_vibrant"
        app:cornerRadius="16dp"
        android:padding="16dp"
        android:textColor="@color/white"
        android:textAllCaps="false"
        android:textSize="17sp" />

    <Button
        android:id="@+id/btn_skip"
        style="@style/Widget.MaterialComponents.Button.TextButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Skip for now"
        android:textColor="@color/blue_vibrant"
        android:layout_marginTop="8dp" />

</LinearLayout>
```

- [ ] **Step 2: Commit Layout Update**
```bash
git add app/src/main/res/layout/activity_setup.xml
git commit -m "feat: update SetupActivity layout to Modern Dark"
```

---

### Task 3: Setup Wizard Logic Implementation

**Files:**
- Modify: `app/src/main/kotlin/com/example/forcethernet/SetupActivity.kt`

- [ ] **Step 1: Remove temporary skip logic and fix `onCreate`**
Remove the `prefs.edit().putBoolean("setup_complete", true).apply()` and the immediate `finish()` from `onCreate`.

- [ ] **Step 2: Implement `updateUI` with Modern Dark styles**
```kotlin
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
        setTextColor(ContextCompat.getColor(context, R.color.muted_gray))
    }

    when (currentStep) {
        SetupStep.WELCOME -> {
            iconView.text = "👋"
            titleView.text = "Welcome to Force Ethernet"
            descriptionView.text = "Keep Ethernet Tethering enabled automatically when a cable is connected."
            binding.btnSkip.visibility = View.GONE
            binding.btnNext.text = "Get Started"
        }
        SetupStep.NOTIFICATIONS -> {
            iconView.text = "🔔"
            titleView.text = "Stay Informed"
            descriptionView.text = "Get alerts when tethering starts or stops."
            binding.btnSkip.visibility = View.VISIBLE
            binding.btnNext.text = "Grant Permission"
        }
        SetupStep.ACCESSIBILITY -> {
            iconView.text = "♿"
            titleView.text = "Accessibility Service"
            descriptionView.text = "Needed to automatically toggle tethering settings.\n\nFind 'Force Ethernet' and turn it ON."
            binding.btnSkip.visibility = View.GONE
            binding.btnNext.text = "Open Settings"
        }
        SetupStep.BATTERY -> {
            iconView.text = "🔋"
            titleView.text = "Battery Optimization"
            descriptionView.text = "Exclude app from optimization for stable background monitoring."
            binding.btnSkip.visibility = View.VISIBLE
            binding.btnNext.text = "Ignore Optimization"
        }
        SetupStep.COMPLETE -> {
            iconView.text = "✅"
            titleView.text = "You're all set!"
            descriptionView.text = "Tethering will now stay enabled whenever you connect an Ethernet cable."
            binding.btnSkip.visibility = View.GONE
            binding.btnNext.text = "Finish Setup"
        }
    }

    binding.stepContainer.addView(iconView)
    binding.stepContainer.addView(titleView)
    binding.stepContainer.addView(descriptionView)
}
```

- [ ] **Step 3: Update `advanceStep` to handle `SetupStep.entries` correctly**
Ensure it doesn't finish setup until `SetupStep.COMPLETE` is reached and the "Finish" button is clicked.

- [ ] **Step 4: Commit Logic Changes**
```bash
git add app/src/main/kotlin/com/example/forcethernet/SetupActivity.kt
git commit -m "feat: complete SetupActivity wizard logic and styling"
```

---

### Task 4: MainActivity Refresh

**Files:**
- Modify: `app/src/main/res/layout/activity_main.xml`

- [ ] **Step 1: Refactor `activity_main.xml` to match Design System**
```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/black"
    android:padding="24dp">

    <com.google.android.material.card.MaterialCardView
        android:id="@+id/status_card"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        app:cardBackgroundColor="@color/dark_gray"
        app:cardCornerRadius="24dp"
        app:cardElevation="0dp">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="32dp"
            android:gravity="center">

            <TextView
                android:id="@+id/status_label"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Status"
                android:textColor="@color/muted_gray"
                android:textSize="14sp"
                android:textAllCaps="true"
                android:letterSpacing="0.1" />

            <TextView
                android:id="@+id/status_text"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="DISABLED"
                android:textColor="@color/white"
                android:textSize="32sp"
                android:textStyle="bold"
                android:layout_marginTop="8dp" />

            <com.google.android.material.switchmaterial.SwitchMaterial
                android:id="@+id/master_toggle"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="32dp"
                app:thumbTint="@color/blue_vibrant"
                app:trackTint="@color/muted_gray" />

        </LinearLayout>
    </com.google.android.material.card.MaterialCardView>

</RelativeLayout>
```

- [ ] **Step 2: Commit MainActivity Refresh**
```bash
git add app/src/main/res/layout/activity_main.xml
git commit -m "style: refresh MainActivity UI to match Modern Dark theme"
```

---

### Task 5: Final Validation

- [ ] **Step 1: Build and Run**
- [ ] **Step 2: Verify Setup Wizard flow (from Welcome to Completion)**
- [ ] **Step 3: Verify MainActivity styling matches wizard styling**
- [ ] **Step 4: Confirm permissions are correctly detected and auto-advance works**
