package com.krzyzowski.focusguard.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.krzyzowski.focusguard.R
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId

@Composable
fun FocusRoute(viewModel: FocusViewModel) {
    val context = LocalContext.current
    val focusEnabled by viewModel.focusEnabled.collectAsStateWithLifecycle()
    val lastUnlock by viewModel.lastUnlock.collectAsStateWithLifecycle()
    val dailyCounts by viewModel.dailyCounts.collectAsStateWithLifecycle()
    val promptWindow by viewModel.promptWindow.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    var permissions by remember { mutableStateOf(PermissionStatus.read(context)) }
    var now by remember { mutableStateOf(Instant.now()) }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            permissions = PermissionStatus.read(context)
            while (true) {
                now = Instant.now()
                delay(30_000)
            }
        }
    }

    val today = now.atZone(ZoneId.systemDefault()).toLocalDate()
    val openOverlaySettings = {
        context.startActivity(
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, "package:${context.packageName}".toUri()),
        )
    }
    val requestNotifications = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {
        if (!Settings.canDrawOverlays(context)) openOverlaySettings()
    }

    FocusScreen(
        focusEnabled = focusEnabled,
        lastUnlockText = lastUnlock
            ?.let { stringResource(R.string.main_last_unlock, viewModel.unlockTimeText.describe(it, now)) }
            ?: stringResource(R.string.main_no_unlock),
        unlocksToday = dailyCounts.countOn(today),
        lastWeek = dailyCounts.lastDays(today, 7),
        promptWindow = promptWindow,
        onPromptWindowChange = viewModel::setPromptWindow,
        permissions = permissions,
        onFocusChange = { enabled ->
            viewModel.setFocusEnabled(enabled)
            when {
                !enabled -> Unit
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !permissions.notifications ->
                    requestNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
                !permissions.overlay -> openOverlaySettings()
            }
        },
        onOpenNotificationSettings = {
            context.startActivity(
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName),
            )
        },
        onOpenOverlaySettings = openOverlaySettings,
        onOpenBatterySettings = {
            context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
        },
    )
}
