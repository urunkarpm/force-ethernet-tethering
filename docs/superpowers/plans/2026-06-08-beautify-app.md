# Ethernet Auto-Toggle Beautification Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Improve app aesthetics with a "Clean & Minimalist" style using Material 3 principles, cards, and better spacing.

**Architecture:** Use MaterialCardView for layout grouping, update theme with modern neutral colors, and increase padding/margins for better visual hierarchy.

**Tech Stack:** Android XML Layouts, Material Components (M3), Kotlin.

---

### Task 1: Define Design Tokens (Colors & Dimens)

**Files:**
- Create: `app/src/main/res/values/colors.xml`
- Create: `app/src/main/res/values/dimens.xml`

- [ ] **Step 1: Create colors.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="primary">#2196F3</color>
    <color name="primary_variant">#1976D2</color>
    <color name="on_primary">#FFFFFF</color>
    <color name="secondary">#4CAF50</color>
    <color name="secondary_variant">#388E3C</color>
    <color name="on_secondary">#FFFFFF</color>
    <color name="background">#FAFAFA</color>
    <color name="surface">#FFFFFF</color>
    <color name="on_surface">#212121</color>
    <color name="on_surface_variant">#757575</color>
    <color name="error">#B00020</color>
    <color name="on_error">#FFFFFF</color>
    <color name="status_enabled">#4CAF50</color>
    <color name="status_disabled">#757575</color>
</resources>
```

- [ ] **Step 2: Create dimens.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <dimen name="screen_padding">24dp</dimen>
    <dimen name="card_padding">24dp</dimen>
    <dimen name="card_corner_radius">16dp</dimen>
    <dimen name="card_elevation">4dp</dimen>
    <dimen name="spacing_small">8dp</dimen>
    <dimen name="spacing_medium">16dp</dimen>
    <dimen name="spacing_large">32dp</dimen>
    <dimen name="title_size">24sp</dimen>
    <dimen name="body_size">14sp</dimen>
</resources>
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/res/values/colors.xml app/src/main/res/values/dimens.xml
git commit -m "style: define basic color and dimension tokens"
```

### Task 2: Update App Theme

**Files:**
- Modify: `app/src/main/res/values/themes.xml`

- [ ] **Step 1: Update Theme.ForceEthernet**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.ForceEthernet" parent="Theme.MaterialComponents.DayNight.NoActionBar">
        <!-- Primary brand color. -->
        <item name="colorPrimary">@color/primary</item>
        <item name="colorPrimaryVariant">@color/primary_variant</item>
        <item name="colorOnPrimary">@color/on_primary</item>
        <!-- Secondary brand color. -->
        <item name="colorSecondary">@color/secondary</item>
        <item name="colorSecondaryVariant">@color/secondary_variant</item>
        <item name="colorOnSecondary">@color/on_secondary</item>
        <!-- Status bar color. -->
        <item name="android:statusBarColor">?attr/colorPrimaryVariant</item>
        <!-- Background and Surface -->
        <item name="android:windowBackground">@color/background</item>
        <item name="colorSurface">@color/surface</item>
        <item name="colorOnSurface">@color/on_surface</item>
    </style>
</resources>
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/res/values/themes.xml
git commit -m "style: update theme to use custom colors and material 3 defaults"
```

### Task 3: Redesign Main Screen Layout

**Files:**
- Modify: `app/src/main/res/layout/activity_main.xml`

- [ ] **Step 1: Replace layout content**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.core.widget.NestedScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="@dimen/screen_padding"
        android:gravity="center_horizontal">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Ethernet Auto-Toggle"
            android:textSize="@dimen/title_size"
            android:fontFamily="sans-serif-light"
            android:textColor="@color/on_surface"
            android:layout_marginTop="@dimen/spacing_large"
            android:layout_marginBottom="@dimen/spacing_large" />

        <com.google.android.material.card.MaterialCardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_maxWidth="400dp"
            app:cardCornerRadius="@dimen/card_corner_radius"
            app:cardElevation="@dimen/card_elevation"
            app:strokeWidth="1dp"
            app:strokeColor="#f0f0f0">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="@dimen/card_padding"
                android:gravity="center_horizontal">

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="SERVICE STATUS"
                    android:textSize="12sp"
                    android:letterSpacing="0.1"
                    android:textColor="@color/on_surface_variant" />

                <TextView
                    android:id="@+id/status_text"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="DISABLED"
                    android:textSize="20sp"
                    android:textStyle="bold"
                    android:textColor="@color/status_disabled"
                    android:layout_marginBottom="24dp" />

                <View
                    android:layout_width="match_parent"
                    android:layout_height="1dp"
                    android:background="#f9f9f9"
                    android:layout_marginBottom="24dp" />

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal"
                    android:gravity="center_vertical">

                    <TextView
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:text="Auto-Tethering"
                        android:textSize="16sp"
                        android:textColor="@color/on_surface" />

                    <com.google.android.material.switchmaterial.SwitchMaterial
                        android:id="@+id/master_toggle"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content" />
                </LinearLayout>
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:layout_marginTop="@dimen/spacing_large"
            android:layout_maxWidth="400dp">

            <TextView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="How it works"
                android:textSize="16sp"
                android:textStyle="bold"
                android:textColor="@color/on_surface"
                android:layout_marginBottom="@dimen/spacing_medium" />

            <TextView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="1. Enable the master toggle above.\n2. Connect an Ethernet cable to your device.\n3. The app will automatically enable Ethernet Tethering for you."
                android:textSize="@dimen/body_size"
                android:lineSpacingExtra="6dp"
                android:textColor="@color/on_surface_variant" />
        </LinearLayout>

    </LinearLayout>
</androidx.core.widget.NestedScrollView>
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/res/layout/activity_main.xml
git commit -m "feat: redesign main activity layout with centered card and better hierarchy"
```

### Task 4: Update MainActivity.kt for UI States

**Files:**
- Modify: `app/src/main/kotlin/com/example/forcethernet/MainActivity.kt`

- [ ] **Step 1: Update status text color dynamically**

```kotlin
// In updateUI() or similar method in MainActivity.kt
val statusText = findViewById<TextView>(R.id.status_text)
if (isEnabled) {
    statusText.text = "ENABLED"
    statusText.setTextColor(ContextCompat.getColor(this, R.color.status_enabled))
} else {
    statusText.text = "DISABLED"
    statusText.setTextColor(ContextCompat.getColor(this, R.color.status_disabled))
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/kotlin/com/example/forcethernet/MainActivity.kt
git commit -m "feat: update MainActivity to handle dynamic UI state colors"
```

### Task 5: Redesign Setup Screen Layout

**Files:**
- Modify: `app/src/main/res/layout/activity_setup.xml`

- [ ] **Step 1: Replace layout content**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="@dimen/screen_padding">

    <TextView
        android:id="@+id/step_indicator"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Step 1 of 4"
        android:textSize="14sp"
        android:textColor="@color/on_surface_variant"
        android:alpha="0.8"
        android:layout_marginBottom="@dimen/spacing_medium" />

    <FrameLayout
        android:id="@+id/step_container"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="end"
        android:layout_marginTop="@dimen/spacing_medium">

        <Button
            android:id="@+id/btn_skip"
            style="@style/Widget.MaterialComponents.Button.TextButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Skip"
            android:visibility="gone"
            android:layout_marginEnd="@dimen/spacing_small" />

        <Button
            android:id="@+id/btn_next"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Next"
            app:cornerRadius="8dp" />
    </LinearLayout>
</LinearLayout>
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/res/layout/activity_setup.xml
git commit -m "style: modernize setup activity layout"
```
