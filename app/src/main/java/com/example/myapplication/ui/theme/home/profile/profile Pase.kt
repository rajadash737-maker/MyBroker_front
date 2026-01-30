package com.example.myapplication.ui.theme.home.profile


import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.R

//import com.example.myApplication.R




@Composable
fun ProfileScreen(
    onEditClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {

    val scroll = rememberScrollState()

    // ⭐ READ FROM SHARED PREFERENCES ⭐
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)

    val serverImage = prefs.getString("PROFILE_IMAGE", null)
    val userName = prefs.getString("FULL_NAME", "User") ?: "User"
    val userEmail = prefs.getString("EMAIL", "user@example.com") ?: "user@example.com"


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {

        // =========================
        // Header Section
        // =========================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                when {
                    serverImage != null -> {
                        Image(
                            painter = rememberAsyncImagePainter(
                                "http://192.168.43.152/myapp/uploads/profile/$serverImage"
                            ),
                            contentDescription = "Profile Photo",
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                        )
                    }
                    else -> {
                        Image(
                            painter = painterResource(id = R.drawable.ic_user),
                            contentDescription = "Profile Photo",
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(text = userName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = userEmail, fontSize = 14.sp, color = Color.Gray)
                    TextButton(onClick = onEditClick) {
                        Text("Edit Profile")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // =========================
        // My Activities
        // =========================
        Text("My Activities", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        ProfileOption(icon = Icons.Default.LocationOn, title = "My Listed Lands")
        ProfileOption(icon = Icons.Default.Favorite, title = "My Bookmarked Lands")
        ProfileOption(icon = Icons.Default.Receipt, title = "My Transactions")
        ProfileOption(icon = Icons.Default.Map, title = "Map Preferences")

        Spacer(modifier = Modifier.height(20.dp))

        // =========================
        // App Settings
        // =========================
        Text("App Settings", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        ProfileOption(icon = Icons.Default.Notifications, title = "Notifications")
        ProfileOption(icon = Icons.Default.DarkMode, title = "Dark Mode")
        ProfileOption(icon = Icons.Default.Language, title = "Language")
        ProfileOption(icon = Icons.Default.Info, title = "Privacy Policy")

        Spacer(modifier = Modifier.height(20.dp))

        // =========================
        // Social Media Section
        // =========================
        Text("Follow Us", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

        ProfileOption(icon = Icons.Default.Phone, title = "WhatsApp")
        ProfileOption(icon = Icons.Default.CameraAlt, title = "Instagram")
        ProfileOption(icon = Icons.Default.Send, title = "Telegram")
        ProfileOption(icon = Icons.Default.PlayArrow, title = "YouTube")
        ProfileOption(icon = Icons.Default.Work, title = "LinkedIn")

        Spacer(modifier = Modifier.height(20.dp))

        // Logout
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



@Composable fun ProfileOption(icon: ImageVector, title: String, onClick: () -> Unit = {}) {
    Row( modifier = Modifier .fillMaxWidth() .clickable {
        onClick() }
        .background(Color.White, shape = RoundedCornerShape(12.dp))
        .padding(16.dp)
        .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically ) {
        Icon(icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = title, fontSize = 16.sp) } }


// ------------LOGOUT---------


fun logoutUser(context: Context) {
    val prefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    prefs.edit().clear().apply()
}


