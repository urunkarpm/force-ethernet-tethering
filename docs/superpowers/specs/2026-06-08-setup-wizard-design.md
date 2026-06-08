# Design Spec: Setup Wizard

## 1. Overview
The Setup Wizard provides a guided first-run experience to ensure all necessary permissions and configurations are set for the app to function reliably. It replaces the basic permission check in `MainActivity` with a structured, multi-step flow.

## 2. Component: SetupActivity
A new activity that manages the setup lifecycle.

### A. States (Steps)
1.  **Welcome**: Introduction and value proposition.
2.  **Notification Permission**: Requests `POST_NOTIFICATIONS` (Android 13+).
3.  **Accessibility Service**: Guides the user to enable the `TetheringAccessibilityService`.
4.  **Battery Optimization**: Guides the user to whitelist the app from battery restrictions.
5.  **Completion**: Final confirmation and transition to `MainActivity`.

### B. Implementation Details
-   **Navigation**: Uses a `ViewPager2` or a simple `Fragment` container to swap between steps.
-   **State Persistence**: Tracks which steps are completed in `SharedPreferences`.
-   **Automatic Skip**: If the activity is launched and certain permissions are already granted, it can skip those steps or show them as "Done".
-   **Routing**:
    -   `MainActivity` checks on `onCreate` if setup is complete.
    -   If not complete, it launches `SetupActivity`.
    -   `SetupActivity` is excluded from the back stack once completion is reached.

## 3. UI/UX Flow
-   **Progress Indicator**: A simple step counter (e.g., "Step 2 of 4").
-   **Visual Cues**: Clear, large icons and simple instructions for each system-level settings screen.
-   **Action Buttons**: Primary action (e.g., "Grant Permission") and a secondary action where applicable (e.g., "Skip" for optional steps like Battery Optimization).

## 4. Technical Logic
-   **Accessibility Check**: Uses existing `isAccessibilityServiceEnabled()` logic.
-   **Battery Optimization Check**: Uses `PowerManager.isIgnoringBatteryOptimizations()`.
-   **Intent Triggers**:
    -   Notifications: `ActivityResultContracts.RequestPermission()`.
    -   Accessibility: `Settings.ACTION_ACCESSIBILITY_SETTINGS`.
    -   Battery: `Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`.

## 5. Success Criteria
-   User completes all required steps.
-   App can reliably monitor Ethernet and trigger tethering without system interference.
-   Setup is not shown again unless a critical permission (Accessibility) is revoked.
