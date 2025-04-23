package com.personal.realtimepricetracker.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.personal.realtimepricetracker.utils.AuthResult
import com.personal.realtimepricetracker.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, authViewModel: AuthViewModel) {
    val authResult by authViewModel.authResult.collectAsState()
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate(if(authResult is AuthResult.Success) navController.navigate("home") else navController.navigate("login")) {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}