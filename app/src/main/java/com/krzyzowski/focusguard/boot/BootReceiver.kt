package com.krzyzowski.focusguard.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.krzyzowski.focusguard.FocusGuardApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in RESUME_ACTIONS) return
        val focusMode = (context.applicationContext as FocusGuardApp).container.focusMode
        val pending = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                focusMode.resumeIfEnabled()
            } finally {
                pending.finish()
            }
        }
    }

    private companion object {
        val RESUME_ACTIONS = setOf(
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
        )
    }
}
