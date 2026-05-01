package com.example.myapplication.ui.feature.home.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Facebook
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.myapplication.R
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.SocialLinksResponse
import com.example.myapplication.ui.feature.common.NoInternetContent
import com.example.myapplication.ui.feature.common.rememberIsConnected
import com.example.myapplication.ui.feature.ratings.RatingViewModel
import com.example.myapplication.ui.feature.ratings.RatingViewModelFactory
import com.example.myapplication.ui.feature.home_pase.components.LandTypeDialog

private const val PRIVACY_POLICY_URL = "https://sites.google.com/view/mybrokerr-privacy/home"

@Composable
fun ProfileScreen(
    onEditClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onMyListedSellerClick: () -> Unit = {},
    onMyListedBuyerClick: () -> Unit = {},
    onMyBookmarkedClick: () -> Unit = {},
    onMapPreferencesClick: () -> Unit = {},
    onMyTransactionsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onDarkModeClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {}
) {
    val scroll = rememberScrollState()
    val context = LocalContext.current
    val isConnected by rememberIsConnected()
    var retryTick by remember { mutableStateOf(0) }

    val api = remember(context) {
        ApiClient.init(context.applicationContext)
        ApiClient.apiService
    }
    val ratingViewModel: RatingViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = RatingViewModelFactory(context)
    )
    val ratingState by ratingViewModel.uiState.collectAsState()

    var userName by remember { mutableStateOf("User") }
    var userEmail by remember { mutableStateOf("user@example.com") }
    var serverImage by remember { mutableStateOf<String?>(null) }
    var socialLinks by remember { mutableStateOf(SocialLinksResponse(id = 0)) }
    var showLandTypeDialog by remember { mutableStateOf(false) }

    if (!isConnected) {
        NoInternetContent(
            onRetry = { retryTick++ },
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    LaunchedEffect(isConnected, retryTick) {
        if (!isConnected) return@LaunchedEffect
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
        try {
            val res = api.getSocialLinks()
            if (res.isSuccessful) {
                res.body()?.let { socialLinks = it }
            }
        } catch (_: Exception) {
        }
        ratingViewModel.loadSummaryForCurrentUser()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        if (showLandTypeDialog) {
            LandTypeDialog(
                onDismiss = { showLandTypeDialog = false },
                onSeller = {
                    showLandTypeDialog = false
                    onMyListedSellerClick()
                },
                onBuyer = {
                    showLandTypeDialog = false
                    onMyListedBuyerClick()
                }
            )
        }

        BoxHeader(
            userName = userName,
            userEmail = userEmail,
            serverImage = serverImage,
            onEditClick = onEditClick,
            ratingAverage = ratingState.summaryAverage,
            ratingCount = ratingState.summaryCount
        )

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle(text = stringResource(id = R.string.profile_my_activities))
        ProfileOption(
            icon = Icons.Default.LocationOn,
            title = stringResource(id = R.string.profile_my_listed_lands),
            onClick = { showLandTypeDialog = true }
        )
        ProfileOption(
            icon = Icons.Default.Favorite,
            title = stringResource(id = R.string.profile_my_bookmarked_lands),
            onClick = { onMyBookmarkedClick() }
        )
        ProfileOption(
            icon = Icons.Default.Receipt,
            title = stringResource(id = R.string.profile_my_transactions),
            onClick = { onMyTransactionsClick() }
        )
        ProfileOption(
            icon = Icons.Default.Map,
            title = stringResource(id = R.string.profile_map_preferences),
            onClick = { onMapPreferencesClick() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle(text = stringResource(id = R.string.profile_app_settings))
        ProfileOption(
            icon = Icons.Default.Notifications,
            title = stringResource(id = R.string.settings_notifications),
            onClick = { onNotificationsClick() }
        )
        ProfileOption(
            icon = Icons.Default.DarkMode,
            title = stringResource(id = R.string.settings_dark_mode),
            onClick = { onDarkModeClick() }
        )
        ProfileOption(
            icon = Icons.Default.Language,
            title = stringResource(id = R.string.settings_language),
            onClick = { onLanguageClick() }
        )
        ProfileOption(
            icon = Icons.Default.Info,
            title = stringResource(id = R.string.settings_privacy_policy),
            onClick = { openExternalLink(context, PRIVACY_POLICY_URL) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle(text = stringResource(id = R.string.profile_follow_us))
        SocialLinkOption(
            icon = Icons.Default.Phone,
            title = stringResource(id = R.string.social_whatsapp),
            url = socialLinks.whatsapp,
            context = context
        )
        SocialLinkOption(
            icon = Icons.Default.CameraAlt,
            title = stringResource(id = R.string.social_instagram),
            url = socialLinks.instagram,
            context = context
        )
        SocialLinkOption(
            icon = Icons.Default.Send,
            title = stringResource(id = R.string.social_telegram),
            url = socialLinks.telegram,
            context = context
        )
        SocialLinkOption(
            icon = Icons.Default.PlayArrow,
            title = stringResource(id = R.string.social_youtube),
            url = socialLinks.youtube,
            context = context
        )
        SocialLinkOption(
            icon = Icons.Default.Facebook,
            title = stringResource(id = R.string.social_facebook),
            url = socialLinks.facebook,
            context = context
        )
        SocialLinkOption(
            icon = Icons.Default.Work,
            title = stringResource(id = R.string.social_linkedin),
            url = socialLinks.linkedin,
            context = context
        )

        Spacer(modifier = Modifier.height(16.dp))

        ProfileOption(
            icon = Icons.Default.Logout,
            title = stringResource(id = R.string.logout),
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
    onEditClick: () -> Unit,
    ratingAverage: Double,
    ratingCount: Int
) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
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
                Text(
                    text = userEmail,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val avgText = if (ratingCount > 0) String.format("%.1f", ratingAverage) else "0.0"
                Text(
                    text = stringResource(id = R.string.profile_rating_summary, avgText, ratingCount),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onEditClick) { Text(stringResource(id = R.string.profile_edit_profile)) }
            }
        }
    }
}

@Composable
fun ProfileOption(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit = {},
    enabled: Boolean = true
) {
    val tint = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
    val textColor = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() }
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = tint
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            color = textColor
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
    )
}

@Composable
fun SocialLinkOption(
    icon: ImageVector,
    title: String,
    url: String?,
    context: Context
) {
    val link = url?.trim().orEmpty()
    val enabled = link.isNotEmpty()
    ProfileOption(
        icon = icon,
        title = title,
        enabled = enabled,
        onClick = {
            if (enabled) {
                openExternalLink(context, link)
            }
        }
    )
}

private fun openExternalLink(context: Context, rawUrl: String) {
    val normalized = if (
        rawUrl.startsWith("http://", ignoreCase = true) ||
        rawUrl.startsWith("https://", ignoreCase = true) ||
        rawUrl.startsWith("tel:", ignoreCase = true) ||
        rawUrl.startsWith("mailto:", ignoreCase = true) ||
        rawUrl.startsWith("tg://", ignoreCase = true) ||
        rawUrl.startsWith("whatsapp://", ignoreCase = true)
    ) {
        rawUrl
    } else {
        "https://$rawUrl"
    }

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(normalized))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

fun logoutUser(context: Context) {
    val prefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    prefs.edit().clear().apply()
}

