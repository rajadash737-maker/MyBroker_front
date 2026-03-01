package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.network.ApiClient
import com.example.myapplication.ui.feature.NavigationGraph
import com.example.myapplication.ui.feature.buyer.data.remote.LandApi
import com.example.myapplication.ui.feature.buyer.data.repository.LandRepositoryImpl
import com.example.myapplication.ui.feature.buyer.data.repository.WishlistRepositoryImpl

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApiClient.init(applicationContext)

        setContent {
            val navController = rememberNavController()

            val landApi = remember { ApiClient.createService(LandApi::class.java) }
            val landRepository = remember { LandRepositoryImpl(landApi) }
            val wishlistRepository = remember { WishlistRepositoryImpl(landApi) }

            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                NavigationGraph(
                    navController = navController,
                    landRepository = landRepository,
                    wishlistRepository = wishlistRepository
                )
            }
        }
    }
}
