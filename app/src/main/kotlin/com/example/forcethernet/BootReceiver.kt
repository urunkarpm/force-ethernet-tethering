package com.example.forcethernet

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val isEnabled = prefs.getBoolean("master_enabled", false)
            if (isEnabled) {
                val serviceIntent = Intent(context, EthernetMonitorService::class.java)
                ContextCompat.startForegroundService(context, serviceIntent)
            }
        }
    }
}
