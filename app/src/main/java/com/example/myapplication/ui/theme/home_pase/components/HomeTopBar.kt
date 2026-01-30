package com.example.myapplication.ui.theme.home_pase.components



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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onNotificationClick: () -> Unit,
    onLandClick: () -> Unit,
    onProfileClick: () -> Unit
) {
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
                Image(
                    painter = painterResource(R.drawable.ic_user),
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
