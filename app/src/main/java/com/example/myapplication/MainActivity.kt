package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.SessionManager
import com.example.myapplication.ui.feature.NavigationGraph
import com.example.myapplication.ui.feature.buyer.data.remote.LandApi
import com.example.myapplication.ui.feature.buyer.data.repository.LandRepositoryImpl
import com.example.myapplication.ui.feature.buyer.data.repository.WishlistRepositoryImpl

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ApiClient.init(applicationContext)

        setContent {
            MyApplicationTheme()
        }
    }
}

@Composable
fun MyApplicationTheme() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = SessionManager(context.applicationContext)

    val startDestination = if (sessionManager.isLoggedIn()) "home" else "login"

    val landApi = remember {
        ApiClient.createService(LandApi::class.java)
    }

    val landRepository = remember { LandRepositoryImpl(landApi) }
    val wishlistRepository = remember { WishlistRepositoryImpl(landApi) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            NavigationGraph(
                navController = navController,
                landRepository = landRepository,
                wishlistRepository = wishlistRepository
            )
        }
    }
}
