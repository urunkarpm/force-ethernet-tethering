# Force Ethernet Tethering

Force Ethernet Tethering is a specialized Android utility designed to automate and maintain Ethernet tethering connectivity. 

### What it does

The primary goal of this application is to ensure that Ethernet tethering remains active whenever a compatible USB Ethernet adapter is connected to the device. It removes the need for manual intervention by automatically toggling the system's tethering settings.

### Key Features

*   **Automatic Detection:** Actively monitors the device for the connection of USB Ethernet adapters.
*   **Persistent Connectivity:** Automatically enables Ethernet tethering as soon as a valid connection is detected.
*   **Seamless Monitoring:** Runs a background service to ensure that tethering is re-enabled if it is unexpectedly disabled or if the cable is re-plugged.
*   **Reliability Focus:** Includes smart detection logic to differentiate between standard Ethernet internet and tethering-capable interfaces.
*   **User Notifications:** Provides real-time status updates via persistent notifications, allowing users to see the current tethering state at a glance.

### How it works

The app utilizes a foreground service combined with network callbacks and accessibility services to navigate system settings and enforce the tethering state. By observing network interface changes, it can react instantly to hardware events, ensuring your shared connection is always ready.
