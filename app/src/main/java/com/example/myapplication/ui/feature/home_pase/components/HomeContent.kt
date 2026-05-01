package com.example.myapplication.ui.feature.home_pase.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.BannerResponse
import com.example.myapplication.ui.feature.common.PullToRefreshBox

@Composable
fun HomeContent(
    navController: NavController,
    userName: String,
    banners: List<BannerResponse>,
    nearbyLands: List<HomeLandItem>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item { WelcomeSection(userName = userName) }
            item {
                MyBrokerSearchBar(
                    onClick = { navController.navigate("explore") }
                )
            }
            item {
                val bannerUrls = banners
                    .filter { it.isActive }
                    .sortedBy { it.sortOrder }
                    .mapNotNull { ApiClient.absoluteUrl(it.imageUrl) }
                BannerSlider(bannerUrls = bannerUrls)
            }
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
}
