# Design Spec: Pro Reliability Package

## 1. Overview
The Pro Reliability Package improves the core automation logic of Force Ethernet to be more intelligent, less intrusive, and more resilient to user interference or edge cases.

## 2. Core Logic Enhancements

### A. Intelligent State Detection
- **File**: `TetheringUtils.kt`
- **Change**: Implement real detection in `isEthernetTetheringActive()`.
- **Logic**: Scan `NetworkInterface.getNetworkInterfaces()`.
- **Active Markers**: If an interface named `rndis0`, `usb0`, or `usb1` is "Up" and has an assigned IP address, tethering is considered **Active**.
- **Benefit**: Prevents the app from opening the Settings screen if tethering is already enabled.

### B. Anti-Spam Cooldown
- **File**: `EthernetMonitorService.kt`
- **Variable**: `lastTriggerTime: Long`, `isCooldownActive: Boolean`.
- **Logic**: 
    1. When `triggerTethering()` is called, record the timestamp.
    2. If the user backs out or fails to enable tethering, do not re-trigger for **30 seconds**.
    3. If tethering becomes active, reset the cooldown.
- **Benefit**: Prevents the "infinite loop" of Settings screens if the user intentionally wants to keep tethering off while the cable is plugged in.

## 3. Interactive Notification
- **Channel**: `MONITORING_CHANNEL`
- **Actions**:
    1. **"ENABLE NOW"**: Manually triggers the `triggerTethering()` logic immediately, bypassing any active cooldown.
    2. **"DISMISS"**: Activates a **5-minute silence** period for the auto-trigger logic.
- **Implementation**: Uses `BroadcastReceiver` to handle intent actions from the notification buttons.

## 4. Success Criteria
- The Settings screen *only* opens when a cable is plugged in AND tethering is genuinely inactive.
- The user can manually override or silence the automation without fighting the app.
- System-level interface names (`rndis`, `usb`) are correctly mapped to tethering states.
