package com.krzyzowski.focusguard

import android.content.Context
import android.os.UserManager
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.krzyzowski.focusguard.data.FocusSettings
import com.krzyzowski.focusguard.data.FocusStoreLocation
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
        produceFile = {
            FocusStoreLocation(
                deviceProtectedDir = context.createDeviceProtectedStorageContext().filesDir,
                legacyDir = context.filesDir,
                legacyReadable = context.getSystemService(UserManager::class.java).isUserUnlocked,
            ).resolve()
        },
    )

    val focusSettings = FocusSettings(dataStore)
    val unlockLog = UnlockLog(dataStore, ZoneId::systemDefault)
    val wordOfUnlock = WordOfUnlock(dataStore, UNLOCK_WORDS, Random.Default)
    val unlockTimeText = UnlockTimeText(ZoneId::systemDefault, Locale.getDefault())
    val focusMode = FocusMode(context, focusSettings)
}
