package com.example.forcethernet

import android.net.ConnectivityManager
import java.net.NetworkInterface

object TetheringUtils {
    fun isEthernetTetheringActive(cm: ConnectivityManager): Boolean {
        // Because reflection on ConnectivityManager is blocked on newer Android versions,
        // we return false here so the accessibility service always opens and checks 
        // the actual UI toggle state. The accessibility service will back out 
        // safely if it's already enabled.
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
