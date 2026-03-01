package com.example.myapplication.ui.feature.buyer.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onLandClick: (Int) -> Unit,
    focusLandId: Int? = null
) {
    val lands = viewModel.lands
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    var selectedLand by remember { mutableStateOf<MapLand?>(null) }
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    val scope = rememberCoroutineScope()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(20.2961, 85.8245), 15f)
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
            properties = MapProperties(mapType = mapType)
        ) {
            lands.forEach { land ->
                if (land.polygonPoints.size < 3) return@forEach

                val fillColor = when (land.status) {
                    LandStatus.AVAILABLE -> Color(0x5532CD32)
                    LandStatus.TEMP_LOCK -> Color(0x55FF7043) // lock color
                    LandStatus.BOOKED -> Color(0x55007BFF)
                    LandStatus.SOLD -> Color(0x55FF0000)
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
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("₹${land.price}", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                    Text("${land.area} sqft")
                    Text(
                        land.status.name,
                        color = when (land.status) {
                            LandStatus.AVAILABLE -> Color(0xFF2E7D32)
                            LandStatus.TEMP_LOCK -> Color(0xFFE64A19)
                            LandStatus.BOOKED -> Color(0xFF1565C0)
                            LandStatus.SOLD -> Color(0xFFC62828)
                        }
                    )

                    if (land.status == LandStatus.TEMP_LOCK) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatLockRemaining(land.offerLockExpiresAt) ?: "Temporarily locked",
                            color = Color(0xFFE64A19)
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
    return try {
        val expiry = OffsetDateTime.parse(expiresAt).toInstant()
        val now = Instant.now()
        val diff = Duration.between(now, expiry)
        if (diff.isNegative || diff.isZero) {
            "Lock expired"
        } else {
            val h = diff.toHours()
            val m = diff.toMinutes() % 60
            "Lock: ${h}h ${m}m left"
        }
    } catch (_: DateTimeParseException) {
        null
    }
}
