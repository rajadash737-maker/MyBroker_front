package com.example.myapplication.ui.feature.home_pase.components



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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


@Composable
fun HomeContent(
    padding: PaddingValues,
    navController: NavController
) {

    val context = LocalContext.current
    val api = remember(context) {
        ApiClient.init(context.applicationContext)
        ApiClient.apiService
    }
    var userName by remember { mutableStateOf("User") }

    LaunchedEffect(Unit) {
        try {
            val res = api.getMyProfile()
            if (res.isSuccessful) {
                userName = res.body()?.fullname ?: "User"
            }
        } catch (_: Exception) {}
    }

    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item { WelcomeSection(userName = userName) }

        item {MyBrokerSearchBar()}
        item { BannerSlider() }
        item { DashboardCards(navController) }
        item { PropertyList() }
    }
}
