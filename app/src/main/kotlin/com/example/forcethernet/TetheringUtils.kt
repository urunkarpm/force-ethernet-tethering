package com.example.forcethernet

import android.net.ConnectivityManager
import java.net.NetworkInterface

object TetheringUtils {
    fun isEthernetTetheringActive(cm: ConnectivityManager): Boolean {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            if (interfaces != null) {
                for (intf in interfaces) {
                    val name = intf.name.lowercase()
                    // Check if interface is up and has an IP address
                    if (intf.isUp && intf.inetAddresses.hasMoreElements()) {
                        // Look for common tethering interface names
                        if (name.contains("rndis") || name.contains("usb") || name.contains("tether")) {
                            android.util.Log.d("ForceEthernet", "Active tethering interface detected: ${intf.name}")
                            return true
                        }
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ForceEthernet", "Error scanning interfaces for tethering", e)
        }
        return false
    }

    fun isEthernetPluggedIn(cm: ConnectivityManager): Boolean {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            if (interfaces != null) {
                for (intf in interfaces) {
                    android.util.Log.d("ForceEthernet", "Detected interface: name=${intf.name}, isUp=${intf.isUp}, isLoopback=${intf.isLoopback}, isVirtual=${intf.isVirtual}")
                    // USB ethernet adapters typically show up as "eth0", "eth1", etc.
                    // Also check for "rndis" or "usb" which some manufacturers use
                    if (intf.isUp && !intf.isLoopback && !intf.isVirtual) {
                        if (intf.name.startsWith("eth") || intf.name.startsWith("rndis") || intf.name.startsWith("usb")) {
                            android.util.Log.d("ForceEthernet", "Matched Ethernet/USB interface: ${intf.name}")
                            return true
                        }
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ForceEthernet", "Error checking interfaces", e)
        }
        return false
    }
}
