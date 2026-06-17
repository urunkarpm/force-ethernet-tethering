# Design Doc: Interactive Notification Buttons

Add "ENABLE NOW" and "DISMISS (5 MINS)" buttons to the persistent monitor notification.

## Goals
- Allow manual trigger of tethering logic via notification.
- Allow silencing the monitor for a short duration (5 minutes).

## Proposed Changes

### 1. Resources (`strings.xml`)
Add strings for the notification actions.

### 2. Service Logic (`EthernetMonitorService.kt`)
- Define `ACTION_SILENCE` and `ACTION_ENABLE_NOW`.
- Implement `onStartCommand` to handle these actions.
- Update `createNotification()` to:
    - Add the two actions using `PendingIntent.getService`.
    - Show silence status in the content text if silenced.

### 3. State Management
Use `AtomicLong` for `silenceUntil` (already exists but will be utilized).

## Verification Plan
1. Start the service.
2. Verify "ENABLE NOW" and "DISMISS (5 MINS)" buttons appear in the notification.
3. Click "DISMISS (5 MINS)":
    - Verify logs show silence set.
    - Verify notification text updates to show silence.
    - Verify tethering is NOT triggered for 5 minutes.
4. Click "ENABLE NOW":
    - Verify tethering logic is triggered immediately (Settings opens).
    - Verify silence is reset.
