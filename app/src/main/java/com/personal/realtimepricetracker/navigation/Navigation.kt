package com.personal.realtimepricetracker.navigation

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.personal.realtimepricetracker.R
import com.personal.realtimepricetracker.ui.theme.HomePage
import com.personal.realtimepricetracker.ui.theme.SearchPage
import com.personal.realtimepricetracker.viewmodel.PriceTrackerViewModel

@Composable
fun NavHostComposable(navHost: NavHostController,viewModel: PriceTrackerViewModel){
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { SvBottomBar(navHost) }
    ) { innerPadding ->
        NavHost(navController = navHost, startDestination = "home", modifier = Modifier.padding(innerPadding)){
            composable("home"){
                HomePage(viewModel)
            }
            composable("search") {
                SearchPage(viewModel,innerPadding)
            }
        }
    }
}

@Composable
fun SvBottomBar(navController: NavController) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF3B4CD5))
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { navController.navigate("Home") }) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Home",
                    tint = Color.White
                )
            }
            IconButton(onClick = { navController.navigate("Data") }) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.data_icon),
                    contentDescription = "Data",
                    tint = Color.White
                )
            }
            IconButton(onClick = { navController.navigate("search") }) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
            IconButton(onClick = { navController.navigate("Profile") }) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Profile",
                    tint = Color.White
                )
            }
        }
    }
}

