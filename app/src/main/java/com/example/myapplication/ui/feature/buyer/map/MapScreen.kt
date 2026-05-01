package com.example.myapplication.ui.feature.buyer.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.feature.common.NoInternetContent
import com.example.myapplication.ui.feature.common.rememberIsConnected
import com.example.myapplication.ui.feature.home.profile.LocalThemeViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onLandClick: (Int) -> Unit,
    focusLandId: Int? = null
) {
    val lands = viewModel.lands
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val isConnected by rememberIsConnected()

    var selectedLand by remember { mutableStateOf<MapLand?>(null) }
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val themeViewModel = LocalThemeViewModel.current
    val isDarkTheme by themeViewModel.isDark.collectAsState(initial = isSystemInDarkTheme())
    val mapStyle = remember(isDarkTheme) {
        if (!isDarkTheme) return@remember null
        runCatching {
            MapStyleOptions.loadRawResourceStyle(context, R.raw.map_dark)
        }.getOrNull()
    }
    val colors = MaterialTheme.colorScheme

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(20.2961, 85.8245), 15f)
    }

    if (!isConnected) {
        NoInternetContent(
            onRetry = { viewModel.loadMapData() },
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    LaunchedEffect(focusLandId, lands.size) {
        val target = lands.firstOrNull { it.id == focusLandId } ?: lands.firstOrNull()
        if (target != null) {
            selectedLand = target
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(getPolygonCenter(target), 18f),
                900
            )
        }
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = mapType,
                mapStyleOptions = if (mapType == MapType.NORMAL) mapStyle else null
            )
        ) {
            lands.forEach { land ->
                if (land.polygonPoints.size < 3) return@forEach

                val fillColor = when (land.status) {
                    LandStatus.AVAILABLE -> colors.primary.copy(alpha = 0.35f)
                    LandStatus.TEMP_LOCK -> colors.tertiary.copy(alpha = 0.35f)
                    LandStatus.BOOKED -> colors.secondary.copy(alpha = 0.35f)
                    LandStatus.SOLD -> colors.error.copy(alpha = 0.35f)
                }

                val center = getPolygonCenter(land)
                val lockHint = if (land.status == LandStatus.TEMP_LOCK) {
                    formatLockRemaining(land.offerLockExpiresAt)?.let { " • $it" }.orEmpty()
                } else {
                    ""
                }

                Polygon(
                    points = land.polygonPoints.map { LatLng(it.first, it.second) },
                    fillColor = fillColor,
                    strokeColor = fillColor.copy(alpha = 0.95f),
                    strokeWidth = 4f,
                    clickable = true,
                    onClick = {
                        selectedLand = land
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(center, 18f),
                                700
                            )
                        }
                    }
                )

                Marker(
                    state = MarkerState(position = center),
                    title = "₹${land.price}",
                    snippet = "${land.area} sqft • ${land.status.name}$lockHint",
                    onClick = {
                        selectedLand = land
                        true
                    }
                )
            }
        }

        FloatingActionButton(
            onClick = { mapType = if (mapType == MapType.NORMAL) MapType.SATELLITE else MapType.NORMAL },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(if (mapType == MapType.NORMAL) "Satellite" else "Map")
        }

        if (isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }

        if (!errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            )
        }

        if (!isLoading && lands.isEmpty()) {
            Text(
                text = "No live lands with boundary found",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }

        selectedLand?.let { land ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("₹${land.price}", fontSize = 20.sp, color = colors.primary)
                    Text("${land.area} sqft", color = colors.onSurfaceVariant)
                    Text(
                        land.status.name,
                        color = when (land.status) {
                            LandStatus.AVAILABLE -> colors.primary
                            LandStatus.TEMP_LOCK -> colors.tertiary
                            LandStatus.BOOKED -> colors.secondary
                            LandStatus.SOLD -> colors.error
                        }
                    )

                    if (land.status == LandStatus.TEMP_LOCK) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatLockRemaining(land.offerLockExpiresAt) ?: "Temporarily locked",
                            color = colors.tertiary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onLandClick(land.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("View More") }
                }
            }
        }
    }
}

fun getPolygonCenter(land: MapLand): LatLng {
    if (land.polygonPoints.isEmpty()) return LatLng(20.2961, 85.8245)
    val lat = land.polygonPoints.map { it.first }.average()
    val lng = land.polygonPoints.map { it.second }.average()
    return LatLng(lat, lng)
}

private fun formatLockRemaining(expiresAt: String?): String? {
    if (expiresAt.isNullOrBlank()) return null
    val expiryMillis = parseIsoToMillis(expiresAt) ?: return null
    val nowMillis = System.currentTimeMillis()
    val diff = expiryMillis - nowMillis
    if (diff <= 0L) return "Lock expired"
    val h = diff / (1000 * 60 * 60)
    val m = (diff / (1000 * 60)) % 60
    return "Lock: ${h}h ${m}m left"
}

private fun parseIsoToMillis(raw: String?): Long? {
    if (raw.isNullOrBlank()) return null
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss"
    )
    patterns.forEach { pattern ->
        runCatching {
            val sdf = SimpleDateFormat(pattern, Locale.US)
            if (pattern == "yyyy-MM-dd'T'HH:mm:ss") {
                sdf.timeZone = TimeZone.getTimeZone("UTC")
            }
            sdf.parse(raw)?.time
        }.getOrNull()?.let { return it }
    }
    return null
}

