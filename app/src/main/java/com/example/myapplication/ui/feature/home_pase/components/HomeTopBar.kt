package com.example.myapplication.ui.feature.home_pase.components



import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.network.ApiClient




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onNotificationClick: () -> Unit,
    onLandClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val context = LocalContext.current
    val api = remember(context) {
        ApiClient.init(context.applicationContext)
        ApiClient.apiService
    }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val res = api.getMyProfile()
            if (res.isSuccessful) {
                val p = res.body()
                profileImageUrl = ApiClient.absoluteUrl(p?.profileImageUrl) ?: p?.profileImageUrl
            }
        } catch (_: Exception) {}
    }


    TopAppBar(
        modifier = Modifier.background(
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0D47A1),   // Dark blue
                    Color(0xFF1565C0)    // Light blue
                )
            )
        ),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        title = {
            Text(
                "MyBroker App",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = onNotificationClick) {
                Icon(Icons.Default.Notifications, null, tint = Color.White)
            }
            IconButton(onClick = onLandClick) {
                Icon(Icons.Default.HomeWork, null, tint = Color.White)
            }
            IconButton(onClick = onProfileClick) {
                val model = if (!profileImageUrl.isNullOrBlank()) profileImageUrl else R.drawable.ic_user
                Image(
                    painter = rememberAsyncImagePainter(model),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
            }

        }
    )
}
