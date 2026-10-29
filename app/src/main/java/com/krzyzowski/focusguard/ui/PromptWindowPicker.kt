package com.krzyzowski.focusguard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.krzyzowski.focusguard.R
import java.time.Duration

private val WINDOW_OPTIONS: List<Duration?> = listOf(15L, 30L, 60L, 120L, 240L, 480L).map(Duration::ofMinutes) + null

@Composable
fun PromptWindowPicker(selected: Duration?, onSelect: (Duration?) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.prompt_window_title), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.prompt_window_explanation), style = MaterialTheme.typography.bodyMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WINDOW_OPTIONS.forEach { window ->
                    FilterChip(
                        selected = window == selected,
                        onClick = { onSelect(window) },
                        label = {
                            Text(
                                when {
                                    window == null -> stringResource(R.string.prompt_window_off)
                                    window.toMinutes() < 60 ->
                                        stringResource(R.string.prompt_window_minutes, window.toMinutes())
                                    else -> stringResource(R.string.prompt_window_hours, window.toHours())
                                },
                            )
                        },
                    )
                }
            }
        }
    }
}
