# Update MainActivity UI States Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Update MainActivity.kt to dynamically update the status text and color based on the master toggle state.

**Architecture:** Use ViewBinding to access UI elements and a helper function to consolidate UI update logic.

**Tech Stack:** Kotlin, Android ViewBinding, Material Components.

---

### Task 1: Update MainActivity.kt for UI States

**Files:**
- Modify: `app/src/main/kotlin/com/example/forcethernet/MainActivity.kt`

- [ ] **Step 1: Add updateStatusUI function**

Add the following function to `MainActivity` class:

```kotlin
    private fun updateStatusUI(isEnabled: Boolean) {
        if (isEnabled) {
            binding.statusText.text = "ENABLED"
            binding.statusText.setTextColor(ContextCompat.getColor(this, R.color.status_enabled))
        } else {
            binding.statusText.text = "DISABLED"
            binding.statusText.setTextColor(ContextCompat.getColor(this, R.color.status_disabled))
        }
    }
```

- [ ] **Step 2: Call updateStatusUI in onCreate**

Call `updateStatusUI(isEnabled)` after initializing `isEnabled`.

```kotlin
        val isEnabled = prefs.getBoolean("master_enabled", false)
        binding.masterToggle.isChecked = isEnabled
        updateStatusUI(isEnabled) // Add this
```

- [ ] **Step 3: Call updateStatusUI in masterToggle listener**

Call `updateStatusUI(isChecked)` inside the `setOnCheckedChangeListener`.

```kotlin
        binding.masterToggle.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("master_enabled", isChecked).apply()
            updateStatusUI(isChecked) // Add this
            if (isChecked) {
                startMonitoring()
            } else {
                stopMonitoring()
            }
        }
```

- [ ] **Step 4: Verify Compilation**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
git add app/src/main/kotlin/com/example/forcethernet/MainActivity.kt
git commit -m "feat: update MainActivity to handle dynamic UI state colors"
```
