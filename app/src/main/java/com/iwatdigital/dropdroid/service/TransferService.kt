package com.iwatdigital.dropdroid.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.iwatdigital.dropdroid.R

class TransferService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification("Preparing transfer…")
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(content: String): Notification {
        val channelId = ensureChannel()
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("DropDroid")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }

    private fun ensureChannel(): String {
        val channelId = "dropdroid.transfer"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Transfers",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
        return channelId
    }

    companion object {
        const val NOTIFICATION_ID = 42
    }
}
