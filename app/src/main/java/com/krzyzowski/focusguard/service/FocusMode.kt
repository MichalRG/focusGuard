package com.krzyzowski.focusguard.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.krzyzowski.focusguard.data.FocusSettings
import kotlinx.coroutines.flow.first

class FocusMode(private val context: Context, private val settings: FocusSettings) {

    suspend fun turnOn() {
        settings.setEnabled(true)
        startService()
    }

    suspend fun turnOff() {
        settings.setEnabled(false)
        context.stopService(Intent(context, FocusService::class.java))
    }

    suspend fun resumeIfEnabled() {
        if (settings.enabled.first()) startService()
    }

    private fun startService() {
        ContextCompat.startForegroundService(context, Intent(context, FocusService::class.java))
    }
}
