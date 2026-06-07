# Design Spec: Force Ethernet Tethering

## 1. Overview
A specialized utility app for Android that automatically enables "Ethernet Tethering" whenever an Ethernet cable is detected. It uses an Accessibility Service to overcome system-level API restrictions on non-rooted devices.

## 2. Architecture & Components

### A. UI (MainActivity)
- **Primary Switch**: Enables/Disables the background monitoring service.
- **Permission Check**: Displays the status of "Accessibility Service" permission. If not granted, provides a button to open the System Accessibility Settings.
- **State Persistence**: Uses `SharedPreferences` to save whether the auto-toggling feature is enabled.

### B. Ethernet Detection Service (Foreground Service)
- **Real-time Monitoring**: Uses `ConnectivityManager.NetworkCallback` to detect `TRANSPORT_ETHERNET` events instantly.
- **Periodic Check**: Uses `WorkManager` to trigger a scan every 10 minutes (per user requirement) to ensure tethering is active if a cable is present.
- **Action Trigger**: If Ethernet is connected and the app's master toggle is ON, it sends a command to the `TetheringAccessibilityService`.

### C. Tethering Accessibility Service
- **Role**: Performs the UI automation.
- **Workflow**:
    1. Receives a "Toggle" request.
    2. Launches `Settings.ACTION_TETHER_SETTINGS`.
    3. Finds the "Ethernet tethering" node by text.
    4. If the switch is currently OFF, it performs a `CLICK` action on the switch or its parent container.
    5. Briefly waits, then performs a global `BACK` action to return the user to their previous app.

## 3. Data Flow
1. User enables master toggle in App.
2. Background Service starts.
3. Cable plugged in -> `ConnectivityManager` fires callback.
4. App checks: Is Tethering already ON? (using hidden system properties or checking interface state).
5. If OFF -> Launch Accessibility Macro.
6. Settings open -> Macro clicks -> Settings close.

## 4. Permissions Required
- `android.permission.ACCESS_NETWORK_STATE`: To detect Ethernet connection.
- `android.permission.FOREGROUND_SERVICE`: To keep the detector running in the background.
- `android.permission.BIND_ACCESSIBILITY_SERVICE`: To automate the UI toggle.
- `android.permission.POST_NOTIFICATIONS`: (Android 13+) To show the required foreground service notification.

## 5. Constraints & Error Handling
- **Grayed Out Options**: If the "Ethernet tethering" option is grayed out in settings (e.g., driver issues), the Macro will time out after 5 seconds and close.
- **Device Reboot**: A `BootReceiver` will restart the monitoring service if the master toggle was ON.
- **UI Variations**: The search logic will be flexible to look for "Ethernet tethering" across different OEM versions (Samsung, Google, etc.).
