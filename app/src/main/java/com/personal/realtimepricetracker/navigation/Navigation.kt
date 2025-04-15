package com.personal.realtimepricetracker.navigation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.personal.realtimepricetracker.R
import com.personal.realtimepricetracker.ui.composables.HomePage
import com.personal.realtimepricetracker.ui.composables.Profile
import com.personal.realtimepricetracker.ui.composables.SearchPage
import com.personal.realtimepricetracker.ui.composables.SignInScreen
import com.personal.realtimepricetracker.ui.composables.SignUpScreen
import com.personal.realtimepricetracker.ui.composables.StockDetail
import com.personal.realtimepricetracker.ui.composables.StockPredictor
import com.personal.realtimepricetracker.utils.AuthResult
import com.personal.realtimepricetracker.viewmodel.AuthViewModel
import com.personal.realtimepricetracker.viewmodel.MainViewModel

@Composable
fun NavHostComposable(
    navHost: NavHostController,
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    context: Context
){
    val TAG = "Navigation"
    val googleSignInClient = authViewModel.getGoogleSignInClient()
    val authResult by authViewModel.authResult.collectAsState()
    val userId by authViewModel.userId.collectAsState()
    val signInExecutor:(String,String)->Unit={email,password ->
        authViewModel.signInWithCredentials(email, password)
    }
    val signUpExecutor:(String,String,String)->Unit={email,password,userName ->
        authViewModel.createUserAtSignUp(email, password,userName)
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            authViewModel.signInWithGoogleCredential(account.idToken!!)
            authViewModel.setSuccess()
        } catch (e: ApiException) {
            Log.e("GOOGLE_LOGIN", "Google sign-in failed", e)
        }
    }

    LaunchedEffect(userId) {
        mainViewModel.getWatchListItemForUser()
        authViewModel.fetchUsername()
    }

    LaunchedEffect(authResult) {
        when(authResult){
            is AuthResult.Success -> navHost.navigate("home"){
                popUpTo("login"){inclusive = true} // pops backstack upto the login route specified in popUpTo
            }

            is AuthResult.Idle -> {
            navHost.navigate("login") {
                popUpToRouteClass
                }
            }
            AuthResult.Loading -> Log.d(TAG,"Hi")
            is AuthResult.Failure -> Toast.makeText(context, (authResult as AuthResult.Failure).message , Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().padding(),
        bottomBar = { if(authResult is AuthResult.Success) SvBottomBar(navHost,authViewModel) }
    ) { innerPadding ->
            NavHost(navController = navHost, startDestination = "login", modifier = Modifier.padding(innerPadding)){
                composable("login") {
                    SignInScreen(onSignInClick = signInExecutor ,{
                        authViewModel.setLoading()
                        launcher.launch(googleSignInClient.signInIntent)
                    }, onSignUpClick = {navHost.navigate("signup")})
                }
                composable("signup") {
                    SignUpScreen(onContinueClick = signUpExecutor) {
                        authViewModel.setLoading()
                        launcher.launch(googleSignInClient.signInIntent)
                    }
                }
                composable("home"){
                    HomePage(mainViewModel,navHost)
                }
                composable("search") {
                    SearchPage(mainViewModel,authViewModel,innerPadding)
                }
                composable("profile") {
                    Profile(authViewModel,innerPadding)
                }
                composable("details") {
                    StockDetail(navHost, mainViewModel = mainViewModel, context)
                }
                composable("predictor") {
                    StockPredictor()
                }
            }
            if (authResult is AuthResult.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(enabled = false) {} // To block interaction
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
    }
}


@Composable
fun SvBottomBar(navController: NavController, authViewModel: AuthViewModel) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF3B4CD5))
            .padding(vertical = 8.dp).navigationBarsPadding()
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
            IconButton(onClick = { navController.navigate("predictor") }) {
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
            IconButton(onClick = { navController.navigate("profile") }) {
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

