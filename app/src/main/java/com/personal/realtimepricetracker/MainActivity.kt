package com.personal.realtimepricetracker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.personal.realtimepricetracker.navigation.NavHostComposable
import com.personal.realtimepricetracker.ui.theme.RealTimePriceTrackerTheme
import com.personal.realtimepricetracker.viewmodel.PriceTrackerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: PriceTrackerViewModel by viewModels()
        enableEdgeToEdge()
        setContent {
            Log.d("MainActivity", "ViewModel initialized")
            RealTimePriceTrackerTheme {
                val navHostController = rememberNavController()
                NavHostComposable(navHostController, viewModel)
            }
        }
    }
}