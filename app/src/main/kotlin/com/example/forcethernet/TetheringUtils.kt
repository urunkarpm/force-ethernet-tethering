package com.example.forcethernet

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object TetheringUtils {
    fun isEthernetTetheringActive(cm: ConnectivityManager): Boolean {
        return try {
            val method = cm.javaClass.getMethod("getTetheredIfaces")
            val tethered = method.invoke(cm) as Array<*>
            tethered.any { (it as String).contains("eth") }
        } catch (e: Exception) {
            false
        }
    }

    fun isEthernetPluggedIn(cm: ConnectivityManager): Boolean {
        // Check active/available networks for Ethernet transport
        val hasEthernetNetwork = cm.allNetworks.any { network ->
            cm.getNetworkCapabilities(network)?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true
        }
        if (hasEthernetNetwork) return true

        // Check if interface is detected by system but not yet tethered/connected
        return try {
            val method = cm.javaClass.getMethod("getTetherableIfaces")
            val tetherable = method.invoke(cm) as Array<*>
            tetherable.any { (it as String).contains("eth") }
        } catch (e: Exception) {
            false
        }
    }
}
