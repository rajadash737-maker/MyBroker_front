package com.example.myapplication.ui.feature.home.profile

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.myapplication.R
import com.example.myapplication.network.ApiClient

@Composable
fun ProfileScreen(
    onEditClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val scroll = rememberScrollState()
    val context = LocalContext.current

    val api = remember(context) {
        ApiClient.init(context.applicationContext)
        ApiClient.apiService
    }

    var userName by remember { mutableStateOf("User") }
    var userEmail by remember { mutableStateOf("user@example.com") }
    var serverImage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val res = api.getMyProfile()
            if (res.isSuccessful) {
                res.body()?.let { p ->
                    userName = p.fullname ?: "User"
                    userEmail = p.email ?: "user@example.com"
                    serverImage = ApiClient.absoluteUrl(p.profileImageUrl?.trim())
                }
            }
        } catch (_: Exception) {
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        BoxHeader(
            userName = userName,
            userEmail = userEmail,
            serverImage = serverImage,
            onEditClick = onEditClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("My Activities", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        ProfileOption(icon = Icons.Default.LocationOn, title = "My Listed Lands")
        ProfileOption(icon = Icons.Default.Favorite, title = "My Bookmarked Lands")
        ProfileOption(icon = Icons.Default.Receipt, title = "My Transactions")
        ProfileOption(icon = Icons.Default.Map, title = "Map Preferences")

        Spacer(modifier = Modifier.height(20.dp))

        Text("App Settings", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        ProfileOption(icon = Icons.Default.Notifications, title = "Notifications")
        ProfileOption(icon = Icons.Default.DarkMode, title = "Dark Mode")
        ProfileOption(icon = Icons.Default.Language, title = "Language")
        ProfileOption(icon = Icons.Default.Info, title = "Privacy Policy")

        Spacer(modifier = Modifier.height(20.dp))

        Text("Follow Us", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        ProfileOption(icon = Icons.Default.Phone, title = "WhatsApp")
        ProfileOption(icon = Icons.Default.CameraAlt, title = "Instagram")
        ProfileOption(icon = Icons.Default.Send, title = "Telegram")
        ProfileOption(icon = Icons.Default.PlayArrow, title = "YouTube")
        ProfileOption(icon = Icons.Default.Work, title = "LinkedIn")

        Spacer(modifier = Modifier.height(20.dp))

        ProfileOption(
            icon = Icons.Default.Logout,
            title = "Logout",
            onClick = {
                logoutUser(context)
                onLogoutClick()
            }
        )
    }
}

@Composable
private fun BoxHeader(
    userName: String,
    userEmail: String,
    serverImage: String?,
    onEditClick: () -> Unit
) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!serverImage.isNullOrBlank()) {
                SubcomposeAsyncImage(
                    model = serverImage,
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape),
                    loading = {
                        CircularProgressIndicator(strokeWidth = 2.dp)
                    },
                    error = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_user),
                            contentDescription = "Profile Photo",
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                        )
                    }
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = userName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = userEmail, fontSize = 14.sp, color = Color.Gray)
                TextButton(onClick = onEditClick) { Text("Edit Profile") }
            }
        }
    }
}

@Composable
fun ProfileOption(icon: ImageVector, title: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = title, fontSize = 16.sp)
    }
}

fun logoutUser(context: Context) {
    val prefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    prefs.edit().clear().apply()
}
