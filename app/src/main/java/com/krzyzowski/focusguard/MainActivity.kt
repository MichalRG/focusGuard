package com.krzyzowski.focusguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.krzyzowski.focusguard.ui.FocusGuardTheme
import com.krzyzowski.focusguard.ui.FocusRoute
import com.krzyzowski.focusguard.ui.FocusViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: FocusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FocusGuardTheme {
                FocusRoute(viewModel)
            }
        }
    }

    // A force-stop kills the service but keeps the saved On state, so restart it when the app is opened.
    override fun onStart() {
        super.onStart()
        lifecycleScope.launch { (application as FocusGuardApp).container.focusMode.resumeIfEnabled() }
    }
}
