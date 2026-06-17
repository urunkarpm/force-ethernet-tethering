# MainActivity Refresh Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Update the MainActivity UI and styling to match the "Modern Dark" Design System.

**Architecture:** Replace the current layout with a centered MaterialCardView on a black background, following the specific styling requirements for the "Modern Dark" theme.

**Tech Stack:** Android XML Layouts, Material Components for Android, Kotlin.

---

### Task 1: Refactor activity_main.xml

**Files:**
- Modify: `app/src/main/res/layout/activity_main.xml`

- [ ] **Step 1: Replace layout content**

Replace the entire content of `app/src/main/res/layout/activity_main.xml` with the new Modern Dark design.

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/black">

    <com.google.android.material.card.MaterialCardView
        android:id="@+id/status_card"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_margin="24dp"
        app:cardBackgroundColor="@color/dark_gray"
        app:cardCornerRadius="24dp"
        app:cardElevation="8dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:strokeWidth="0dp">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center_horizontal"
            android:orientation="vertical"
            android:padding="48dp">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:fontFamily="sans-serif-medium"
                android:letterSpacing="0.1"
                android:text="STATUS"
                android:textColor="@color/muted_gray"
                android:textSize="12sp" />

            <TextView
                android:id="@+id/status_text"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="8dp"
                android:layout_marginBottom="32dp"
                android:fontFamily="sans-serif-black"
                android:text="DISABLED"
                android:textColor="@color/white"
                android:textSize="36sp"
                android:textStyle="bold" />

            <com.google.android.material.switchmaterial.SwitchMaterial
                android:id="@+id/master_toggle"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:scaleX="1.5"
                android:scaleY="1.5"
                app:thumbTint="@color/blue_vibrant"
                app:trackTint="@color/muted_gray" />

        </LinearLayout>
    </com.google.android.material.card.MaterialCardView>

</androidx.constraintlayout.widget.ConstraintLayout>
```

### Task 2: Update MainActivity.kt logic

**Files:**
- Modify: `app/src/main/kotlin/com/example/forcethernet/MainActivity.kt`

- [ ] **Step 1: Ensure status text color remains white**

Update the `updateStatusUI` method to use `white` for the status text color, as per the "Modern Dark" design requirements.

```kotlin
    private fun updateStatusUI(isEnabled: Boolean) {
        if (isEnabled) {
            binding.statusText.text = "ENABLED"
        } else {
            binding.statusText.text = "DISABLED"
        }
        binding.statusText.setTextColor(ContextCompat.getColor(this, R.color.white))
    }
```

### Task 3: Verification and Commit

- [ ] **Step 1: Verify build**
Run: `./gradlew assembleDebug`
Expected: SUCCESS

- [ ] **Step 2: Commit changes**
Run: `git add app/src/main/res/layout/activity_main.xml app/src/main/kotlin/com/example/forcethernet/MainActivity.kt`
Run: `git commit -m "style: refresh MainActivity UI to match Modern Dark theme"`
