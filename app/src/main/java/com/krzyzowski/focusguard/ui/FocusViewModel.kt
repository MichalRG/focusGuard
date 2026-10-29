package com.krzyzowski.focusguard.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.krzyzowski.focusguard.FocusGuardApp
import com.krzyzowski.focusguard.data.DailyUnlockCounts
import com.krzyzowski.focusguard.data.FocusSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

class FocusViewModel(application: Application) : AndroidViewModel(application) {
    private val container = (application as FocusGuardApp).container

    val unlockTimeText = container.unlockTimeText

    val focusEnabled: StateFlow<Boolean> = container.focusSettings.enabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val lastUnlock: StateFlow<Instant?> = container.unlockLog.lastUnlock
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val dailyCounts: StateFlow<DailyUnlockCounts> = container.unlockLog.dailyCounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DailyUnlockCounts.NONE)

    val promptWindow: StateFlow<Duration?> = container.focusSettings.promptWindow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FocusSettings.DEFAULT_PROMPT_WINDOW)

    fun setPromptWindow(window: Duration?) {
        viewModelScope.launch { container.focusSettings.setPromptWindow(window) }
    }

    fun setFocusEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) container.focusMode.turnOn() else container.focusMode.turnOff()
        }
    }
}
