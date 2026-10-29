package com.krzyzowski.focusguard.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class FocusStoreLocationTest {
    @get:Rule
    val folder = TemporaryFolder()

    private val deviceDir by lazy { folder.newFolder("user_de") }
    private val legacyDir by lazy { folder.newFolder("user_ce") }

    private fun legacyStore(content: String) = File(legacyDir, FocusStoreLocation.STORE_PATH).apply {
        parentFile?.mkdirs()
        writeText(content)
    }

    @Test
    fun `the store lives in device-protected storage`() {
        val file = FocusStoreLocation(deviceDir, legacyDir, legacyReadable = true).resolve()

        assertEquals(File(deviceDir, FocusStoreLocation.STORE_PATH), file)
    }

    @Test
    fun `data saved by an older version is moved into device-protected storage`() {
        val legacy = legacyStore("saved settings")

        val file = FocusStoreLocation(deviceDir, legacyDir, legacyReadable = true).resolve()

        assertEquals("saved settings", file.readText())
        assertFalse(legacy.exists())
    }

    @Test
    fun `existing device-protected data is never overwritten by old data`() {
        legacyStore("old settings")
        File(deviceDir, FocusStoreLocation.STORE_PATH).apply {
            parentFile?.mkdirs()
            writeText("current settings")
        }

        val file = FocusStoreLocation(deviceDir, legacyDir, legacyReadable = true).resolve()

        assertEquals("current settings", file.readText())
    }

    @Test
    fun `old data is left alone while the phone is still locked`() {
        val legacy = legacyStore("saved settings")

        val file = FocusStoreLocation(deviceDir, legacyDir, legacyReadable = false).resolve()

        assertFalse(file.exists())
        assertTrue(legacy.exists())
    }
}
