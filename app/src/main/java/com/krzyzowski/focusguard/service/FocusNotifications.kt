package com.krzyzowski.focusguard.service

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.krzyzowski.focusguard.MainActivity
import com.krzyzowski.focusguard.R

class FocusNotifications(private val context: Context) {
    private val manager = NotificationManagerCompat.from(context)

    fun ongoing(): Notification {
        manager.createNotificationChannel(
            NotificationChannelCompat.Builder(ONGOING_CHANNEL, NotificationManagerCompat.IMPORTANCE_LOW)
                .setName(context.getString(R.string.channel_focus_mode))
                .build(),
        )
        return NotificationCompat.Builder(context, ONGOING_CHANNEL)
            .setSmallIcon(R.drawable.ic_focus)
            .setContentTitle(context.getString(R.string.notification_focus_on))
            .setContentIntent(openApp())
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    // Used when "Display over other apps" is not granted, so the unlock prompt cannot be drawn.
    fun unlockReminder(previousUnlockLine: String) {
        val denied = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        if (denied) return
        manager.createNotificationChannel(
            NotificationChannelCompat.Builder(REMINDER_CHANNEL, NotificationManagerCompat.IMPORTANCE_HIGH)
                .setName(context.getString(R.string.channel_unlock_reminder))
                .build(),
        )
        val reminder = NotificationCompat.Builder(context, REMINDER_CHANNEL)
            .setSmallIcon(R.drawable.ic_focus)
            .setContentTitle(context.getString(R.string.prompt_question))
            .setContentText(previousUnlockLine)
            .setContentIntent(openApp())
            .setAutoCancel(true)
            .build()
        manager.notify(REMINDER_ID, reminder)
    }

    private fun openApp(): PendingIntent = PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE,
    )

    companion object {
        const val ONGOING_ID = 1
        private const val REMINDER_ID = 2
        private const val ONGOING_CHANNEL = "focus_mode"
        private const val REMINDER_CHANNEL = "unlock_reminder"
    }
}
