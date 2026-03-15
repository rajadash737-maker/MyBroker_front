package com.example.myapplication.ui.feature.sell_land.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.myapplication.ui.feature.sell_land.components.SellLandTopBar
import com.example.myapplication.ui.feature.sell_land.viewmodel.SellLandViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@SuppressLint("MissingPermission")
@Composable
fun MapBoundaryScreen(
    viewModel: SellLandViewModel,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val draft by viewModel.draft.collectAsState()
    var points by remember { mutableStateOf(draft.polygon) }
    var isSatellite by remember { mutableStateOf(false) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            points.firstOrNull() ?: LatLng(20.5937, 78.9629),
            16f
        )
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission && points.isEmpty()) {
            val fused = LocationServices.getFusedLocationProviderClient(context)
            fused.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                        LatLng(loc.latitude, loc.longitude),
                        17f
                    )
                }
            }
        }
    }

    val mapProperties = MapProperties(
        mapType = if (isSatellite) MapType.SATELLITE else MapType.NORMAL,
        isMyLocationEnabled = hasLocationPermission
    )

    val mapUiSettings = MapUiSettings(
        myLocationButtonEnabled = hasLocationPermission,
        zoomControlsEnabled = true
    )

    Scaffold(
        topBar = {
            SellLandTopBar(
                title = "Mark Land Boundary",
                onBack = onBack,
                onSaveDraft = {
                    viewModel.updatePolygon(points)
                    viewModel.saveBoundary()
                    viewModel.saveDraft(step = 4)
                }
            )
        },
        bottomBar = {
            BoundaryActionBar(
                isValid = points.size >= 3,
                onClear = { points = emptyList() },
                onConfirm = {
                    viewModel.updatePolygon(points)
                    viewModel.saveBoundary()
                    viewModel.saveDraft(step = 4)
                    onContinue()
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    cameraPositionState = cameraPositionState,
                    properties = mapProperties,
                    uiSettings = mapUiSettings,
                    onMapClick = { latLng ->
                        if (!points.contains(latLng)) points = points + latLng
                    }
                ) {
                    points.forEach { point ->
                        Marker(
                            state = MarkerState(position = point),
                            onClick = {
                                points = points - point
                                true
                            }
                        )
                    }

                    if (points.size >= 3) {
                        Polygon(
                            points = points,
                            fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                            strokeColor = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4f
                        )
                    }
                }

                InstructionCard(
                    pointCount = points.size,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                )

                FloatingActionButton(
                    onClick = { isSatellite = !isSatellite },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Layers, contentDescription = "Toggle Map")
                }
            }
        }
    }
}


@Composable
fun BoundaryActionBar(
    isValid: Boolean,
    onClear: () -> Unit,
    onConfirm: () -> Unit
) {
    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onClear,
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear")
            }

            Button(
                onClick = onConfirm,
                modifier = Modifier.weight(1f),
                enabled = isValid
            ) {
                Text("Confirm Boundary")
            }
        }
    }
}

@Composable
fun InstructionCard(
    pointCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "How to mark boundary",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text("• Tap on map to add points")
            Text("• Minimum 3 points required")
            Text("• Tap marker to remove point")
            Text("• Points selected: $pointCount")
        }
    }
}
