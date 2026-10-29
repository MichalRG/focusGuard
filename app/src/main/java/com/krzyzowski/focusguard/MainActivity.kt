package com.krzyzowski.focusguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.krzyzowski.focusguard.ui.FocusGuardTheme
import com.krzyzowski.focusguard.ui.FocusRoute
import com.krzyzowski.focusguard.ui.FocusViewModel

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
}
