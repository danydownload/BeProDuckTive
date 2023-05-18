package com.example.beproducktive.ui.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder

class TimerService : Service() {

        override fun onBind(intent: Intent): IBinder {
            TODO("Not yet implemented")
        }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra("inputExtra")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val notification = createNotification()

        startForeground(NOTIFICATION_ID, notification)

        // Return START_STICKY to ensure the service is restarted if it's killed by the system
        return START_STICKY
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val channel = NotificationChannel( CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun createNotification() : Notification {
        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Timer Service")
            .setContentText("Timer is running...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "TimerServiceChannel"
        private const val CHANNEL_NAME = "Timer Service Channel"
        private const val NOTIFICATION_ID = 1
        private const val TIMER_DURATION = 60000L
        private const val TIMER_INTERVAL = 1000L
    }
}