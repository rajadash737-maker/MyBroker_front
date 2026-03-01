package com.example.myapplication.ui.feature.home_pase.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.network.ApiClient
import com.example.myapplication.ui.feature.buyer.domain.repository.LandRepository

@Composable
fun HomeContent(
    padding: PaddingValues,
    navController: NavController,
    landRepository: LandRepository // NEW
) {
    val context = LocalContext.current
    val api = remember(context) {
        ApiClient.init(context.applicationContext)
        ApiClient.apiService
    }

    var userName by remember { mutableStateOf("User") }
    var nearbyLands by remember { mutableStateOf<List<HomeLandItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            val profileRes = api.getMyProfile()
            if (profileRes.isSuccessful) {
                userName = profileRes.body()?.fullname ?: "User"
            }
        } catch (_: Exception) {
        }

        try {
            val lands = landRepository.getNearbyLands(skip = 0, limit = 10, sort = "latest")
            nearbyLands = lands.map { land ->
                HomeLandItem(
                    id = land.id,
                    imageUrl = land.coverImageUrl,
                    price = land.formattedPrice,
                    area = land.formattedArea,
                    district = land.districtOrCity,
                    state = land.stateText
                )
            }
        } catch (_: Exception) {
            nearbyLands = emptyList()
        }
    }

    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item { WelcomeSection(userName = userName) }
        item { MyBrokerSearchBar() }
        item { BannerSlider() }
        item { DashboardCards(navController) }
        item {
            PropertyList(
                lands = nearbyLands,
                onViewMoreClick = { navController.navigate("explore") },
                onCardClick = { landId -> navController.navigate("land_detail/$landId") }
            )
        }
    }
}
