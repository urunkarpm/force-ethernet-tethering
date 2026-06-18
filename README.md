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


### Application

This solution is ideal if you have a spare 5G smartphone with a compatible SIM card and an unlimited data plan. Simply recharge your data plan as needed and connect the spare phone to a USB Ethernet adapter (dongle).

Once configured, there's no need to repeatedly navigate through settings to enable tethering or related services. The service continuously monitors USB connections and automatically activates when the phone is connected. If you need to use the phone temporarily, simply disconnect it. When you reconnect it later, the service will automatically detect the connection and restore functionality without requiring any additional setup, saving you the 1–2 minutes typically spent re-enabling the feature each time.

This provides a convenient, low-maintenance, and cost-effective way to maintain a reliable 5G internet connection using a spare smartphone.


### Getting started for Indian Users

How to Set Up a Portable 5G Wi-Fi Station Using Your Phone
What You'll Need
A 5G smartphone with an active 5G data plan
A [USB-C Ethernet dongle](https://www.amazon.in/Portronics-Ethernet-Internet-Compatible-Windows/dp/B0F7B1JK9T?crid=2G10J4LKE9LZR&dib=eyJ2IjoiMSJ9.3zbsp6Rn74sL8DwuDQRwPB8C52bJirTJnDEmzPcSVkFGTqGG7U2y-dA47GVyxFxpx4L1Dp4QBXq1TyuRJ88ykNrqjWBf5aH2Igw2H_7TBv2OS3xOhfHuclAwtN2-D3vMzDJkUhBsl5k-UUFH1YzELAP8JI4TX1aY3QENZMM-Hln44Gtoz9w6yUUkF7NiQy4nlzayksLFb1bDdzaIiz0jsY0YbMe5nCb_R7Soaj2fMbo.A7TizIFO2ukF6KC_rxey9YJ8ytnwH97ctWqnAjEIoUg&dib_tag=se&keywords=ethernet+type+c+adapter&qid=1781760360&sprefix=ethernet+type+c+%2Caps%2C269&sr=8-8) with:
1 × RJ45 (Ethernet) port
1 × USB-C Power Delivery (charging) port
A Wi-Fi router
An Ethernet (LAN) cable
A USB-C charger
(Optional) A small enclosure or box to make the setup portable
Step 1: Connect the Ethernet Cable
Plug one end of the LAN cable into the RJ45 port on the USB-C dongle.
Plug the other end into the WAN/Internet port of your Wi-Fi router.
Step 2: Connect the Charger
Connect your USB-C charger to the USB-C Power Delivery port on the dongle.
This keeps your phone charged while it provides internet connectivity.
Step 3: Connect Your Phone
Connect your 5G smartphone to the USB-C dongle.
Install the app, give permissions
Your phone will automatically detect the Ethernet adapter and begin sharing its mobile data connection 
Step 4: Prepare the Router
Reset the router to its factory settings (recommended for first-time setup).
Complete the basic setup by changing:
Wi-Fi network name (SSID)
Wi-Fi password
No special WAN or internet settings are required—the router should automatically obtain an internet connection from the phone via Ethernet.
Step 5: Power Everything On
Ensure:
The charger is powered.
The phone is connected to the dongle.
The router is turned on.
Within a few moments, the router should detect the internet connection automatically.
Step 6: Connect Your Devices
Connect your laptops, TVs, gaming consoles, or other devices to the router using Wi-Fi or Ethernet.
They will now access the internet through your phone's 5G connection.
Optional: Build a Portable Wi-Fi Station

For a clean and travel-friendly setup, place the following components inside a compact enclosure:

Wi-Fi router
USB-C Ethernet dongle
Charger
Cables

This creates a portable Wi-Fi station that can be moved anywhere with power and 5G coverage. If you ever need to use your phone separately, simply unplug it from the dongle. When you reconnect it later, the setup resumes automatically—there's no need to reconfigure the router or repeat the setup process.
