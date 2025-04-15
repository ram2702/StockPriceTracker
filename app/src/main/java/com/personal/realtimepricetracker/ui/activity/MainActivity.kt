package com.personal.realtimepricetracker.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.personal.realtimepricetracker.navigation.NavHostComposable
import com.personal.realtimepricetracker.ui.theme.RealTimePriceTrackerTheme
import com.personal.realtimepricetracker.viewmodel.AuthViewModel
import com.personal.realtimepricetracker.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainViewModel: MainViewModel by viewModels()
        val authViewModel: AuthViewModel by viewModels()
        Firebase.initialize(this)
        enableEdgeToEdge()
        setContent {
            Log.d("MainActivity", "ViewModel initialized")
            RealTimePriceTrackerTheme {
                val navHostController = rememberNavController()
                NavHostComposable(navHostController, mainViewModel, authViewModel, this@MainActivity)
            }
        }
    }
}