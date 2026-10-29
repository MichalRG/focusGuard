package com.krzyzowski.focusguard.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.IBinder
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.krzyzowski.focusguard.FocusGuardApp
import com.krzyzowski.focusguard.prompt.UnlockPrompt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant

class FocusService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val container get() = (application as FocusGuardApp).container
    private lateinit var notifications: FocusNotifications
    private lateinit var prompt: UnlockPrompt

    // ACTION_USER_PRESENT is only delivered to receivers registered at runtime.
    private val unlockReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            scope.launch { showPromptForUnlock() }
        }
    }

    override fun onCreate() {
        super.onCreate()
        notifications = FocusNotifications(this)
        prompt = UnlockPrompt(this, container.unlockTimeText, notifications)
        ServiceCompat.startForeground(
            this,
            FocusNotifications.ONGOING_ID,
            notifications.ongoing(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
        )
        // USER_PRESENT is sent by SystemUI (another app), so NOT_EXPORTED drops it; it is a protected broadcast only the platform can send.
        ContextCompat.registerReceiver(
            this,
            unlockReceiver,
            IntentFilter(Intent.ACTION_USER_PRESENT),
            ContextCompat.RECEIVER_EXPORTED,
        )
        // A sticky restart by the system does not check the setting, so the service checks it itself.
        scope.launch {
            if (!container.focusSettings.enabled.first()) stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        unregisterReceiver(unlockReceiver)
        prompt.dismiss()
        scope.cancel()
        super.onDestroy()
    }

    private suspend fun showPromptForUnlock() {
        val unlock = container.unlockLog.record(Instant.now())
        if (unlock.needsPrompt(container.focusSettings.promptWindow.first())) {
            prompt.show(unlock, container.wordOfUnlock.nextForCard())
        }
    }
}
