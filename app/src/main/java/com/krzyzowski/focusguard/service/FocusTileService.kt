package com.krzyzowski.focusguard.service

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.krzyzowski.focusguard.FocusGuardApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FocusTileService : TileService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val container get() = (application as FocusGuardApp).container

    override fun onStartListening() {
        scope.launch { showState(container.focusSettings.enabled.first()) }
    }

    override fun onClick() {
        scope.launch {
            val enable = !container.focusSettings.enabled.first()
            if (enable) container.focusMode.turnOn() else container.focusMode.turnOff()
            showState(enable)
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun showState(enabled: Boolean) {
        val tile = qsTile ?: return
        tile.state = if (enabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.updateTile()
    }
}
