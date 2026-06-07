package com.example.forcethernet

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import androidx.work.*
import java.util.concurrent.TimeUnit

class CheckTetheringWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        
        val isEthernetConnected = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true
        
        if (isEthernetConnected) {
            // Trigger the automation
            val intent = Intent(Settings.ACTION_TETHER_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            applicationContext.startActivity(intent)
            
            val serviceIntent = Intent(applicationContext, TetheringAccessibilityService::class.java).apply {
                action = "ACTION_ENABLE_TETHERING"
            }
            applicationContext.startService(serviceIntent)
        }
        
        return Result.success()
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
