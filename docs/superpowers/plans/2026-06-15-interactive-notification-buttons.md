# Interactive Notification Buttons Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add "ENABLE NOW" and "DISMISS (5 MINS)" buttons to the persistent monitor notification.

**Architecture:** Use `PendingIntent.getService` with custom actions. Handle actions in `onStartCommand` of `EthernetMonitorService`.

**Tech Stack:** Kotlin, Android Notifications.

---

### Task 1: Add Action Strings

**Files:**
- Modify: `app/src/main/res/values/strings.xml`

- [ ] **Step 1: Add action strings**

```xml
    <string name="action_enable_now">ENABLE NOW</string>
    <string name="action_dismiss_silence">DISMISS (5 MINS)</string>
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/res/values/strings.xml
git commit -m "feat: add notification action strings"
```

---

### Task 2: Define Actions and Handle in onStartCommand

**Files:**
- Modify: `app/src/main/kotlin/com/example/forcethernet/EthernetMonitorService.kt`

- [ ] **Step 1: Define action constants**

Add to `companion object`:
```kotlin
        const val ACTION_SILENCE = "com.example.forcethernet.ACTION_SILENCE"
        const val ACTION_ENABLE_NOW = "com.example.forcethernet.ACTION_ENABLE_NOW"
```

- [ ] **Step 2: Implement onStartCommand**

```kotlin
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SILENCE -> {
                android.util.Log.d("ForceEthernet", "Action: Silence for 5 mins")
                silenceUntil.set(System.currentTimeMillis() + 5 * 60 * 1000)
                updateNotification()
            }
            ACTION_ENABLE_NOW -> {
                android.util.Log.d("ForceEthernet", "Action: Enable Now")
                silenceUntil.set(0)
                lastTriggerTime.set(0)
                triggerTethering()
            }
        }
        return START_STICKY
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, createNotification())
    }
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/kotlin/com/example/forcethernet/EthernetMonitorService.kt
git commit -m "feat: handle notification actions in EthernetMonitorService"
```

---

### Task 3: Update createNotification with Actions

**Files:**
- Modify: `app/src/main/kotlin/com/example/forcethernet/EthernetMonitorService.kt`

- [ ] **Step 1: Update createNotification**

```kotlin
    private fun createNotification(): Notification {
        val now = System.currentTimeMillis()
        val silenceTime = silenceUntil.get()
        val isSilenced = now < silenceTime
        
        val contentText = if (isSilenced) {
            val remainingMins = ((silenceTime - now) / 60000) + 1
            "Monitoring SILENCED ($remainingMins mins left)"
        } else {
            "Watching for cable connection..."
        }

        val silenceIntent = Intent(this, EthernetMonitorService::class.java).apply {
            action = ACTION_SILENCE
        }
        val silencePendingIntent = PendingIntent.getService(
            this, 0, silenceIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val enableIntent = Intent(this, EthernetMonitorService::class.java).apply {
            action = ACTION_ENABLE_NOW
        }
        val enablePendingIntent = PendingIntent.getService(
            this, 1, enableIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, "monitor_channel")
            .setContentTitle("Ethernet Monitoring Active")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, getString(R.string.action_dismiss_silence), silencePendingIntent)
            .addAction(android.R.drawable.ic_media_play, getString(R.string.action_enable_now), enablePendingIntent)

        return builder.build()
    }
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/kotlin/com/example/forcethernet/EthernetMonitorService.kt
git commit -m "feat: add interactive buttons to monitor notification"
```
