package com.krzyzowski.focusguard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.TextStyle

@Composable
fun WeekChart(days: List<Pair<LocalDate, Int>>, modifier: Modifier = Modifier) {
    val highest = days.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1
    val locale = LocalConfiguration.current.locales[0]
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        days.forEach { (date, count) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(count.toString(), style = MaterialTheme.typography.labelSmall)
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height((BAR_MAX_HEIGHT * count / highest).coerceAtLeast(2).dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)),
                )
                Text(
                    date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

private const val BAR_MAX_HEIGHT = 96
