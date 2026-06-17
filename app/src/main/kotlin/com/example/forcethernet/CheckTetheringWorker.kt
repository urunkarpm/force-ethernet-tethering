package com.example.forcethernet

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.provider.Settings
import androidx.work.*
import java.util.concurrent.TimeUnit

class CheckTetheringWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        // 1. Check if Ethernet tethering is ALREADY active
        if (TetheringUtils.isEthernetTetheringActive(connectivityManager)) {
            return Result.success()
        }

        // 2. Check if Ethernet is plugged in (either as a network or tetherable interface)
        if (TetheringUtils.isEthernetPluggedIn(connectivityManager)) {
            triggerAutomation()
        }
        
        return Result.success()
    }

    private fun triggerAutomation() {
        TetheringAccessibilityService.isTaskPending = true
        
        val intent = Intent("android.settings.TETHER_SETTINGS").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        applicationContext.startActivity(intent)
    }

    companion object {
        fun enqueue(context: Context) {
            val request = PeriodicWorkRequestBuilder<CheckTetheringWorker>(15, TimeUnit.MINUTES) // 15 min is min for Periodic
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "check_tether",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
