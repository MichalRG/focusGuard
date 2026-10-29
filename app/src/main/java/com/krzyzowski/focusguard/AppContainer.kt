package com.krzyzowski.focusguard

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.krzyzowski.focusguard.data.FocusSettings
import com.krzyzowski.focusguard.data.UnlockLog
import com.krzyzowski.focusguard.data.WordOfUnlock
import com.krzyzowski.focusguard.service.FocusMode
import com.krzyzowski.focusguard.unlock.UnlockTimeText
import com.krzyzowski.focusguard.words.UNLOCK_WORDS
import java.time.ZoneId
import java.util.Locale
import kotlin.random.Random

class AppContainer(context: Context) {
    // Device-protected so focus mode can start before the first unlock after a reboot.
    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { context.createDeviceProtectedStorageContext().preferencesDataStoreFile("focus") },
    )
    private val zone = ZoneId.systemDefault()

    val focusSettings = FocusSettings(dataStore)
    val unlockLog = UnlockLog(dataStore, zone)
    val wordOfUnlock = WordOfUnlock(dataStore, UNLOCK_WORDS, Random.Default)
    val unlockTimeText = UnlockTimeText(zone, Locale.getDefault())
    val focusMode = FocusMode(context, focusSettings)
}
