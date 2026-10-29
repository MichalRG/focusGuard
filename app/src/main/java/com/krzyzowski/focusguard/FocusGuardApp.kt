package com.krzyzowski.focusguard

import android.app.Application

class FocusGuardApp : Application() {
    val container by lazy { AppContainer(this) }
}
