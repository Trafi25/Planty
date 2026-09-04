package com.traffipart.polanty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.traffipart.polanty.presentation.root.PlantyRoot
import com.traffipart.polanty.ui.theme.PolantyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PolantyTheme {
                PlantyRoot()
            }
        }
    }
}
