# Pro Reliability Package Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Enhance tethering automation with smart state detection, anti-spam cooldowns, and interactive notification controls.

**Architecture:**
- `TetheringUtils`: Updated to scan network interfaces for active tethering (rndis, usb).
- `EthernetMonitorService`: Implements cooldown logic and handles broadcast intents for "Silence" and "Enable Now".
- `Notification`: Updated with action buttons linked to PendingIntents.

**Tech Stack:** Kotlin, Android SDK, BroadcastReceiver, Coroutines.

---

### Task 1: Smart Tethering Detection

**Files:**
- Modify: `app/src/main/kotlin/com/example/forcethernet/TetheringUtils.kt`

- [ ] **Step 1: Implement `isEthernetTetheringActive` interface scanning**
```kotlin
    fun isEthernetTetheringActive(cm: ConnectivityManager): Boolean {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            if (interfaces != null) {
                for (intf in interfaces) {
                    // Check for common tethering interface names
                    val name = intf.name.lowercase()
                    if (name.contains("rndis") || name.contains("usb") || name.contains("tether")) {
                        // If interface is Up and has an assigned address (not loopback)
                        if (intf.isUp && intf.inetAddresses.hasMoreElements()) {
                            android.util.Log.d("ForceEthernet", "Active tethering interface found: ${intf.name}")
                            return true
                        }
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ForceEthernet", "Error scanning tethering interfaces", e)
        }
        return false
    }
```

- [ ] **Step 2: Commit Detection Logic**
```bash
git add app/src/main/kotlin/com/example/forcethernet/TetheringUtils.kt
git commit -m "feat: implement smart tethering state detection"
```

---

### Task 2: Cooldown and Silence Logic

**Files:**
- Modify: `app/src/main/kotlin/com/example/forcethernet/EthernetMonitorService.kt`

- [ ] **Step 1: Add state variables for cooldown and silence**
Add `private var lastTriggerTime = 0L`, `private var silenceUntil = 0L` to `EthernetMonitorService`.

- [ ] **Step 2: Update `startPolling` with logic gates**
```kotlin
    private fun startPolling() {
        serviceScope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                val isPluggedIn = TetheringUtils.isEthernetPluggedIn(connectivityManager)
                val isTetheringActive = TetheringUtils.isEthernetTetheringActive(connectivityManager)

                val inSilence = now < silenceUntil
                val inCooldown = now - lastTriggerTime < 30000 // 30s cooldown

                if (isPluggedIn && !isTetheringActive && !inSilence && !inCooldown) {
                    lastTriggerTime = now
                    withContext(Dispatchers.Main) {
                        triggerTethering()
                    }
                }
                delay(3000)
            }
        }
    }
```

- [ ] **Step 3: Commit Cooldown Logic**
```bash
git add app/src/main/kotlin/com/example/forcethernet/EthernetMonitorService.kt
git commit -m "feat: add anti-spam cooldown and silence period"
```

---

### Task 3: Interactive Notification Buttons

**Files:**
- Modify: `app/src/main/kotlin/com/example/forcethernet/EthernetMonitorService.kt`
- Modify: `app/src/main/res/values/strings.xml`

- [ ] **Step 1: Add strings for notification actions**
Add `action_enable_now`, `action_dismiss_silence` to `strings.xml`.

- [ ] **Step 2: Implement BroadcastReceiver in Service**
Add a nested or anonymous `BroadcastReceiver` to handle "ACTION_SILENCE" and "ACTION_ENABLE_NOW" intents.

- [ ] **Step 3: Update `createNotification` with Actions**
Add `addAction` to `NotificationCompat.Builder` for both intents.

- [ ] **Step 4: Commit Notification Improvements**
```bash
git add app/src/main/kotlin/com/example/forcethernet/ app/src/main/res/values/strings.xml
git commit -m "feat: add interactive notification buttons"
```

---

### Task 4: Final Validation

- [ ] **Step 1: Verify Smart Detection** (App shouldn't open settings if tethering already on)
- [ ] **Step 2: Verify Cooldown** (App shouldn't reopen settings for 30s after a trigger)
- [ ] **Step 3: Verify Silence Action** (Dismiss button silences app for 5 mins)
- [ ] **Step 4: Build check**
```bash
./gradlew assembleDebug
```
