package com.example.forcethernet

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.*
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicLong

class EthernetMonitorService : Service() {
    private lateinit var connectivityManager: ConnectivityManager
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private val lastTriggerTime = AtomicLong(0L)

    companion object {
        val silenceUntil = AtomicLong(0L)
        const val ACTION_SILENCE = "com.example.forcethernet.ACTION_SILENCE"
        const val ACTION_ENABLE_NOW = "com.example.forcethernet.ACTION_ENABLE_NOW"
    }

    override fun onCreate() {
        super.onCreate()
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        createNotificationChannel()
        
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(1, notification)
        }
        
        registerNetworkCallback()
        startPolling()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SILENCE -> {
                android.util.Log.d("ForceEthernet", "Action: Silence for 5 mins")
                silenceUntil.set(System.currentTimeMillis() + 5 * 60 * 1000)
                updateNotification()
            }
            ACTION_ENABLE_NOW -> {
                android.util.Log.d("ForceEthernet", "Action: Enable Now")
                silenceUntil.set(0)
                lastTriggerTime.set(0)
                triggerTethering()
            }
        }
        return START_STICKY
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, createNotification())
    }

    private fun startPolling() {
        android.util.Log.d("ForceEthernet", "startPolling: Polling started")
        serviceScope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                val isPluggedIn = TetheringUtils.isEthernetPluggedIn()
                val isTetheringActive = TetheringUtils.isEthernetTetheringActive()
                
                val inSilence = now < silenceUntil.get()
                val inCooldown = now - lastTriggerTime.get() < 30000
                
                android.util.Log.d("ForceEthernet", "Polling: PluggedIn=$isPluggedIn, TetheringActive=$isTetheringActive, Silence=$inSilence, Cooldown=$inCooldown")
                
                if (isPluggedIn && !isTetheringActive && !inSilence && !inCooldown) {
                    android.util.Log.d("ForceEthernet", "Triggering tethering logic!")
                    lastTriggerTime.set(now)
                    withContext(Dispatchers.Main) {
                        triggerTethering()
                    }
                }
                delay(3000) // Poll every 3 seconds
            }
        }
    }

    private fun registerNetworkCallback() {
        val request = NetworkRequest.Builder()
            .clearCapabilities() // Remove default requirements like INTERNET
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()
        
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                val now = System.currentTimeMillis()
                val inSilence = now < silenceUntil.get()
                val inCooldown = now - lastTriggerTime.get() < 30000
                
                // Only trigger if Ethernet tethering is NOT already active and not in silence/cooldown
                if (!TetheringUtils.isEthernetTetheringActive() && !inSilence && !inCooldown) {
                    lastTriggerTime.set(now)
                    serviceScope.launch(Dispatchers.Main) {
                        triggerTethering()
                    }
                }
            }
        }
        connectivityManager.registerNetworkCallback(request, networkCallback!!)
    }

    private fun triggerTethering() {
        TetheringAccessibilityService.isTaskPending = true
        
        val intent = Intent("android.settings.TETHER_SETTINGS").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "monitor_channel",
                "Ethernet Monitor",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val now = System.currentTimeMillis()
        val silenceTime = silenceUntil.get()
        val isSilenced = now < silenceTime
        
        val contentText = if (isSilenced) {
            val remainingMins = ((silenceTime - now) / 60000) + 1
            "Monitoring SILENCED ($remainingMins mins left)"
        } else {
            "Watching for cable connection..."
        }

        val silenceIntent = Intent(this, EthernetMonitorService::class.java).apply {
            action = ACTION_SILENCE
        }
        val silencePendingIntent = PendingIntent.getService(
            this, 0, silenceIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val enableIntent = Intent(this, EthernetMonitorService::class.java).apply {
            action = ACTION_ENABLE_NOW
        }
        val enablePendingIntent = PendingIntent.getService(
            this, 1, enableIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "monitor_channel")
            .setContentTitle("Ethernet Monitoring Active")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, getString(R.string.action_dismiss_silence), silencePendingIntent)
            .addAction(android.R.drawable.ic_media_play, getString(R.string.action_enable_now), enablePendingIntent)
            .build()
    }

    override fun onDestroy() {
        serviceScope.cancel()
        networkCallback?.let {
            connectivityManager.unregisterNetworkCallback(it)
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
