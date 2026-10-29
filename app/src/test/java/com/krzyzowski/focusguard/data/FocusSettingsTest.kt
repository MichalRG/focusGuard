package com.krzyzowski.focusguard.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.time.Duration

class FocusSettingsTest {
    @get:Rule
    val folder = TemporaryFolder()

    private val dataStoreScope = CoroutineScope(Dispatchers.IO + Job())

    private val settings by lazy {
        FocusSettings(
            PreferenceDataStoreFactory.create(scope = dataStoreScope) {
                File(folder.root, "focus.preferences_pb")
            },
        )
    }

    @After
    fun closeDataStore() {
        dataStoreScope.cancel()
    }

    @Test
    fun `focus mode is off on a fresh install`() = runTest {
        assertFalse(settings.enabled.first())
    }

    @Test
    fun `turning focus mode on is remembered`() = runTest {
        settings.setEnabled(true)

        assertTrue(settings.enabled.first())
    }

    @Test
    fun `turning focus mode off after on is remembered`() = runTest {
        settings.setEnabled(true)
        settings.setEnabled(false)

        assertFalse(settings.enabled.first())
    }

    @Test
    fun `the card asks for unlocks within two hours by default`() = runTest {
        assertEquals(Duration.ofHours(2), settings.promptWindow.first())
    }

    @Test
    fun `a chosen prompt window is remembered`() = runTest {
        settings.setPromptWindow(Duration.ofMinutes(30))

        assertEquals(Duration.ofMinutes(30), settings.promptWindow.first())
    }

    @Test
    fun `turning the time limit off is remembered`() = runTest {
        settings.setPromptWindow(null)

        assertNull(settings.promptWindow.first())
    }
}
