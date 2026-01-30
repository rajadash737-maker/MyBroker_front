package com.example.myapplication.ui.theme.home_pase.components



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun HomeContent(
    padding: PaddingValues,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item { WelcomeSection() }
        item {MyBrokerSearchBar()}
        item { BannerSlider() }
        item { DashboardCards(navController) }
        item { PropertyList() }
    }
}
