package com.krzyzowski.focusguard.data

import java.io.File

class FocusStoreLocation(
    private val deviceProtectedDir: File,
    private val legacyDir: File,
    private val legacyReadable: Boolean,
) {
    fun resolve(): File {
        val target = File(deviceProtectedDir, STORE_PATH)
        val legacy = File(legacyDir, STORE_PATH)
        if (legacyReadable && !target.exists() && legacy.exists()) {
            target.parentFile?.mkdirs()
            legacy.copyTo(target)
            legacy.delete()
        }
        return target
    }

    companion object {
        const val STORE_PATH = "datastore/focus.preferences_pb"
    }
}
