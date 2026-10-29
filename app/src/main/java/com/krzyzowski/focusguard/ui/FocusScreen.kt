package com.krzyzowski.focusguard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.krzyzowski.focusguard.R
import java.time.Duration
import java.time.LocalDate

@Composable
fun FocusScreen(
    focusEnabled: Boolean,
    lastUnlockText: String,
    unlocksToday: Int,
    lastWeek: List<Pair<LocalDate, Int>>,
    promptWindow: Duration?,
    onPromptWindowChange: (Duration?) -> Unit,
    permissions: PermissionStatus,
    onFocusChange: (Boolean) -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenOverlaySettings: () -> Unit,
    onOpenBatterySettings: () -> Unit,
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.focus_mode), style = MaterialTheme.typography.titleLarge)
                        Text(stringResource(if (focusEnabled) R.string.focus_mode_on else R.string.focus_mode_off))
                    }
                    Switch(checked = focusEnabled, onCheckedChange = onFocusChange)
                }
            }
            Text(lastUnlockText, style = MaterialTheme.typography.bodyLarge)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        pluralStringResource(R.plurals.main_unlocks_today, unlocksToday, unlocksToday),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(stringResource(R.string.main_last_7_days), style = MaterialTheme.typography.labelLarge)
                    WeekChart(lastWeek)
                }
            }
            PromptWindowPicker(selected = promptWindow, onSelect = onPromptWindowChange)
            if (focusEnabled) {
                Text(stringResource(R.string.needed_for_focus_mode), style = MaterialTheme.typography.titleMedium)
                PermissionRow(R.string.permission_notifications, permissions.notifications, onOpenNotificationSettings)
                PermissionRow(R.string.permission_overlay, permissions.overlay, onOpenOverlaySettings)
                PermissionRow(R.string.permission_battery, permissions.batteryUnrestricted, onOpenBatterySettings)
            }
        }
    }
}

@Composable
private fun PermissionRow(label: Int, allowed: Boolean, onOpenSettings: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(label), modifier = Modifier.weight(1f))
        if (allowed) {
            Text(stringResource(R.string.permission_allowed), color = MaterialTheme.colorScheme.primary)
        } else {
            TextButton(onClick = onOpenSettings) { Text(stringResource(R.string.permission_open_settings)) }
        }
    }
}
